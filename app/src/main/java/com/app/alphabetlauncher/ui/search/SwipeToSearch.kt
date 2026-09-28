package com.app.alphabetlauncher.ui.search

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.app.alphabetlauncher.ui.alphabet.AlphabetBarWidth
import kotlin.math.abs

/** Keep the pointer observer attached while a letter selection changes the screen. */
@Composable
internal fun Modifier.swipeUpToSearch(
    enabled: Boolean,
    onSwipeUp: () -> Unit
): Modifier {
    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnSwipeUp by rememberUpdatedState(onSwipeUp)
    return pointerInput(Unit) {
        val swipeDistance = 72.dp.toPx()
        val alphabetWidth = AlphabetBarWidth.toPx()
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (!currentEnabled || down.position.x >= size.width - alphabetWidth) {
                return@awaitEachGesture
            }

            var movement = Offset.Zero
            var handledByChild = down.isConsumed
            while (true) {
                val change = awaitPointerEvent(PointerEventPass.Final)
                    .changes.firstOrNull { it.id == down.id } ?: break
                if (!change.pressed) break
                if (change.isConsumed) handledByChild = true
                if (handledByChild) continue

                movement += change.position - change.previousPosition
                if (movement.y <= -swipeDistance && -movement.y > abs(movement.x) * 1.25f) {
                    currentOnSwipeUp()
                    break
                }
            }
        }
    }
}
