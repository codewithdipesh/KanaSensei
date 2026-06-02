package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.ui.components.haptic.rememberHapticManager
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

/**
 * The Write page's interactive layer: the user draws each stroke with their finger and it's matched
 * against the target stroke (in order) by [StrokeMatcher]. A passing stroke snaps to the clean
 * target ink and advances; a miss flashes red and lets them retry. When the last stroke passes,
 * [onComplete] fires so the screen can show "Nice Work!" and enable Continue.
 *
 * All capture/matching happens through the shared [KanaViewport], so the user is effectively
 * writing in the same coordinate space as the guide drawn behind by [KanaStage]. Bumping [resetKey]
 * clears progress and starts over.
 */
@Composable
fun KanaWritePad(
    strokes: KanaStrokes,
    paths: List<Path>,
    color: Color,
    modifier: Modifier = Modifier,
    resetKey: Int = 0,
    onComplete: () -> Unit = {},
) {
    val haptic = rememberHapticManager()

    // Target strokes sampled once into viewBox-space points for matching.
    val targets = remember(paths) {
        paths.map { p -> StrokeMatcher.sampleTarget(PathMeasure().apply { setPath(p, false) }) }
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var strokeIndex by remember(resetKey, paths) { mutableStateOf(0) }
    var currentPoints by remember(resetKey, paths) { mutableStateOf<List<Offset>>(emptyList()) }
    var errorPoints by remember(resetKey, paths) { mutableStateOf<List<Offset>>(emptyList()) }

    // Brief red flash of the attempted ink when a stroke doesn't match.
    val errorAlpha = remember { Animatable(0f) }
    LaunchedEffect(errorPoints) {
        if (errorPoints.isNotEmpty()) {
            errorAlpha.snapTo(1f)
            errorAlpha.animateTo(0f, tween(450))
            errorPoints = emptyList()
        }
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .pointerInput(paths, resetKey) {
                detectDragGestures(
                    onDragStart = { offset -> currentPoints = listOf(offset) },
                    onDrag = { change, _ ->
                        currentPoints = currentPoints + change.position
                        change.consume()
                    },
                    onDragCancel = { currentPoints = emptyList() },
                    onDragEnd = {
                        val index = strokeIndex
                        if (index < targets.size && canvasSize != Size.Zero) {
                            val viewport = KanaViewport.fit(
                                canvasSize, strokes.viewBoxWidth, strokes.viewBoxHeight
                            )
                            val userViewBox = currentPoints.map { viewport.toViewBox(it) }
                            if (StrokeMatcher.matches(userViewBox, targets[index])) {
                                strokeIndex = index + 1
                                haptic.correctHaptic()
                                if (strokeIndex >= targets.size) onComplete()
                            } else {
                                haptic.wrongHaptic()
                                errorPoints = currentPoints
                            }
                        }
                        currentPoints = emptyList()
                    }
                )
            }
    ) {
        val viewport = KanaViewport.fit(size, strokes.viewBoxWidth, strokes.viewBoxHeight)
        // Ink drawn in canvas space needs the brush scaled to match the viewport-drawn strokes.
        val inkWidth = KanaGlyphDefaults.STROKE_WIDTH * viewport.scale
        val inkBrush = Stroke(width = inkWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val targetBrush = Stroke(
            width = KanaGlyphDefaults.STROKE_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round
        )

        // Completed strokes: snap to clean target ink (viewBox space).
        withKanaViewport(viewport) {
            for (i in 0 until strokeIndex) {
                drawPath(paths[i], color = color, style = targetBrush)
            }
        }

        // The stroke being drawn right now (raw canvas-space points).
        if (currentPoints.size > 1) {
            drawPath(polyline(currentPoints), color = color, style = inkBrush)
        }

        // A miss, fading out.
        if (errorPoints.size > 1 && errorAlpha.value > 0f) {
            drawPath(
                polyline(errorPoints),
                color = KanaColors.error,
                alpha = errorAlpha.value,
                style = inkBrush
            )
        }
    }
}

/** Builds an open path through [points] (canvas space). */
private fun polyline(points: List<Offset>): Path = Path().apply {
    moveTo(points.first().x, points.first().y)
    for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
}
