package com.app.alphabetlauncher

import kotlin.math.exp

internal fun letterAt(y: Float, height: Float): Char {
    val rowHeight = height / 28f // star + A-Z + dot
    val row = (y / rowHeight).toInt().coerceIn(1, 26)
    return 'A' + (row - 1)
}

internal fun bendOffset(rowY: Float, touchY: Float, radius: Float, maxOffset: Float): Float {
    val distance = (rowY - touchY) / radius
    return maxOffset * exp(-0.5f * distance * distance)
}

internal fun appsForLetter(apps: List<LaunchableApp>, letter: Char): List<LaunchableApp> =
    apps.filter { startsWithLetter(it.name, letter) }

internal fun startsWithLetter(name: String, letter: Char): Boolean =
    name.firstOrNull()?.uppercaseChar() == letter
