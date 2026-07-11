package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay

/**
 * Animates the kana's strokes one at a time, in order, to teach stroke order — the same paths the
 * other pages render, revealed progressively with a moving brush tip so direction is obvious.
 *
 * It draws only the "ink so far": already-finished strokes stay solid, the active stroke grows from
 * its start point, and upcoming strokes are left to the faint guide drawn behind this layer
 * (see [KanaStage]). Bumping [replayKey] restarts the animation from stroke one.
 */
@Composable
fun KanaStrokeAnimator(
    paths: List<Path>,
    viewBoxWidth: Float,
    viewBoxHeight: Float,
    color: Color,
    modifier: Modifier = Modifier,
    replayKey: Int = 0,
    strokeWidth: Float = KanaGlyphDefaults.STROKE_WIDTH,
) {
    // One PathMeasure per stroke, holding its total length — rebuilt only when the glyph changes.
    val measures = remember(paths) {
        paths.map { path -> PathMeasure().apply { setPath(path, false) } }
    }

    val progress = remember { Animatable(0f) }
    var activeIndex by remember { mutableStateOf(0) }

    LaunchedEffect(paths, replayKey) {
        if (paths.isEmpty()) return@LaunchedEffect
        for (i in paths.indices) {
            activeIndex = i
            progress.snapTo(0f)
            val durationMs = (measures[i].length * MS_PER_VIEWBOX_UNIT)
                .toInt()
                .coerceIn(MIN_STROKE_MS, MAX_STROKE_MS)
            progress.animateTo(1f, tween(durationMs, easing = LinearEasing))
            delay(PAUSE_BETWEEN_STROKES_MS)
        }
    }

    Canvas(modifier) {
        if (paths.isEmpty()) return@Canvas
        val viewport = KanaViewport.fit(size, viewBoxWidth, viewBoxHeight)
        val brush = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val tipRadius = strokeWidth * 0.7f
        // Read so the Canvas redraws as the stroke fills in.
        val activeProgress = progress.value
        val index = activeIndex

        withKanaViewport(viewport) {
            // Strokes already completed: draw them whole.
            for (i in 0 until index) {
                drawPath(paths[i], color = color, style = brush)
            }

            // The active stroke: reveal 0..length and draw the brush tip at the leading edge.
            val measure = measures[index]
            val drawnLength = activeProgress * measure.length
            val segment = Path()
            if (measure.getSegment(0f, drawnLength, segment, startWithMoveTo = true)) {
                drawPath(segment, color = color, style = brush)
            }
            drawCircle(
                color = color,
                radius = tipRadius,
                center = measure.getPosition(drawnLength)
            )
        }
    }
}

private const val MS_PER_VIEWBOX_UNIT = 7f
private const val MIN_STROKE_MS = 350
private const val MAX_STROKE_MS = 1400
private const val PAUSE_BETWEEN_STROKES_MS = 180L
