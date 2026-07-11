package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import kotlin.math.min

/**
 * The single shared mapping from a kana's SVG viewBox space into on-canvas pixels.
 *
 * This is the heart of the whole feature: every layer of every page (the faint guide, the
 * animated stroke, the user's ink) is drawn through the *same* viewport, so they always line up
 * pixel-perfectly. There is deliberately no per-layer alignment math — alignment is guaranteed by
 * construction because everyone shares this transform.
 *
 * The glyph is scaled uniformly to fit the canvas and centered (letterboxed), matching how an SVG
 * with `preserveAspectRatio="xMidYMid meet"` would render.
 */
data class KanaViewport(
    val scale: Float,
    val offset: Offset,
    val viewBoxWidth: Float,
    val viewBoxHeight: Float
) {
    /** viewBox coordinate -> canvas pixel. */
    fun toCanvas(point: Offset): Offset =
        Offset(point.x * scale + offset.x, point.y * scale + offset.y)

    /** canvas pixel -> viewBox coordinate (used to interpret touch input against stroke geometry). */
    fun toViewBox(point: Offset): Offset =
        Offset((point.x - offset.x) / scale, (point.y - offset.y) / scale)

    companion object {
        fun fit(canvas: Size, viewBoxWidth: Float, viewBoxHeight: Float): KanaViewport {
            val scale = min(canvas.width / viewBoxWidth, canvas.height / viewBoxHeight)
            val offset = Offset(
                x = (canvas.width - viewBoxWidth * scale) / 2f,
                y = (canvas.height - viewBoxHeight * scale) / 2f
            )
            return KanaViewport(scale, offset, viewBoxWidth, viewBoxHeight)
        }
    }
}

/**
 * Runs [block] with the canvas transformed into viewBox space, so paths parsed straight from the
 * SVG can be drawn with their original coordinates. Stroke widths given in viewBox units scale with
 * the glyph automatically, keeping the brush weight resolution-independent.
 */
inline fun DrawScope.withKanaViewport(viewport: KanaViewport, block: DrawScope.() -> Unit) {
    withTransform({
        translate(viewport.offset.x, viewport.offset.y)
        scale(viewport.scale, viewport.scale, pivot = Offset.Zero)
    }, block)
}

/**
 * Parses a [KanaStrokes]' raw `d` strings into drawable [Path]s (in viewBox space), memoized so the
 * parse only happens when the stroke data actually changes. Malformed paths are skipped rather than
 * crashing the screen.
 */
@Composable
fun rememberKanaPaths(strokes: KanaStrokes): List<Path> =
    remember(strokes) {
        strokes.strokePaths.mapNotNull { d ->
            runCatching { PathParser().parsePathString(d).toPath() }.getOrNull()
        }
    }

/** Shared visual defaults so every page renders the brush consistently. */
object KanaGlyphDefaults {
    /** Brush thickness in viewBox units (KanjiVG medians are thin; ~6.5 reads as a soft brush). */
    const val STROKE_WIDTH = 6.5f

    /** Opacity of the faint tracing guide shown behind strokes on the trace/stroke/write pages. */
    const val GUIDE_ALPHA = 0.18f
}
