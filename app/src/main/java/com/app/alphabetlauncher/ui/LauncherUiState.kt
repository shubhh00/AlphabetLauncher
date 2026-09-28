package com.app.alphabetlauncher.ui

import com.app.alphabetlauncher.data.LaunchableApp

data class LauncherUiState(
    val apps: List<LaunchableApp> = emptyList(),
    val loading: Boolean = true,
    val favouriteComponents: Set<String> = emptySet(),
    val isDefaultLauncher: Boolean = false,
    val showHomePrompt: Boolean = false
)
