package fr.simon.marquis.preferencesmanager.ui.preferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme
import fr.simon.marquis.preferencesmanager.util.PrefManager

enum class EPreferencesAdd {
    INTEGER, STRING, BOOLEAN, FLOAT, LONG, STRINGSET
}

enum class EPreferencesOverflow {
    EDIT, SHORTCUT, BACKUP, RESTORE
}

enum class EPreferencesSort {
    ALPHANUMERIC, TYPE_AND_ALPHANUMERIC
}

@Composable
fun PreferencesMenu(
    onSearch: () -> Unit,
    onAddClicked: (value: EPreferencesAdd) -> Unit,
    onOverflowClicked: (value: EPreferencesOverflow) -> Unit,
    onSortClicked: (value: EPreferencesSort) -> Unit
) {
    var isAddMenuShowing by remember { mutableStateOf(false) }
    var isOverflowMenuSowing by remember { mutableStateOf(false) }
    var isSortMenuShowing by remember { mutableStateOf(false) }

    IconButton(onClick = onSearch) {
        Icon(Icons.Default.Search, null)
    }
    IconButton(onClick = { isAddMenuShowing = !isAddMenuShowing }) {
        Icon(Icons.Default.Add, null)
    }
    IconButton(onClick = { isOverflowMenuSowing = !isOverflowMenuSowing }) {
        Icon(Icons.Default.MoreVert, null)
    }

    DropdownMenu(
        expanded = isAddMenuShowing,
        onDismissRequest = { isAddMenuShowing = false }
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_add_int)) },
            leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null)
            },
            onClick = {
                onAddClicked(EPreferencesAdd.INTEGER)
                isAddMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_add_string)) },
            leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null)
            },
            onClick = {
                onAddClicked(EPreferencesAdd.STRING)
                isAddMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_add_boolean)) },
            leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null)
            },
            onClick = {
                onAddClicked(EPreferencesAdd.BOOLEAN)
                isAddMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_add_float)) },
            leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null)
            },
            onClick = {
                onAddClicked(EPreferencesAdd.FLOAT)
                isAddMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_add_long)) },
            leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null)
            },
            onClick = {
                onAddClicked(EPreferencesAdd.LONG)
                isAddMenuShowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_add_stringset)) },
            leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null)
            },
            onClick = {
                onAddClicked(EPreferencesAdd.STRINGSET)
                isAddMenuShowing = false
            }
        )
    }

    DropdownMenu(
        expanded = isOverflowMenuSowing,
        onDismissRequest = { isOverflowMenuSowing = false }
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_sort)) },
            leadingIcon = {
                Icon(Icons.Default.Sort, contentDescription = null)
            },
            trailingIcon = {
                Icon(Icons.Default.ArrowRight, contentDescription = null)
            },
            onClick = {
                isSortMenuShowing = true
                isOverflowMenuSowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_direct_edit)) },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null)
            },
            onClick = {
                onOverflowClicked(EPreferencesOverflow.EDIT)
                isOverflowMenuSowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_shortcut)) },
            leadingIcon = {
                Icon(Icons.Default.AppShortcut, contentDescription = null)
            },
            onClick = {
                onOverflowClicked(EPreferencesOverflow.SHORTCUT)
                isOverflowMenuSowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_backup_file)) },
            leadingIcon = {
                Icon(Icons.Default.Download, contentDescription = null)
            },
            onClick = {
                onOverflowClicked(EPreferencesOverflow.BACKUP)
                isOverflowMenuSowing = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.action_restore_file)) },
            leadingIcon = {
                Icon(Icons.Default.Restore, contentDescription = null)
            },
            onClick = {
                onOverflowClicked(EPreferencesOverflow.RESTORE)
                isOverflowMenuSowing = false
            }
        )
    }

    DropdownMenu(
        expanded = isSortMenuShowing,
        onDismissRequest = { isSortMenuShowing = false }
    ) {
        DropdownMenuItem(
            enabled = PrefManager.keySortType == EPreferencesSort.TYPE_AND_ALPHANUMERIC.ordinal,
            text = { Text(text = stringResource(id = R.string.action_sort_alpha)) },
            leadingIcon = {
                Icon(Icons.Default.SortByAlpha, contentDescription = null)
            },
            onClick = {
                onSortClicked(EPreferencesSort.ALPHANUMERIC)
                isSortMenuShowing = false
            }
        )
        DropdownMenuItem(
            enabled = PrefManager.keySortType == EPreferencesSort.ALPHANUMERIC.ordinal,
            text = { Text(text = stringResource(id = R.string.action_sort_type)) },
            leadingIcon = {
                Icon(Icons.Default.Category, contentDescription = null)
            },
            onClick = {
                onSortClicked(EPreferencesSort.TYPE_AND_ALPHANUMERIC)
                isSortMenuShowing = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview_PreferencesMenu(
    viewModel: PreferencesViewModel = viewModel(),
    title: String = "Some Cool App",
    pkgName: String = "com.some.cool.app"
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior(topBarState) }

    AppTheme {
        PreferencesAppBar(
            scrollBehavior = scrollBehavior,
            viewModel = viewModel,
            pkgTitle = title,
            pkgName = pkgName,
            iconUri = null,
            onBackPressed = {},
            onAddClicked = {},
            onOverflowClicked = {},
            onSortClicked = {},
        )
    }
}