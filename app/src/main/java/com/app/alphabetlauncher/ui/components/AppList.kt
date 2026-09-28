package com.app.alphabetlauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val colors = MaterialTheme.colorScheme
    val rowShape = RoundedCornerShape(18.dp)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(apps, key = { it.component.flattenToString() }) { app ->
            val isFavourite = app.component.flattenToString() in favouriteComponents
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(rowShape)
                    .background(colors.surface.copy(alpha = 0.86f))
                    .border(1.dp, colors.onBackground.copy(alpha = 0.08f), rowShape)
                    .combinedClickable(
                        onClickLabel = "Open ${app.name}",
                        onLongClickLabel = if (isFavourite) "Remove from favourites" else "Add to favourites",
                        onLongClick = { toggleFavourite(app) },
                        onClick = { launchApp(app) }
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(app.icon, contentDescription = null, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    app.name,
                    color = colors.onBackground,
                    fontSize = 16.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (isFavourite) Text("★", color = colors.primary, fontSize = 16.sp)
            }
        }
    }
}
