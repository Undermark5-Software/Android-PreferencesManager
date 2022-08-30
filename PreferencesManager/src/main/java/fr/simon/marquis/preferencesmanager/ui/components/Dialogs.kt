package fr.simon.marquis.preferencesmanager.ui.components

import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.vanpra.composematerialdialogs.*
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.model.BackupContainer
import fr.simon.marquis.preferencesmanager.util.PrefManager

@Composable
fun DialogTheme(dialogState: MaterialDialogState, onPositive: () -> Unit) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        // This is heavily coupled with PrefManager.themePreference
        val themeItems = listOf(
            stringResource(id = R.string.light_theme),
            stringResource(id = R.string.dark_theme),
            stringResource(id = R.string.system_theme),
        )
        listItemsSingleChoice(
            list = themeItems,
            initialSelection = PrefManager.themePreference
        ) {
            PrefManager.themePreference = it
            onPositive()
        }
    }
}

@Composable
fun DialogAbout(
    dialogState: MaterialDialogState
) {
    val context = LocalContext.current

    @Suppress("DEPRECATION")
    val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    val appTitle = stringResource(R.string.app_name) + "\n" + appVersion

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(res = R.string.close)
        }
    ) {
        iconTitle(
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_launcher),
                    contentDescription = null,
                )
            },
            text = appTitle
        )
        customView {
            val string = stringResource(id = R.string.about_body)
            AndroidView(
                modifier = Modifier,
                factory = { context -> TextView(context) },
                update = {
                    it.text = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    it.setTextColor(Color.BLACK)
                }
            )
        }
    }
}

@Composable
fun DialogNoRoot(
    dialogState: MaterialDialogState
) {
    val context = LocalContext.current
    MaterialDialog(
        dialogState = dialogState,
        autoDismiss = false,
        buttons = {
            positiveButton(res = R.string.no_root_button) {
                (context as Activity).finish()
            }
        },
    ) {
        iconTitle(
            icon = {
                Image(
                    painterResource(id = R.drawable.ic_action_emo_evil),
                    contentDescription = null,
                )
            },
            textRes = R.string.no_root_title
        )
        message(res = R.string.no_root_message)
    }
}

@Composable
fun DialogRestore(
    dialogState: MaterialDialogState,
    container: BackupContainer?,
    onDelete: (file: String) -> Unit,
    onRestore: (file: String) -> Unit,
) {
    if (container == null)
        return

    if (container.backupList.isEmpty())
        dialogState.hide()

    MaterialDialog(
        dialogState = dialogState,
        autoDismiss = true,
        buttons = {
            positiveButton(res = R.string.dialog_restore)
            negativeButton(res = R.string.dialog_cancel)
        }
    ) {
        title(res = R.string.pick_restore)
        listItemsCustomSingleChoice(
            list = container.backupList.map { it.timeSinceBackup() },
            onChoiceDelete = {
                val file = container.backupList[it].backupFile
                onDelete(file)
            },
            onChoiceChange = {
                val file = container.backupList[it].backupFile
                onRestore(file)
            }
        )
    }
}

/**
 * Custom "Single Item List" but with a custom view.
 */
@Suppress("ComposableNaming")
@Composable
fun MaterialDialogScope.listItemsCustomSingleChoice(
    list: List<String>,
    state: LazyListState = rememberLazyListState(),
    disabledIndices: Set<Int> = setOf(),
    initialSelection: Int? = null,
    waitForPositiveButton: Boolean = true,
    onChoiceDelete: (selected: Int) -> Unit = {},
    onChoiceChange: (selected: Int) -> Unit = {},
) {
    var selectedItem by remember { mutableStateOf(initialSelection) }
    PositiveButtonEnabled(valid = selectedItem != null) {}

    if (waitForPositiveButton) {
        DialogCallback { onChoiceChange(selectedItem!!) }
    }

    val onSelect = { index: Int ->
        if (index !in disabledIndices) {
            selectedItem = index

            if (!waitForPositiveButton) {
                onChoiceChange(selectedItem!!)
            }
        }
    }

    val onDelete = { index: Int ->
        onChoiceDelete(index)
    }

    val isEnabled = remember(disabledIndices) { { index: Int -> index !in disabledIndices } }
    listItems(
        list = list,
        state = state,
        closeOnClick = false,
        onClick = { index, _ -> onSelect(index) },
        isEnabled = isEnabled
    ) { index, item ->
        val enabled = remember(disabledIndices) { index !in disabledIndices }
        val selected = remember(selectedItem) { index == selectedItem }

        CustomViewChoiceItem(
            item = item,
            index = index,
            selected = selected,
            enabled = enabled,
            onSelect = onSelect,
            onDelete = onDelete,
        )
    }
}

@Composable
private fun CustomViewChoiceItem(
    item: String,
    index: Int,
    selected: Boolean,
    enabled: Boolean,
    onSelect: (index: Int) -> Unit,
    onDelete: (index: Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                if (enabled) {
                    onSelect(index)
                }
            },
            enabled = enabled
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
        )
        Text(
            item,
            modifier = Modifier.weight(1f),
            color = if (enabled) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
            },
            style = MaterialTheme.typography.body1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
        )
        IconButton(
            onClick = {
                if (enabled) {
                    onDelete(index)
                }
            },
            enabled = enabled
        ) {
            Icon(Icons.Default.Delete, null)
        }
    }
}

@Preview
@Composable
private fun Preview_RestoreItem() {
    CustomViewChoiceItem(
        item = "00/00/00 12:34:56PM",
        index = 1,
        selected = true,
        enabled = true,
        onDelete = {},
        onSelect = {}
    )
}
