package com.app.alphabetlauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.alphabetlauncher.data.LaunchableApp

@Composable
fun LauncherScreen(
    state: LauncherUiState,
    launchApp: (LaunchableApp) -> Unit,
    toggleFavourite: (LaunchableApp) -> Unit,
    requestDefaultLauncher: () -> Unit,
    dismissHomePrompt: () -> Unit
) {
    var selected by remember { mutableStateOf<Char?>(null) }
    val haptics = LocalHapticFeedback.current
    BackHandler(enabled = selected != null) { selected = null }
    val apps = state.apps
    val favouriteApps = remember(apps, state.favouriteComponents) {
        apps.filter { it.component.flattenToString() in state.favouriteComponents }
    }
    val grouped = remember(apps) {
        ('A'..'Z').associateWith { letter -> appsForLetter(apps, letter) }
    }
    val availableLetters = remember(apps) { lettersWithApps(apps.map { it.name }) }
    val colors = MaterialTheme.colorScheme

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LauncherBackground(Modifier.fillMaxSize())
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 34.dp, top = 90.dp, end = 102.dp, bottom = 36.dp)
        ) {
            if (selected == null) {
                Clock()
                if (state.isDefaultLauncher) {
                    Spacer(Modifier.height(22.dp))
                } else {
                    TextButton(onClick = requestDefaultLauncher) {
                        Text("Set as default launcher")
                    }
                }
                if (state.loading) {
                    Text("Loading apps…", color = colors.onBackground)
                } else {
                    Text(
                        "Long-press an app to edit favourites",
                        color = colors.onBackground.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    if (favouriteApps.isEmpty()) {
                        Text("No favourites yet", color = colors.onBackground.copy(alpha = 0.65f))
                    } else {
                        AppList(
                            favouriteApps,
                            state.favouriteComponents,
                            launchApp,
                            toggleFavourite
                        )
                    }
                }
            } else {
                TextButton(onClick = { selected = null }) {
                    Text("← Favourites")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    selected.toString(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Light,
                    color = colors.onBackground
                )
                Spacer(Modifier.height(16.dp))
                val matches = selected?.let { grouped[it] }.orEmpty()
                if (matches.isEmpty()) {
                    Text(
                        "No apps",
                        color = colors.onBackground.copy(alpha = 0.65f),
                        fontSize = 18.sp
                    )
                } else {
                    AppList(matches, state.favouriteComponents, launchApp, toggleFavourite)
                }
            }
        }
        AlphabetBar(
            modifier = Modifier.align(Alignment.CenterEnd),
            availableLetters = if (state.loading) ('A'..'Z').toSet() else availableLetters,
            onLetterChanged = { letter ->
                if (selected != letter) {
                    selected = letter
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }
            }
        )
    }
    if (state.showHomePrompt) {
        DefaultHomePrompt(requestDefaultLauncher, dismissHomePrompt)
    }
}
