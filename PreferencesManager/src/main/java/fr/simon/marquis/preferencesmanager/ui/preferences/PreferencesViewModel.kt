package fr.simon.marquis.preferencesmanager.ui.preferences

import android.content.Context
import android.net.Uri
import android.util.Pair
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.simon.marquis.preferencesmanager.model.BackupContainer
import fr.simon.marquis.preferencesmanager.model.PreferenceFile
import fr.simon.marquis.preferencesmanager.util.Utils
import fr.simon.marquis.preferencesmanager.util.executeAsyncTask
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class PreferencesState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val tabList: List<TabItem> = listOf(),
    val backupContainer: List<BackupContainer> = listOf(),
    val pkgTitle: String = "",
    val pkgName: String = "",
    val pkgIcon: Uri? = null,
    val restoreData: BackupContainer? = null
)

class PreferencesViewModel : ViewModel() {

    private val _uiState = mutableStateOf(PreferencesState())
    val uiState: State<PreferencesState> = _uiState

    private val _searchText = MutableStateFlow(TextFieldValue(""))
    val searchText: MutableStateFlow<TextFieldValue> = _searchText

    fun setPackageInfo(pkgTitle: String, pkgName: String, pkgIcon: Uri?) {
        _uiState.value = uiState.value.copy(
            pkgTitle = pkgTitle,
            pkgName = pkgName,
            pkgIcon = pkgIcon
        )
    }

    fun setIsSearching(value: Boolean) {
        _uiState.value = uiState.value.copy(isSearching = value)
    }

    fun setRestoreData(container: BackupContainer) {
        _uiState.value = uiState.value.copy(restoreData = container)
    }

    fun clearRestoreData() {
        _uiState.value = uiState.value.copy(restoreData = null)
    }

    fun getTabsAndPreferences() {
        viewModelScope.executeAsyncTask(
            onPreExecute = {
                _uiState.value = uiState.value.copy(isLoading = true)
            },
            doInBackground = { _: suspend (progress: Int) -> Unit ->
                val xmlFiles = Utils.findXmlFiles(uiState.value.pkgName)
                val xmlPreferences = xmlFiles.map { file ->
                    val content = Utils.readFile(file)
                    PreferenceFile.fromXml(content, file)
                }

                Pair(xmlFiles, xmlPreferences)
            },
            onPostExecute = {
                val tabList = it.first.mapIndexed { index, string ->
                    TabItem(pkgName = string, preferenceFile = it.second[index])
                }
                _uiState.value = uiState.value.copy(tabList = tabList, isLoading = false)
            },
            onProgressUpdate = {
            }
        )
    }

    fun backupFile(context: Context, pkgName: String, file: String) {
        viewModelScope.launch {
            val date = Date()
            val result = Utils.backupFile(context, date.time, pkgName, file)
            Timber.d("Backup: $result")
        }
    }
}
