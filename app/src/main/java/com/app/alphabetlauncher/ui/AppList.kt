package com.app.alphabetlauncher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.alphabetlauncher.data.LaunchableApp

@Composable
internal fun AppList(
    apps: List<LaunchableApp>,
    favouriteComponents: Set<String>,
    launchApp: (LaunchableApp) -> Unit,
    toggleFavourite: (LaunchableApp) -> Unit
) {
    val color = MaterialTheme.colorScheme.onBackground
    LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        items(apps, key = { it.component.flattenToString() }) { app ->
            val isFavourite = app.component.flattenToString() in favouriteComponents
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .combinedClickable(
                        onClickLabel = "Open ${app.name}",
                        onLongClickLabel = if (isFavourite) "Remove from favourites" else "Add to favourites",
                        onLongClick = { toggleFavourite(app) },
                        onClick = { launchApp(app) }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(app.icon, contentDescription = null, modifier = Modifier.size(42.dp))
                Spacer(Modifier.width(18.dp))
                Text(
                    app.name,
                    color = color,
                    fontSize = 18.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (isFavourite) Text("★", color = color, fontSize = 18.sp)
            }
        }
    }
}
