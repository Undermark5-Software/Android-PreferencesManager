@file:OptIn(ExperimentalMaterial3Api::class)

package fr.simon.marquis.preferencesmanager.ui.editor

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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.model.EAppTheme
import fr.simon.marquis.preferencesmanager.model.EFontTheme
import fr.simon.marquis.preferencesmanager.model.XmlColorTheme
import fr.simon.marquis.preferencesmanager.ui.components.AppBar
import fr.simon.marquis.preferencesmanager.ui.components.DialogSaveChanges
import fr.simon.marquis.preferencesmanager.ui.components.NavigationBack
import fr.simon.marquis.preferencesmanager.ui.components.showToast
import fr.simon.marquis.preferencesmanager.ui.preferences.KEY_FILE
import fr.simon.marquis.preferencesmanager.ui.preferences.KEY_PACKAGE_NAME
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme
import fr.simon.marquis.preferencesmanager.util.PrefManager
import timber.log.Timber

class FileEditorActivity : ComponentActivity() {

    private val viewModel: FileEditorViewModel by viewModels()

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
            val uiState by viewModel.uiState

            val context = LocalContext.current
            val topBarState = rememberTopAppBarState()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

            LaunchedEffect(Unit) {
                val file = intent.getString(KEY_FILE)
                val fileSeparator = System.getProperty("file.separator")
                val title = file?.let {
                    file.substring(file.lastIndexOf(fileSeparator!!) + 1)
                }
                val pkgName = intent.getString(KEY_PACKAGE_NAME)

                viewModel.setPackageInfo(file, title, pkgName)
            }

            val windowInset = Modifier
                .systemBarsPadding()
                .windowInsetsPadding(
                    WindowInsets
                        .systemBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )

            val saveChangesState = rememberMaterialDialogState()
            DialogSaveChanges(
                saveChangesState = saveChangesState,
                onPositive = {
                    if (viewModel.saveChanges(this@FileEditorActivity)) {
                        showToast(R.string.save_success)
                        setResult(ComponentActivity.RESULT_OK)
                        finish()
                    } else {
                        showToast(R.string.save_fail)
                    }
                },
                onNegative = {
                    @Suppress("DEPRECATION")
                    onBackPressed()
                }
            )

            val isDarkTheme = when (EAppTheme.getAppTheme(PrefManager.themePreference)) {
                EAppTheme.AUTO -> isSystemInDarkTheme()
                EAppTheme.DAY -> false
                EAppTheme.NIGHT -> true
            }
            AppTheme(isDarkTheme = isDarkTheme) {
                Scaffold(
                    modifier = windowInset,
                    topBar = {
                        AppBar(
                            scrollBehavior = scrollBehavior,
                            title = {
                                Text(
                                    text = uiState.title ?: "[Empty Package Name]",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                NavigationBack {
                                    if (uiState.textChanged) {
                                        saveChangesState.show()
                                        return@NavigationBack
                                    }

                                    @Suppress("DEPRECATION")
                                    onBackPressed()
                                }
                            },
                            actions = {
                                FileEditorMenu(
                                    onSave = {
                                        if (!uiState.textChanged) {
                                            showToast(R.string.toast_no_changes)
                                            return@FileEditorMenu
                                        }

                                        val saveChanges = viewModel.saveChanges(context)
                                        if (saveChanges)
                                            showToast(R.string.save_success)
                                        else
                                            showToast(R.string.save_fail)
                                    },
                                    onFontTheme = {
                                        viewModel.setFontTheme(it)
                                    },
                                    onFontSize = {
                                        viewModel.setFontSize(it)
                                    }
                                )
                            },
                            onSearchValueChange = { }
                        )
                    }
                ) { paddingValues ->
                    FileEditorLayout(
                        paddingValues = paddingValues,
                        scrollBehavior = scrollBehavior,
                        xmlColorTheme = uiState.xmlColorTheme!!,
                        textSize = uiState.fontSize.size,
                        text = uiState.editText ?: "[This should not be empty!]",
                        onValueChange = {
                            viewModel.setTextChanged(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FileEditorLayout(
    paddingValues: PaddingValues,
    xmlColorTheme: XmlColorTheme,
    textSize: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    text: String,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .imePadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        TextField(
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle.Default.copy(fontSize = textSize.sp),
            value = text,
            onValueChange = onValueChange,
            visualTransformation = XmlTransformation(xmlColorTheme)
        )
    }
}

@Preview
@Composable
private fun Preview_FileEditorLayout() {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    val xmlColorTheme = XmlColorTheme.createTheme(EFontTheme.ECLIPSE)

    AppTheme(isSystemInDarkTheme()) {
        FileEditorLayout(
            paddingValues = PaddingValues(),
            xmlColorTheme = xmlColorTheme,
            textSize = PrefManager.keyFontSize,
            scrollBehavior = scrollBehavior,
            text = stringResource(id = R.string.about_body),
            onValueChange = {},
        )
    }
}
