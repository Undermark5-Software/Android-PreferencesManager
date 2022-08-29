@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package fr.simon.marquis.preferencesmanager.ui.preferences

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme

@Composable
fun PreferenceFragment(
    list: MutableList<MutableMap.MutableEntry<Any, Any>>?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior(topBarState) }
    val scrollState = rememberLazyListState()

    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Box(contentAlignment = Alignment.Center) {
            PreferenceEmptyView(isEmpty = list.isNullOrEmpty())

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = scrollState,
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(list.orEmpty(), key = { it.key }) { entry ->
                    PreferencesEntryItem(
                        modifier = Modifier.animateItemPlacement(),
                        item = entry,
                        onClick = onClick,
                        onLongClick = onLongClick
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
            Image(
                painter = painterResource(id = R.drawable.empty_view),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(id = R.string.empty_preference_file_valid))
        }
    }
}

@Preview
@Composable
private fun Preview_PreferenceEmptyView() {
    AppTheme {
        PreferenceEmptyView(true)
    }
}
