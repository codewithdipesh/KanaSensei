package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.core.model.content.LessonPageType
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

/**
 * The central kana card shared by every lesson page. It draws the cream card chrome and, inside it,
 * the stroke layers appropriate for [type]:
 *
 *  - LISTEN — the full-opacity glyph.
 *  - TRACE  — the same glyph as a faint tracing guide.
 *  - STROKE — faint guide + animated stroke order (Phase 2).
 *  - WRITE  — faint guide + handwriting capture & matching (Phase 3).
 *
 * Because all layers share [KanaViewport], everything overlays the guide pixel-perfectly.
 */
@Composable
fun KanaStage(
    strokes: KanaStrokes,
    type: LessonPageType,
    modifier: Modifier = Modifier,
    // STROKE: bump to replay the animation. WRITE: bump to reset the user's writing.
    replayKey: Int = 0,
    // WRITE: whether the faint guide is shown behind the user's ink (toggled by the eye button).
    showGuide: Boolean = true,
    // WRITE: reports completion so the screen can enable Continue / show "Nice Work!".
    onWriteComplete: () -> Unit = {},
) {
    val paths = rememberKanaPaths(strokes)
    val glyphModifier = Modifier.fillMaxSize().padding(24.dp)

    @Composable
    fun guide() = KanaGlyph(
        paths = paths,
        viewBoxWidth = strokes.viewBoxWidth,
        viewBoxHeight = strokes.viewBoxHeight,
        color = KanaColors.learningDrawing,
        alpha = KanaGlyphDefaults.GUIDE_ALPHA,
        modifier = glyphModifier
    )

    KanaCard(modifier) {
        when (type) {
            LessonPageType.LISTEN -> KanaGlyph(
                paths = paths,
                viewBoxWidth = strokes.viewBoxWidth,
                viewBoxHeight = strokes.viewBoxHeight,
                color = KanaColors.learningDrawing,
                modifier = glyphModifier
            )

            // Faint guide + the strokes drawn on, one at a time, in order.
            LessonPageType.STROKE -> {
                guide()
                KanaStrokeAnimator(
                    paths = paths,
                    viewBoxWidth = strokes.viewBoxWidth,
                    viewBoxHeight = strokes.viewBoxHeight,
                    color = KanaColors.learningDrawing,
                    replayKey = replayKey,
                    modifier = glyphModifier
                )
            }

            // Optional faint guide + handwriting capture matched against the same strokes.
            LessonPageType.WRITE -> {
                if (showGuide) guide()
                KanaWritePad(
                    strokes = strokes,
                    paths = paths,
                    color = KanaColors.learningDrawing,
                    resetKey = replayKey,
                    onComplete = onWriteComplete,
                    modifier = glyphModifier
                )
            }

            LessonPageType.QUIZ -> Unit

            LessonPageType.INFO -> Unit
        }
    }
}

/** The cream rounded card the kana sits in. Square, so the glyph keeps its aspect ratio. */
@Composable
private fun KanaCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(KanaColors.learningBackground)
            .border(1.dp, KanaColors.learningSurface, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
        content = content
    )
}
