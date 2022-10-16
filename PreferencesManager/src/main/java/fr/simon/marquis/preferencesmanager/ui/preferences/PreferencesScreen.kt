@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package fr.simon.marquis.preferencesmanager.ui.preferences

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.model.KeyValueIndex
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme

@Composable
fun PreferenceFragment(
    list: List<KeyValueIndex>,
    onClickGenerator: (KeyValueIndex) -> () -> Unit,
    onLongClickGenerator: (KeyValueIndex) -> () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scrollState = rememberLazyListState()

    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Box(contentAlignment = Alignment.Center) {
            PreferenceEmptyView(isEmpty = list.isEmpty())

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = scrollState,
                contentPadding = WindowInsets.navigationBars.asPaddingValues()
            ) {
                itemsIndexed(
                    items = list,
                    key = { _, item ->
                        item.key
                    }
                ) { index, item ->

                    item.index = index
                    PreferencesEntryItem(
                        modifier = Modifier.animateItemPlacement(),
                        item = item,
                        onClick = onClickGenerator(item),
                        onLongClick = onLongClickGenerator(item)
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceEmptyView(isEmpty: Boolean) {
    if (isEmpty) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.empty_view),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(id = R.string.empty_preference_file_valid))
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun Preview_PreferenceEmptyView() {
    AppTheme(isSystemInDarkTheme()) {
        PreferenceEmptyView(true)
    }
}
