package com.codewithdipesh.ui.components.progressbar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalProgressBar(
    modifier: Modifier = Modifier,
    size : Int,
    currentPosition : Int,
    selectedColor : Color = MaterialTheme.colorScheme.onBackground,
    unSelectedColor : Color = MaterialTheme.colorScheme.secondary,
) {
    var previousPosition by remember { mutableStateOf(currentPosition) }

    val shouldAnimate = currentPosition > previousPosition

    val animatedIndex by animateFloatAsState(
        targetValue = if (shouldAnimate) currentPosition.toFloat() else previousPosition.toFloat(),
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(currentPosition) {
        previousPosition = currentPosition
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        (0..size - 1).forEach { index ->

            val fill = when {
                index < previousPosition -> 1f            // already past → filled instantly
                index == previousPosition && shouldAnimate -> animatedIndex - previousPosition + 0f
                index == currentPosition && shouldAnimate -> animatedIndex - (currentPosition - 1)
                else -> 0f
            }.coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .height(5.dp)
                    .weight(1f)
                    .background(
                        if (fill > 0f) selectedColor else unSelectedColor
                    )
            )
        }
    }
}