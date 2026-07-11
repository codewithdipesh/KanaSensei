package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Draws a set of kana stroke [paths] (in viewBox space) as brush strokes through the shared
 * [KanaViewport]. Used directly for the Listen page (full opacity) and the Trace page (low
 * opacity guide); the Stroke and Write pages reuse the same viewport + brush styling so their
 * extra layers register exactly on top of this.
 *
 * @param paths stroke paths in viewBox coordinates (from [rememberKanaPaths]).
 * @param viewBoxWidth/[viewBoxHeight] the SVG viewBox size (usually 109x109).
 * @param strokeWidth brush thickness in viewBox units.
 */
@Composable
fun KanaGlyph(
    paths: List<Path>,
    viewBoxWidth: Float,
    viewBoxHeight: Float,
    color: Color,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    strokeWidth: Float = KanaGlyphDefaults.STROKE_WIDTH,
) {
    Canvas(modifier) {
        if (paths.isEmpty()) return@Canvas
        val viewport = KanaViewport.fit(size, viewBoxWidth, viewBoxHeight)
        val brush = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        withKanaViewport(viewport) {
            paths.forEach { path ->
                drawPath(path = path, color = color, alpha = alpha, style = brush)
            }
        }
    }
}
