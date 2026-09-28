package com.app.alphabetlauncher.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
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
            now = Date()
            delay(1_000.milliseconds)
        }
    }
    val color = MaterialTheme.colorScheme.onBackground
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
