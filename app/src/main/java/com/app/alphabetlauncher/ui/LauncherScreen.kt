package com.app.alphabetlauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.app.alphabetlauncher.ui.alphabet.AlphabetBar
import com.app.alphabetlauncher.ui.alphabet.groupByInitial
import com.app.alphabetlauncher.ui.components.AppList
import com.app.alphabetlauncher.ui.components.Clock
import com.app.alphabetlauncher.ui.search.SearchScreen
import com.app.alphabetlauncher.ui.search.swipeUpToSearch

@Composable
fun LauncherScreen(
    state: LauncherUiState,
    launchApp: (LaunchableApp) -> Unit,
    toggleFavourite: (LaunchableApp) -> Unit,
    requestDefaultLauncher: () -> Unit,
    dismissHomePrompt: () -> Unit
) {
    var selected by remember { mutableStateOf<Char?>(null) }
    var searchOpen by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    BackHandler(enabled = selected != null && !searchOpen) { selected = null }
    val apps = state.apps
    val favouriteApps = remember(apps, state.favouriteComponents) {
        apps.filter { it.component.flattenToString() in state.favouriteComponents }
    }
    val grouped = remember(apps) {
        groupByInitial(apps) { it.name }
    }
    val availableLetters = grouped.keys
    val colors = MaterialTheme.colorScheme

    if (searchOpen) {
        SearchScreen(
            apps = apps,
            loading = state.loading,
            favouriteComponents = state.favouriteComponents,
            launchApp = launchApp,
            toggleFavourite = toggleFavourite,
            onClose = { searchOpen = false }
        )
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.background)
                .swipeUpToSearch(enabled = selected == null) {
                    searchOpen = true
                }
        ) {
            LauncherBackground(Modifier.fillMaxSize())
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 76.dp, end = 90.dp, bottom = 30.dp)
            ) {
                if (selected == null) {
                    Clock()
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "↑  Swipe up to search apps",
                        modifier = Modifier.clickable(onClickLabel = "Search apps") {
                            searchOpen = true
                        },
                        color = colors.primary,
                        fontSize = 13.sp
                    )
                    if (!state.isDefaultLauncher) {
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = requestDefaultLauncher) {
                            Text("Set as default launcher")
                        }
                    }
                    Spacer(Modifier.height(if (state.isDefaultLauncher) 38.dp else 24.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "FAVOURITES",
                            color = colors.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        )
                        if (!state.loading) {
                            Text(
                                "${favouriteApps.size} APPS",
                                color = colors.onBackground.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (state.loading) {
                        Text("Loading apps…", color = colors.onBackground)
                    } else {
                        Text(
                            "Hold an app to edit favourites",
                            color = colors.onBackground.copy(alpha = 0.55f),
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        if (favouriteApps.isEmpty()) {
                            Text(
                                "No favourites yet",
                                color = colors.onBackground.copy(alpha = 0.65f)
                            )
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
                    TextButton(
                        onClick = { selected = null },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("← Favourites")
                    }
                    Spacer(Modifier.height(26.dp))
                    Text(
                        "BROWSE APPS",
                        color = colors.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    val matches = selected?.let { grouped[it] }.orEmpty()
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            selected.toString(),
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Light,
                            lineHeight = 76.sp,
                            color = colors.onBackground
                        )
                        Text(
                            "${matches.size} ${if (matches.size == 1) "app" else "apps"}",
                            color = colors.onBackground.copy(alpha = 0.55f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 10.dp, bottom = 13.dp)
                        )
                    }
                    Spacer(Modifier.height(22.dp))
                    if (matches.isEmpty()) {
                        Text(
                            "No apps under $selected",
                            color = colors.onBackground.copy(alpha = 0.65f),
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Slide the alphabet to choose another letter",
                            color = colors.onBackground.copy(alpha = 0.5f),
                            fontSize = 12.sp
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
    }
    if (state.showHomePrompt) {
        DefaultHomePrompt(requestDefaultLauncher, dismissHomePrompt)
    }
}
