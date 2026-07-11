package com.codewithdipesh.sharedfeature.learning.lesson.components.kana

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.ui.components.buttons.customClickable
import com.codewithdipesh.kanasensei.ui.components.haptic.HapticManager
import com.codewithdipesh.kanasensei.ui.components.haptic.rememberHapticManager
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.tick_icon
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import org.jetbrains.compose.resources.painterResource


@Composable
fun QuizDrawingPad(
    strokes: KanaStrokes,
    paths: List<Path>,
    onComplete: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    resetKey: Int = 0,
    clickable: Boolean = true
) {
    val haptic = rememberHapticManager()
    val player = rememberAudioManager()

    val targets = remember(paths) {
        paths.map { p -> StrokeMatcher.sampleTarget(PathMeasure().apply { setPath(p, false) }) }
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var userStrokes by remember(resetKey, paths) { mutableStateOf<List<List<Offset>>>(emptyList()) }
    var currentStroke by remember(resetKey, paths) { mutableStateOf<List<Offset>>(emptyList()) }
    var isError by remember(resetKey, paths) { mutableStateOf(false) }
    var drawingCompleted by remember(resetKey, paths) { mutableStateOf(false) }
    var matches by remember(resetKey, paths) { mutableStateOf(false) }

    val errorAlpha = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            errorAlpha.snapTo(1f)
            errorAlpha.animateTo(0f, tween(1000))
            isError = false
            userStrokes = emptyList()
        }
    }

    LaunchedEffect(drawingCompleted,matches){
       if(drawingCompleted){
           if (matches) {
               player.playFinished()
               haptic.correctHaptic()
               onComplete(true)
           } else {
               player.playLockDenied()
               haptic.wrongHaptic()
               isError = true
               onComplete(false)
           }
           drawingCompleted = false
       }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(KanaColors.learningBackground)
            .border(1.5.dp, KanaColors.learningSurface, RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                .pointerInput(resetKey, clickable) {
                    if (!clickable) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset -> currentStroke = listOf(offset) },
                        onDrag = { change, _ ->
                            currentStroke = currentStroke + change.position
                            change.consume()
                        },
                        onDragEnd = {
                            if (currentStroke.size > 1) {
                                userStrokes = userStrokes + listOf(currentStroke)
                            }
                            currentStroke = emptyList()
                        },
                        onDragCancel = { currentStroke = emptyList() }
                    )
                }
        ) {
            val viewport = KanaViewport.fit(size, strokes.viewBoxWidth, strokes.viewBoxHeight)
            val inkWidth = KanaGlyphDefaults.STROKE_WIDTH * viewport.scale
            val inkBrush = Stroke(width = inkWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)

            userStrokes.forEach { stroke ->
                val color =
                    if (errorAlpha.value > 0f) KanaColors.error.copy(alpha = errorAlpha.value) else KanaColors.learningDrawing
                drawPath(polyline(stroke), color = color, style = inkBrush)
            }

            if (currentStroke.size > 1) {
                drawPath(
                    polyline(currentStroke),
                    color = KanaColors.learningDrawing,
                    style = inkBrush
                )
            }
        }

        if (clickable && userStrokes.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(KanaColors.onLearningBackground)
                    .customClickable {
                        drawingCompleted = true
                        matches = validate(canvasSize, strokes, userStrokes, targets)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.tick_icon),
                    contentDescription = "Check",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

fun validate(
    canvasSize: Size,
    strokes: KanaStrokes,
    userStrokes: List<List<Offset>>,
    targets: List<List<Offset>>
) : Boolean {
    if (canvasSize == Size.Zero || userStrokes.isEmpty()) return false
    val viewport = KanaViewport.fit(canvasSize, strokes.viewBoxWidth, strokes.viewBoxHeight)

    val matches = if (userStrokes.size != targets.size) {
        false
    } else {
        userStrokes.zip(targets).all { (user, target) ->
            val viewBoxUser = user.map { viewport.toViewBox(it) }
            StrokeMatcher.matches(viewBoxUser, target)
        }
    }

    return matches

}

private fun polyline(points: List<Offset>): Path = Path().apply {
    if (points.isEmpty()) return@apply
    moveTo(points.first().x, points.first().y)
    for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
}