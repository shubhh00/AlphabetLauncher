package com.app.alphabetlauncher.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
internal fun DefaultHomePrompt(onChoose: () -> Unit, onNotNow: () -> Unit) {
    AlertDialog(
        onDismissRequest = onNotNow,
        title = { Text("Use Alphabet Launcher as your Home app?") },
        text = { Text("You can choose it as your default launcher in the Android system picker. You can change this later in Settings.") },
        confirmButton = { TextButton(onClick = onChoose) { Text("Choose launcher") } },
        dismissButton = { TextButton(onClick = onNotNow) { Text("Not now") } }
    )
}
