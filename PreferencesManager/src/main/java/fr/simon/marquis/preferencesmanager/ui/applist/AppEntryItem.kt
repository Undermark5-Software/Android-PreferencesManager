@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package fr.simon.marquis.preferencesmanager.ui.applist

import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import fr.simon.marquis.preferencesmanager.model.AppEntry
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme

@Composable
fun AppEntryItem(
    modifier: Modifier = Modifier,
    entry: AppEntry,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .height(72.dp)
            .fillMaxWidth(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        headlineText = {
            Text(
                text = entry.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            SubcomposeAsyncImage(
                modifier = Modifier.size(40.dp),
                model = entry.iconUri,
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                },
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview_AppEntryItem() {
    val appInfo = ApplicationInfo().apply {
        packageName = "Some Cool App"
        name = "Some Cool App"
        sourceDir = ""
    }
    val entry = AppEntry(
        applicationInfo = appInfo,
        label = appInfo.packageName
    )

    AppTheme(isSystemInDarkTheme()) {
        AppEntryItem(entry = entry, onClick = {}, onLongClick = {})
    }
}
