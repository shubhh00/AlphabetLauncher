package com.app.alphabetlauncher.data

import android.content.Context
import androidx.core.content.edit

internal class LauncherPreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences("launcher_preferences", Context.MODE_PRIVATE)

    fun favourites(apps: List<LaunchableApp>): Set<String> {
        val saved = preferences.getStringSet(FAVOURITES, null)
        if (saved != null) return saved.toSet()

        val initial = apps.take(7).mapTo(mutableSetOf()) { it.component.flattenToString() }
        if (apps.isNotEmpty()) saveFavourites(initial)
        return initial
    }

    fun saveFavourites(components: Set<String>) {
        preferences.edit { putStringSet(FAVOURITES, components.toSet()) }
    }

    var hasAnsweredHomePrompt: Boolean
        get() = preferences.getBoolean(HOME_PROMPT_ANSWERED, false)
        set(value) {
            preferences.edit { putBoolean(HOME_PROMPT_ANSWERED, value) }
        }

    private companion object {
        const val FAVOURITES = "favourites"
        const val HOME_PROMPT_ANSWERED = "home_prompt_answered"
    }
}
