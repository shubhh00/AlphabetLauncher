package com.app.alphabetlauncher.data

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import java.util.Locale

data class LaunchableApp(val name: String, val component: ComponentName, val icon: ImageBitmap)

internal fun loadApps(pm: PackageManager): List<LaunchableApp> {
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
            name = info.loadLabel(pm).toString(),
            component = ComponentName(activity.packageName, activity.name),
            icon = info.loadIcon(pm).toBitmap(96, 96).asImageBitmap()
        )
    }.distinctBy { it.component }
        .sortedBy { it.name.lowercase(Locale.ROOT) }
}
