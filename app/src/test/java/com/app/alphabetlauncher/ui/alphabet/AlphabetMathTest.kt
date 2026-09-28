package com.app.alphabetlauncher.ui.alphabet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlphabetMathTest {
    @Test
    fun touchPositionMapsToLetters() {
        assertEquals('A', letterAt(0f, 280f))
        assertEquals('A', letterAt(15f, 280f))
        assertEquals('M', letterAt(135f, 280f))
        assertEquals('Z', letterAt(279f, 280f))
    }

    @Test
    fun bendFallsOffSmoothly() {
        val centre = bendOffset(50f, 50f, 20f, 40f)
        val nearby = bendOffset(70f, 50f, 20f, 40f)
        val far = bendOffset(110f, 50f, 20f, 40f)
        assertEquals(40f, centre, 0.001f)
        assertTrue(centre > nearby && nearby > far && far >= 0f)
    }

    @Test
    fun groupingPreservesAppOrderAndSkipsNamesWithoutLetters() {
        val grouped = groupByInitial(listOf("App", "gmail", "GPay", "", "#Tools", "Notes")) { it }
        assertEquals(listOf("App"), grouped['A'])
        assertEquals(listOf("gmail", "GPay"), grouped['G'])
        assertEquals(listOf("Notes"), grouped['N'])
        assertEquals(setOf('A', 'G', 'N'), grouped.keys)
    }
}
