@file:OptIn(ExperimentalMaterial3Api::class)

package fr.simon.marquis.preferencesmanager.ui.applist

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.ui.components.AppBar
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme
import fr.simon.marquis.preferencesmanager.util.PrefManager

@Composable
fun AppListMenu(
    onSearch: () -> Unit,
    onShowSystemApps: () -> Unit,
    onSwitchTheme: () -> Unit,
    onAbout: () -> Unit,
) {
    var isMenuShowing by remember { mutableStateOf(false) }

    IconButton(onClick = onSearch) {
        Icon(Icons.Default.Search, null)
    }
    IconButton(onClick = { isMenuShowing = !isMenuShowing }) {
        Icon(Icons.Default.MoreVert, null)
    }

    DropdownMenu(
        expanded = isMenuShowing,
        onDismissRequest = { isMenuShowing = false }
    ) {
        val menuText = if (PrefManager.showSystemApps) {
            R.string.hide_system_apps
        } else {
            R.string.show_system_apps
        }

        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = menuText))
            },
            leadingIcon = {
                Icon(Icons.Default.SettingsSuggest, contentDescription = null)
            },
            onClick = {
                onShowSystemApps()
                isMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.switch_theme))
            },
            leadingIcon = {
                Icon(Icons.Default.DarkMode, contentDescription = null)
            },
            onClick = {
                onSwitchTheme()
                isMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.show_popup))
            },
            leadingIcon = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            onClick = {
                onAbout()
                isMenuShowing = false
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview_AppListMenu(
    appName: String = stringResource(id = R.string.app_name),

) {
    var textFieldValue: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    AppTheme(isSystemInDarkTheme()) {
        AppBar(
            title = { Text(text = appName) },
            actions = {
                AppListMenu(
                    onSearch = {},
                    onShowSystemApps = {},
                    onSwitchTheme = {},
                    onAbout = {}
                )
            },
            textState = textFieldValue,
            onSearchValueChange = {
                textFieldValue = it
            }
        )
    }
}
