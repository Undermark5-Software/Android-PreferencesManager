@file:OptIn(ExperimentalMaterial3Api::class)

package fr.simon.marquis.preferencesmanager.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.simon.marquis.preferencesmanager.R
import fr.simon.marquis.preferencesmanager.ui.theme.AppTheme

private val slideIn = {
    slideIn(
        initialOffset = { IntOffset(it.width, 0) },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )

    )
}

private val slideUp = {
    slideOut(
        targetOffset = { IntOffset(0, -it.height) },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    )
}

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    textState: TextFieldValue? = null,
    isSearching: Boolean = false,
    onSearchClose: () -> Unit = {},
    onSearchValueChange: (TextFieldValue) -> Unit,
) {

    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )

    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        SmallTopAppBar(
            modifier = modifier,
            actions = actions,
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                    content = { title() }
                )
            },
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = navigationIcon
        )

        if (textState != null) {
            AnimatedVisibility(
                visible = isSearching,
                enter = slideIn(),
                exit = slideUp(),
            ) {
                SearchView(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    value = textState,
                    onClose = onSearchClose,
                    onValueChange = onSearchValueChange,
                )
            }
        }
    }
}

@Composable
fun SearchView(
    backgroundColor: Color,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClose: () -> Unit
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(64.dp),
        label = { Text("Search apps...") },
        textStyle = TextStyle(fontSize = 18.sp),
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            IconButton(
                onClick = {
                    onClose()
                    onValueChange(TextFieldValue(""))
                }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    // Remove text from TextField when you press the 'X' icon
                    onValueChange(TextFieldValue(""))
                }
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = backgroundColor
        )
    )
}

@Composable
fun NavigationBack(
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        content = { Icon(Icons.Default.ArrowBack, contentDescription = null) }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview_AppBar(
    appName: String = stringResource(id = R.string.app_name),

    ) {
    var textFieldValue: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    AppTheme(isSystemInDarkTheme()) {
        AppBar(
            title = { Text(appName) },
            textState = textFieldValue,
            onSearchValueChange = {
                textFieldValue = it
            }
        )
    }
}
