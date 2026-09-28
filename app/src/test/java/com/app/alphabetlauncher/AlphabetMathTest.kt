package com.app.alphabetlauncher

import com.app.alphabetlauncher.ui.bendOffset
import com.app.alphabetlauncher.ui.letterAt
import com.app.alphabetlauncher.ui.lettersWithApps
import com.app.alphabetlauncher.ui.startsWithLetter
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
    fun matchingIgnoresCaseAndHandlesEmptyNames() {
        assertTrue(startsWithLetter("gmail", 'G'))
        assertTrue(startsWithLetter("GPay", 'G'))
        assertTrue(!startsWithLetter("", 'G'))
        assertTrue(!startsWithLetter("Maps", 'G'))
    }

    @Test
    fun onlyLettersWithAppsAreAvailable() {
        assertEquals(setOf('A', 'G'), lettersWithApps(listOf("App", "gmail", "GPay", "", "#Tools")))
    }
}
