package fr.simon.marquis.preferencesmanager.ui.applist

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import fr.simon.marquis.preferencesmanager.model.AppEntry
import fr.simon.marquis.preferencesmanager.model.ThemeSettingsImpl
import fr.simon.marquis.preferencesmanager.util.Utils
import fr.simon.marquis.preferencesmanager.util.executeAsyncTask
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

data class AppListState(
    val isLoading: Boolean = false,
    val isRootGranted: Boolean = false,
    val isSearching: Boolean = false,
    val appList: List<AppEntry> = listOf(),
    val filteredAppList: List<AppEntry> = listOf()
)

class AppListViewModel : ViewModel() {

    var themeSettings: ThemeSettingsImpl
        private set

    private val _uiState = mutableStateOf(AppListState())
    val uiState: State<AppListState> = _uiState

    private val _searchText = MutableStateFlow(TextFieldValue(""))
    val searchText: MutableStateFlow<TextFieldValue> = _searchText

    init {
        viewModelScope.launch {
            searchText.collectLatest {
                searchText(it.text)
            }
        }

        themeSettings = ThemeSettingsImpl()
    }

    fun setIsSearching(value: Boolean) {
        _uiState.value = uiState.value.copy(isSearching = value)
    }

    fun checkRoot() {
        _uiState.value = uiState.value.copy(isRootGranted = Shell.isAppGrantedRoot() ?: false)

        Timber.i("Root access is ${uiState.value.isRootGranted}")
    }

    private fun searchText(value: String) {
        val isSearching = uiState.value.isSearching && searchText.value.text.isNotEmpty()
        val list = if (isSearching) {
            uiState.value.appList.filter {
                it.label
                    .lowercase(Locale.getDefault())
                    .contains(value.lowercase(Locale.getDefault()))
            }
        } else {
            uiState.value.appList
        }

        _uiState.value = uiState.value.copy(filteredAppList = list)
    }

    fun startTask(context: Context) {
        viewModelScope.executeAsyncTask(
            onPreExecute = {
                _uiState.value = uiState.value.copy(isLoading = true)
            },
            doInBackground = { _: suspend (progress: Int) -> Unit ->
                Utils.getApplications(context)
            },
            onPostExecute = {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    appList = it,
                    filteredAppList = it
                )
            },
            onProgressUpdate = {
            }
        )
    }
}
