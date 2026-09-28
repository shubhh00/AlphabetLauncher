package com.app.alphabetlauncher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.app.alphabetlauncher.data.AppCatalogObserver
import com.app.alphabetlauncher.data.LaunchableApp
import com.app.alphabetlauncher.data.LauncherPreferences
import com.app.alphabetlauncher.data.loadApps
import com.app.alphabetlauncher.ui.LauncherScreen
import com.app.alphabetlauncher.ui.LauncherUiState
import com.app.alphabetlauncher.ui.theme.AlphabetLauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    private lateinit var preferences: LauncherPreferences
    private lateinit var appCatalogObserver: AppCatalogObserver
    private var appRefreshJob: Job? = null
    private var uiState by mutableStateOf(LauncherUiState())
    private val homeRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            uiState = uiState.copy(isDefaultLauncher = isDefaultLauncher(this))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preferences = LauncherPreferences(this)
        appCatalogObserver = AppCatalogObserver(this) { refreshApps(debounceMillis = 250) }
        val isDefault = isDefaultLauncher(this)
        uiState = uiState.copy(
            isDefaultLauncher = isDefault,
            showHomePrompt = !isDefault && !preferences.hasAnsweredHomePrompt
        )
        setContent {
            AlphabetLauncherTheme {
                LauncherScreen(
                    state = uiState,
                    launchApp = ::launchApp,
                    toggleFavourite = ::toggleFavourite,
                    requestDefaultLauncher = ::requestDefaultLauncher,
                    dismissHomePrompt = ::dismissHomePrompt
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appCatalogObserver.start()
        refreshApps()
    }

    override fun onResume() {
        super.onResume()
        val isDefault = isDefaultLauncher(this)
        uiState = uiState.copy(
            isDefaultLauncher = isDefault,
            showHomePrompt = uiState.showHomePrompt && !isDefault
        )
    }

    override fun onStop() {
        appCatalogObserver.stop()
        appRefreshJob?.cancel()
        super.onStop()
    }

    private fun refreshApps(debounceMillis: Long = 0) {
        appRefreshJob?.cancel()
        appRefreshJob = lifecycleScope.launch {
            if (debounceMillis > 0) delay(debounceMillis.milliseconds)
            val apps = withContext(Dispatchers.IO) { loadApps(packageManager) }
            val favourites = if (uiState.loading) {
                withContext(Dispatchers.IO) { preferences.favourites(apps) }
            } else {
                uiState.favouriteComponents
            }
            uiState = uiState.copy(apps = apps, favouriteComponents = favourites, loading = false)
        }
    }

    private fun toggleFavourite(app: LaunchableApp) {
        val component = app.component.flattenToString()
        val updated = uiState.favouriteComponents.toMutableSet()
        if (!updated.add(component)) updated.remove(component)
        uiState = uiState.copy(favouriteComponents = updated)
        preferences.saveFavourites(updated)
    }

    private fun dismissHomePrompt() {
        preferences.hasAnsweredHomePrompt = true
        uiState = uiState.copy(showHomePrompt = false)
    }

    private fun requestDefaultLauncher() {
        dismissHomePrompt()
        try {
            homeRequest.launch(defaultLauncherRequest(this))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Open Settings > Default apps > Home app", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun launchApp(app: LaunchableApp) {
        try {
            startActivity(Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = app.component
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            })
        } catch (_: ActivityNotFoundException) {
            // An app may have been uninstalled since the catalogue was loaded.
        }
    }
}
