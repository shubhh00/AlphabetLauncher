package com.app.alphabetlauncher.ui.alphabet

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

internal fun <T> groupByInitial(items: Iterable<T>, nameOf: (T) -> String): Map<Char, List<T>> {
    val groups = mutableMapOf<Char, MutableList<T>>()
    for (item in items) {
        val letter = nameOf(item).firstOrNull()?.uppercaseChar()?.takeIf { it in 'A'..'Z' }
            ?: continue
        groups.getOrPut(letter) { mutableListOf() }.add(item)
    }
    return groups
}
