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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)

package fr.simon.marquis.preferencesmanager.ui.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.model.*
import fr.simon.marquis.preferencesmanager.ui.components.AppBar
import fr.simon.marquis.preferencesmanager.ui.components.DialogRestore
import fr.simon.marquis.preferencesmanager.ui.components.showToast
import fr.simon.marquis.preferencesmanager.ui.editor.FileEditorActivity
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme
import fr.simon.marquis.preferencesmanager.util.PrefManager
import fr.simon.marquis.preferencesmanager.util.Utils
import fr.simon.marquis.preferencesmanager.util.getParcelable
import timber.log.Timber

const val KEY_ICON_URI = "KEY_ICON_URI"
const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"
const val KEY_TITLE = "KEY_TITLE"
const val KEY_FILE = "KEY_FILE"

// TODO: Add cab selection again, or some multi select option.
// TODO: Add columns to list if either a tablet or landscape mode.

class PreferencesActivity : ComponentActivity() {

    private val viewModel: PreferencesViewModel by viewModels()
    private var files: List<String>? = null

    private var resultFileEdit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // TODO: This should reflect any changes once were back.
        viewModel.getTabsAndPreferences()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val intent = intent.extras
        if (intent == null) {
            finish()
            return
        }

        Timber.i("onCreate")
        setContent {
            val context = LocalContext.current
            val uiState by viewModel::uiState

            val topBarState = rememberTopAppBarState()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
            val pagerState = rememberPagerState()
            val windowInset = Modifier
                .statusBarsPadding()
                .windowInsetsPadding(
                    WindowInsets
                        .navigationBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )

            LaunchedEffect(Unit) {
                val pkgTitle = intent.getString(KEY_TITLE)!!
                val pkgName = intent.getString(KEY_PACKAGE_NAME)!!
                val pkgIcon = getParcelable(intent, KEY_ICON_URI, Uri::class.java)
                viewModel.setPackageInfo(pkgTitle, pkgName, pkgIcon)
                viewModel.getTabsAndPreferences()
            }

            LaunchedEffect(pagerState.isScrollInProgress) {
                viewModel.clearRestoreData()
            }

            val restoreDialogState = rememberMaterialDialogState()
            DialogRestore(
                dialogState = restoreDialogState,
                container = uiState.restoreData,
                onRestore = { fileName ->
                    val pkgName = uiState.pkgName
                    viewModel.performFileRestore(context, fileName, pkgName)
                },
                onDelete = { fileName ->
                    val currentPage = pagerState.currentPage
                    val currentTab = uiState.tabList[currentPage]
                    val file = currentTab.preferenceFile!!.file

                    viewModel.deleteFile(context, fileName, file)
                }
            )

            // val editDialogState = rememberMaterialDialogState()
            // DialogEditPreference(
            //     dialogState = editDialogState,
            //     preferenceFile =
            // )

            val isDarkTheme = when (EAppTheme.getAppTheme(PrefManager.themePreference)) {
                EAppTheme.AUTO -> isSystemInDarkTheme()
                EAppTheme.DAY -> false
                EAppTheme.NIGHT -> true
            }
            AppTheme(isDarkTheme = isDarkTheme) {
                Scaffold(
                    modifier = windowInset,
                    topBar = {
                        PreferencesAppBar(
                            scrollBehavior = scrollBehavior,
                            uiState = uiState,
                            onBackPressed = {
                                @Suppress("DEPRECATION")
                                onBackPressed()
                            },
                            onAddClicked = {
                                /* TODO */
                            },
                            onOverflowClicked = {
                                val currentPage = pagerState.currentPage
                                val currentTab = uiState.tabList[currentPage]
                                val file = currentTab.preferenceFile!!.file

                                when (it) {
                                    EPreferencesOverflow.EDIT -> editFile(file)
                                    EPreferencesOverflow.FAV -> {
                                        val pkgName = uiState.pkgName
                                        val favorite = Utils.isFavorite(pkgName)
                                        Utils.setFavorite(pkgName, !favorite)
                                    }
                                    EPreferencesOverflow.BACKUP -> {
                                        val pkgName = uiState.pkgName
                                        viewModel.backupFile(this, pkgName, file)
                                    }
                                    EPreferencesOverflow.RESTORE -> {
                                        viewModel.findFilesToRestore(this, file) { hasResult ->
                                            if (hasResult) {
                                                restoreDialogState.show()
                                            } else {
                                                context.showToast(res = R.string.empty_restore)
                                            }
                                        }
                                    }
                                }
                            },
                            onSortClicked = {
                                PrefManager.keySortType = it.ordinal
                                viewModel.getTabsAndPreferences()
                            },
                            onSearch = viewModel::onSearch,
                            onSearchClose = viewModel::onSearchClose,
                            onSearchValueChange = viewModel::onSearchValueChange,
                        )
                    }
                ) { paddingValues ->
                    val haptic = LocalHapticFeedback.current

                    TabLayout(
                        paddingValues = paddingValues,
                        scrollBehavior = scrollBehavior,
                        pagerState = pagerState,
                        uiState = viewModel.uiState,
                        onClick = { preferenceFile, item ->
                            //Show dialog
                        },
                        onLongClick = { _, _ ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            // TODO multi select.
                        }
                    )
                }
            }
        }
    }

    private fun editFile(file: String) {
        val intent = Intent(this, FileEditorActivity::class.java).apply {
            putExtra(KEY_FILE, file)
            putExtra(KEY_ICON_URI, viewModel.uiState.pkgIcon)
            putExtra(KEY_PACKAGE_NAME, viewModel.uiState.pkgName)
        }

        resultFileEdit.launch(intent)
    }
}

@Composable
fun PreferencesAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    uiState: PreferencesState,
    onBackPressed: () -> Unit,
    onAddClicked: (value: EPreferencesAdd) -> Unit,
    onOverflowClicked: (value: EPreferencesOverflow) -> Unit,
    onSortClicked: (value: EPreferencesSort) -> Unit,
    onSearch: () -> Unit,
    onSearchClose: () -> Unit,
    onSearchValueChange: (TextFieldValue) -> Unit
) {
    AppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                SubcomposeAsyncImage(
                    modifier = Modifier.size(48.dp),
                    model = uiState.pkgIcon,
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
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = uiState.pkgTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = uiState.pkgName,
                        maxLines = 1,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            PreferencesMenu(
                isFavorite = Utils.isFavorite(uiState.pkgName),
                onSearch = onSearch,
                onAddClicked = onAddClicked,
                onOverflowClicked = onOverflowClicked,
                onSortClicked = onSortClicked
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        textState = uiState.searchText,
        isSearching = uiState.isSearching,
        onSearchClose = onSearchClose,
        onSearchValueChange = onSearchValueChange
    )
}

@Composable
fun TabLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    pagerState: PagerState,
    uiState: PreferencesState,
    onClick: PreferencesEntryItemClickHandler,
    onLongClick: PreferencesEntryItemClickHandler
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            val tabs = uiState.tabList

            if (tabs.isEmpty() && !uiState.isLoading) {
                PreferenceEmptyView()
            } else {
                Tabs(
                    scrollBehavior = scrollBehavior,
                    tabs = tabs,
                    pagerState = pagerState,
                )
                TabsContent(
                    tabs = tabs,
                    pagerState = pagerState,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(92.dp))
        }
    }
}

@Composable
fun PreferenceEmptyView() {
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
        Text(text = stringResource(id = R.string.empty_preference_application))
    }
}
