package com.app.alphabetlauncher.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun Clock() {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((60_000 - System.currentTimeMillis() % 60_000).milliseconds)
            now = Date()
        }
    }
    val colors = MaterialTheme.colorScheme
    Text(
        "HOME",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp,
        color = colors.primary
    )
    Spacer(Modifier.height(10.dp))
    Text(
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
        fontSize = 72.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = (-3).sp,
        lineHeight = 76.sp,
        color = colors.onBackground
    )
    Text(
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now),
        fontSize = 16.sp,
        color = colors.onBackground.copy(alpha = 0.68f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
