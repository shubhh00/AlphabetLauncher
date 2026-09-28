package com.app.alphabetlauncher

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.app.alphabetlauncher.ui.theme.AlphabetLauncherTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

data class LaunchableApp(val name: String, val component: ComponentName, val icon: ImageBitmap)

class MainActivity : ComponentActivity() {
    private var apps by mutableStateOf<List<LaunchableApp>>(emptyList())
    private var loading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            apps = withContext(Dispatchers.IO) { loadApps(packageManager) }
            loading = false
        }
        setContent {
            AlphabetLauncherTheme(dynamicColor = false) {
                LauncherScreen(apps, loading, ::launchApp)
            }
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

private fun loadApps(pm: PackageManager): List<LaunchableApp> {
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val activities = if (Build.VERSION.SDK_INT >= 33) {
        pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        pm.queryIntentActivities(intent, 0)
    }
    return activities.mapNotNull { info ->
        val activity = info.activityInfo ?: return@mapNotNull null
        LaunchableApp(
            info.loadLabel(pm).toString(),
            ComponentName(activity.packageName, activity.name),
            info.loadIcon(pm).toBitmap(96, 96).asImageBitmap()
        )
    }.distinctBy { it.component }
        .sortedBy { it.name.lowercase(Locale.ROOT) }
}

@Composable
private fun LauncherScreen(
    apps: List<LaunchableApp>,
    loading: Boolean,
    launchApp: (LaunchableApp) -> Unit
) {
    var selected by androidx.compose.runtime.remember { mutableStateOf<Char?>(null) }
    val grouped = androidx.compose.runtime.remember(apps) {
        ('A'..'Z').associateWith { letter ->
            appsForLetter(
                apps,
                letter
            )
        }
    }
    val foreground = Color.White
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 34.dp, top = 90.dp, end = 102.dp, bottom = 36.dp)
        ) {
            if (selected == null) {
                Clock(foreground)
                Spacer(Modifier.height(22.dp))
                if (loading) Text("Loading apps…", color = foreground)
                else AppList(apps.take(7), foreground, launchApp)
            } else {
                Text(
                    selected.toString(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Light,
                    color = foreground
                )
                Spacer(Modifier.height(16.dp))
                val matches = selected?.let { grouped[it] }.orEmpty()
                if (matches.isEmpty()) Text(
                    "No apps",
                    color = foreground.copy(alpha = 0.65f),
                    fontSize = 18.sp
                )
                else AppList(matches, foreground, launchApp)
            }
        }
        AlphabetBar(Modifier.align(Alignment.CenterEnd), { selected = it }, { selected = null })
    }
}

@Composable
private fun Clock(color: Color) {
    var now by androidx.compose.runtime.remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(1_000.milliseconds)
        }
    }
    Text(
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
        fontSize = 60.sp,
        fontWeight = FontWeight.Light,
        color = color
    )
    Text(
        SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(now),
        fontSize = 19.sp,
        color = color
    )
}

@Composable
private fun AppList(apps: List<LaunchableApp>, color: Color, launchApp: (LaunchableApp) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        items(apps, key = { it.component.flattenToString() }) { app ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable { launchApp(app) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(app.icon, contentDescription = null, modifier = Modifier.size(42.dp))
                Spacer(Modifier.width(18.dp))
                Text(app.name, color = color, fontSize = 18.sp, maxLines = 1)
            }
        }
    }
}
