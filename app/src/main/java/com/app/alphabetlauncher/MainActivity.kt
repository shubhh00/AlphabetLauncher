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
import com.app.alphabetlauncher.data.LaunchableApp
import com.app.alphabetlauncher.data.LauncherPreferences
import com.app.alphabetlauncher.data.loadApps
import com.app.alphabetlauncher.ui.LauncherScreen
import com.app.alphabetlauncher.ui.LauncherUiState
import com.app.alphabetlauncher.ui.theme.AlphabetLauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var preferences: LauncherPreferences
    private var uiState by mutableStateOf(LauncherUiState())
    private val homeRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            uiState = uiState.copy(isDefaultLauncher = isDefaultLauncher(this))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preferences = LauncherPreferences(this)
        val isDefault = isDefaultLauncher(this)
        uiState = uiState.copy(
            isDefaultLauncher = isDefault,
            showHomePrompt = !isDefault && !preferences.hasAnsweredHomePrompt
        )
        lifecycleScope.launch {
            val (apps, favourites) = withContext(Dispatchers.IO) {
                val loaded = loadApps(packageManager)
                loaded to preferences.favourites(loaded)
            }
            uiState = uiState.copy(apps = apps, favouriteComponents = favourites, loading = false)
        }
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

    override fun onResume() {
        super.onResume()
        val isDefault = isDefaultLauncher(this)
        uiState = uiState.copy(
            isDefaultLauncher = isDefault,
            showHomePrompt = uiState.showHomePrompt && !isDefault
        )
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
