/*
 * Copyright (C) 2013 Simon Marquis (http://www.simon-marquis.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package fr.simon.marquis.preferencesmanager.ui.applist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.model.AppEntry
import fr.simon.marquis.preferencesmanager.model.EAppTheme
import fr.simon.marquis.preferencesmanager.model.ThemeSettings
import fr.simon.marquis.preferencesmanager.ui.components.*
import fr.simon.marquis.preferencesmanager.ui.preferences.KEY_ICON_URI
import fr.simon.marquis.preferencesmanager.ui.preferences.KEY_PACKAGE_NAME
import fr.simon.marquis.preferencesmanager.ui.preferences.KEY_TITLE
import fr.simon.marquis.preferencesmanager.ui.preferences.PreferencesActivity
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme
import fr.simon.marquis.preferencesmanager.util.PrefManager
import fr.simon.marquis.preferencesmanager.util.Utils
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.reflect.KFunction0

class AppListActivity : ComponentActivity() {

    private val viewModel: AppListViewModel by viewModels()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.run {
            if (uiState.isRootGranted)
                startTask(this@AppListActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        viewModel.checkRoot()

        if (savedInstanceState == null || Utils.previousApps == null) {
            viewModel.run {
                if (uiState.isRootGranted)
                    startTask(this@AppListActivity)
            }
        }

        Timber.i("onCreate")
        setContent {
            val uiState by viewModel::uiState

            val context = LocalContext.current
            val haptic = LocalHapticFeedback.current
            val scope = rememberCoroutineScope()
            val topBarState = rememberTopAppBarState()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

            val dialogNoRootState = rememberMaterialDialogState()
            DialogNoRoot(dialogState = dialogNoRootState)

            val theme = viewModel.themeSettings.themeStream.collectAsState()
            val isDarkTheme = when (theme.value) {
                EAppTheme.AUTO -> isSystemInDarkTheme()
                EAppTheme.DAY -> false
                EAppTheme.NIGHT -> true
            }

            val windowInset = Modifier
                .statusBarsPadding()
                .windowInsetsPadding(
                    WindowInsets
                        .navigationBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )

            if (!uiState.isRootGranted) {
                LaunchedEffect(Unit) {
                    scope.launch {
                        dialogNoRootState.show()
                    }
                }
            }
            val ctx = LocalContext.current
            AppTheme(isDarkTheme = isDarkTheme) {
                Scaffold(
                    modifier = windowInset,
                    topBar = {
                        AppListAppBar(
                            scrollBehavior = scrollBehavior,
                            uiState = viewModel.uiState,
                            themeSettings = viewModel.themeSettings,
                            onSearch = viewModel::onSearch,
                            onSearchClose = viewModel::onSearchClose,
                            onSearchValueChange = viewModel::onSearchValueChange
                        ) {
                            viewModel.onShowSystemApps(ctx)
                        }
                    }
                ) { paddingValues ->
                    AppListLayout(
                        paddingValues = paddingValues,
                        scrollBehavior = scrollBehavior,
                        uiState = viewModel.uiState,
                        onClick = { entry ->
                            if (!uiState.isRootGranted) {
                                Timber.e("We don't have root to continue!")
                            } else {
                                val intent =
                                    Intent(context, PreferencesActivity::class.java).apply {
                                        putExtra(KEY_ICON_URI, entry.iconUri)
                                        putExtra(
                                            KEY_PACKAGE_NAME,
                                            entry.applicationInfo.packageName
                                        )
                                        putExtra(KEY_TITLE, entry.label)
                                    }

                                activityResultLauncher.launch(intent)
                            }
                        },
                        onLongClick = { entry ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                addCategory(Intent.CATEGORY_DEFAULT)
                                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                data = Uri.fromParts(
                                    "package",
                                    entry.applicationInfo.packageName,
                                    null
                                )
                            }

                            activityResultLauncher.launch(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppListAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    uiState: AppListState,
//    viewModel: AppListViewModel,
    themeSettings: ThemeSettings,
    onSearch: () -> Unit,
    onSearchClose: () -> Unit,
    onSearchValueChange: (TextFieldValue) -> Unit,
    onShowSystemApps: () -> Unit,
) {
    val context = LocalContext.current

    val dialogThemeState = rememberMaterialDialogState()
    DialogTheme(dialogThemeState, PrefManager.themePreference, themeSettings)

    val dialogAboutState = rememberMaterialDialogState()
    DialogAbout(dialogState = dialogAboutState)

    AppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            AppListMenu(
                onSearch = onSearch,
                onShowSystemApps = onShowSystemApps,
                onSwitchTheme = { dialogThemeState.show() },
                onAbout = { dialogAboutState.show() }
            )
        },
        textState = uiState.searchText,
        isSearching = uiState.isSearching,
        onSearchClose = onSearchClose,
        onSearchValueChange = onSearchValueChange
    )
}

@Composable
private fun AppListLayout(
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    uiState: AppListState,
    onClick: (entry: AppEntry) -> Unit,
    onLongClick: (entry: AppEntry) -> Unit,
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = scrollState,
                contentPadding = PaddingValues(bottom = 112.dp)
            ) {
                val items = uiState.filteredAppList.groupBy { it.headerChar }
                items.forEach { (letter, item) ->
                    stickyHeader {
                        AppEntryHeader(letter = letter)
                    }
                    items(item) { entry ->
                        AppEntryItem(
                            modifier = Modifier.animateItemPlacement(),
                            entry = entry,
                            onClick = { onClick(entry) },
                            onLongClick = { onLongClick(entry) }
                        )
                    }
                }
            }
        }

        ScrollUpLayout(
            modifier = Modifier.align(Alignment.BottomCenter),
            scrollState = scrollState,
        )
    }
}

@Composable
private fun ScrollUpLayout(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
) {
    val scope = rememberCoroutineScope()
    val showBackUpButton = remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }
    ScrollBackUp(
        modifier = Modifier
            .navigationBarsPadding()
            .then(modifier),
        enabled = showBackUpButton.value,
        onClicked = {
            scope.launch {
                scrollState.animateScrollToItem(0)
            }
        }
    )
}
