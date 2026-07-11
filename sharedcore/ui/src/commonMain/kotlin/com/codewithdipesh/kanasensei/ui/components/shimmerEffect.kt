package com.codewithdipesh.kanasensei.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun Modifier.shimmerEffect(
    isVisible: Boolean,
    customWidth: Dp = 0.dp,
    customHeight: Dp = 0.dp,
    fillMaxWidth: Boolean = false,
    fillMaxHeight: Boolean = false,
): Modifier = composed {
    if (isVisible) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        val transition = rememberInfiniteTransition(label = "")
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1000)
            ), label = ""
        )
        background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFB8B5B5),
                    Color(0xFF8F8B8B),
                    Color(0xFFB8B5B5),
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
            ),
            shape = RoundedCornerShape(12.dp)
        )
            .onGloballyPositioned {
                size = it.size
            }
            .wrapContentSize()
            .then(if (customWidth != 0.dp) Modifier.width(customWidth) else Modifier)
            .then(if (customHeight != 0.dp) Modifier.height(customHeight) else Modifier)
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .then(if (fillMaxHeight) Modifier.fillMaxHeight() else Modifier)
    } else {
        Modifier
    }
}