package fr.simon.marquis.preferencesmanager.ui.editor

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.simon.marquis.preferencesmanager.model.EFontSize
import fr.simon.marquis.preferencesmanager.model.EFontTheme
import fr.simon.marquis.preferencesmanager.model.PreferenceFile
import fr.simon.marquis.preferencesmanager.model.XmlColorTheme
import fr.simon.marquis.preferencesmanager.util.PrefManager
import fr.simon.marquis.preferencesmanager.util.Utils
import java.util.*
import kotlinx.coroutines.launch
import timber.log.Timber

data class FileEditorState(
    val textChanged: Boolean = false,
    val file: String? = null,
    val title: String? = null,
    val pkgName: String? = null,
    val editText: String? = null,
    val fontSize: EFontSize = EFontSize.getBySize(PrefManager.keyFontSize),
    val fontTheme: EFontTheme = EFontTheme.getByTheme(PrefManager.keyFontTheme),
    val xmlColorTheme: XmlColorTheme? = null,
)

class FileEditorViewModel : ViewModel() {

    private val _uiState = mutableStateOf(FileEditorState())
    val uiState: State<FileEditorState> = _uiState

    init {
        val xmlColorTheme = XmlColorTheme.createTheme(uiState.value.fontTheme)
        _uiState.value = uiState.value.copy(xmlColorTheme = xmlColorTheme)
    }

    fun setTextChanged(value: String) {
        _uiState.value = uiState.value.copy(textChanged = true, editText = value)
    }

    fun setPackageInfo(file: String?, title: String?, pkgName: String?) {
        _uiState.value = uiState.value.copy(
            file = file,
            title = title,
            pkgName = pkgName,
            editText = Utils.readFile(file!!)
        )
    }

    fun setFontSize(value: EFontSize) {
        _uiState.value = uiState.value.copy(fontSize = value)

        PrefManager.keyFontSize = uiState.value.fontSize.size
    }

    fun setFontTheme(value: EFontTheme) {
        val xmlColorTheme = XmlColorTheme.createTheme(value)
        _uiState.value = uiState.value.copy(fontTheme = value, xmlColorTheme = xmlColorTheme)

        PrefManager.keyFontTheme = uiState.value.fontTheme.ordinal
    }

    fun saveChanges(context: Context): Boolean {
        val file = uiState.value.file!!
        val pkgName = uiState.value.pkgName!!
        val pref = PreferenceFile.fromXml(uiState.value.editText ?: "")

        backupFile(context, pkgName, file)

        return Utils.savePreferences(context, pref, file, pkgName)
    }

    private fun backupFile(context: Context, pkgName: String, file: String) {
        viewModelScope.launch {
            val date = Date()
            val result = Utils.backupFile(context, date.time, pkgName, file)
            Timber.d("Backup: $result")
        }
    }
}
