package com.app.alphabetlauncher.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.alphabetlauncher.data.LaunchableApp
import com.app.alphabetlauncher.ui.LauncherBackground
import com.app.alphabetlauncher.ui.components.AppList

@Composable
internal fun SearchScreen(
    apps: List<LaunchableApp>,
    loading: Boolean,
    favouriteComponents: Set<String>,
    launchApp: (LaunchableApp) -> Unit,
    toggleFavourite: (LaunchableApp) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val colors = MaterialTheme.colorScheme
    val results = remember(apps, query) {
        val term = query.trim()
        if (term.isEmpty()) apps else apps.filter { it.name.contains(term, ignoreCase = true) }
    }

    fun close() {
        keyboard?.hide()
        onClose()
    }

    BackHandler { close() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Box(Modifier.fillMaxSize().background(colors.background)) {
        LauncherBackground(Modifier.fillMaxSize())
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
                .padding(start = 24.dp, top = 76.dp, end = 24.dp, bottom = 20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SEARCH APPS",
                    color = colors.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
                TextButton(onClick = { close() }) { Text("Cancel") }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                placeholder = { Text("Type an app name") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() })
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "${results.size} ${if (results.size == 1) "APP" else "APPS"}",
                color = colors.onBackground.copy(alpha = 0.55f),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))
            if (loading) {
                Text("Loading apps…", color = colors.onBackground.copy(alpha = 0.65f))
            } else if (results.isEmpty()) {
                Text(
                    "No matching apps",
                    color = colors.onBackground.copy(alpha = 0.65f),
                    fontSize = 16.sp
                )
            } else {
                AppList(results, favouriteComponents, launchApp, toggleFavourite)
            }
        }
    }
}
