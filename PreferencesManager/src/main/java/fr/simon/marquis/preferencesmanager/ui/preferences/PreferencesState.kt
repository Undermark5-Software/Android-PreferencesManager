package fr.simon.marquis.preferencesmanager.ui.preferences

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import fr.simon.marquis.preferencesmanager.model.BackupContainer

data class PreferencesState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isFavorite: Boolean = false,
    val pkgIcon: Uri? = null,
    val pkgName: String = "",
    val pkgTitle: String = "",
    val restoreData: BackupContainer? = null,
    val tabList: List<TabItem> = listOf(),
    val searchText: TextFieldValue = TextFieldValue("")
)