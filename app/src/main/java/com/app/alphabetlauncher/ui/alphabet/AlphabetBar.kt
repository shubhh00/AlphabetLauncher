package com.app.alphabetlauncher.ui.alphabet

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.max

internal val AlphabetBarWidth = 132.dp

@Composable
internal fun AlphabetBar(
    modifier: Modifier = Modifier,
    availableLetters: Set<Char>,
    onLetterChanged: (Char) -> Unit
) {
    var active by remember { mutableStateOf(false) }
    var touchY by remember { mutableFloatStateOf(0f) }
    val currentLetterChanged by rememberUpdatedState(onLetterChanged)
    val foreground = MaterialTheme.colorScheme.onBackground
    val background = MaterialTheme.colorScheme.background
    val bend by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 450f),
        label = "alphabet bend"
    )
    val density = LocalDensity.current.density
    val paint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 14f * density
            typeface = android.graphics.Typeface.create(
                "sans-serif-medium",
                android.graphics.Typeface.NORMAL
            )
        }
    }
    Canvas(
        modifier
            .fillMaxHeight(0.72f)
            .width(AlphabetBarWidth)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    touchY = down.position.y.coerceIn(0f, size.height.toFloat())
                    active = true
                    var lastLetter = letterAt(touchY, size.height.toFloat())
                    currentLetterChanged(lastLetter)
                    var change: androidx.compose.ui.input.pointer.PointerInputChange?
                    do {
                        val event = awaitPointerEvent()
                        change = event.changes.firstOrNull { it.id == down.id }
                        if (change != null && change.pressed) {
                            touchY = change.position.y.coerceIn(0f, size.height.toFloat())
                            val letter = letterAt(touchY, size.height.toFloat())
                            if (letter != lastLetter) {
                                lastLetter = letter
                                currentLetterChanged(letter)
                            }
                            change.consume()
                        }
                    } while (change != null && change.pressed)
                    active = false
                }
            }
    ) {
        val rowHeight = size.height / 28f
        val baseX = size.width - 24.dp.toPx()
        val maxBend = 48.dp.toPx() * bend
        val radius = max(rowHeight * 3.5f, 1f)
        paint.color = foreground.toArgb()
        paint.textSize = 14f * density
        for (row in 0 until 28) {
            val centreY = (row + 0.5f) * rowHeight
            val label = when (row) {
                0 -> "☆"
                27 -> "◦"
                else -> ('A' + row - 1).toString()
            }
            paint.alpha = if (row in 1..26 && ('A' + row - 1) !in availableLetters) 77 else 255
            val x = baseX - bendOffset(centreY, touchY, radius, maxBend)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x,
                centreY - (paint.ascent() + paint.descent()) / 2f,
                paint
            )
        }
        if (active) {
            val bubbleX = baseX - 87.dp.toPx()
            val bubbleY = touchY.coerceIn(24.dp.toPx(), size.height - 24.dp.toPx())
            drawCircle(foreground, 22.dp.toPx(), Offset(bubbleX, bubbleY))
            paint.color = background.toArgb()
            paint.alpha = 255
            paint.textSize = 24f * density
            drawContext.canvas.nativeCanvas.drawText(
                letterAt(touchY, size.height).toString(), bubbleX,
                bubbleY - (paint.ascent() + paint.descent()) / 2f, paint
            )
        }
    }
}
