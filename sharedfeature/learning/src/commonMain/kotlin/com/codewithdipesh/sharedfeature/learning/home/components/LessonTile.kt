package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.components.haptic.rememberHapticManager
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.lesson_locked_tile
import com.codewithdipesh.kanasensei.ui.resources.lesson_tile
import com.codewithdipesh.kanasensei.ui.resources.lock_icon
import com.codewithdipesh.kanasensei.ui.resources.tick_icon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun LessonTile(
    lessonWithProgress: LessonWithProgress,
    isSelected: Boolean,
    onSelect : () -> Unit,
    modifier: Modifier = Modifier
) {
    val lesson = lessonWithProgress.lesson
    val hapticManager = rememberHapticManager()
    val isFirstComposition = rememberSaveable { mutableStateOf(true) }

    val tile = when {
        lessonWithProgress.isLocked -> Res.drawable.lesson_locked_tile
        else -> Res.drawable.lesson_tile
    }

    val scale = remember { Animatable(1f) }

    LaunchedEffect(isSelected) {
        //if already selected then no animation
        if (isFirstComposition.value) {

            if (isSelected) {
                scale.snapTo(1.15f)
            }

            isFirstComposition.value = false
            return@LaunchedEffect
        }
        //if selected by tap ..then
        //a smooth vibration bouncy effect in UI
        if (isSelected) {

            hapticManager.softBounce()

            scale.animateTo(
                1.24f,
                animationSpec = tween(
                    durationMillis = 60,
                    easing = FastOutLinearInEasing
                )
            )

            scale.animateTo(
                1.15f,
                animationSpec = spring(
                    dampingRatio = 0.55f,
                    stiffness = Spring.StiffnessLow
                )
            )

        } else {
            //not selected then as it is
            scale.animateTo(
                1f,
                animationSpec = spring()
            )

        }
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ){
                if(!lessonWithProgress.isLocked) onSelect()
            }
    ){
        Image(
            painter = painterResource(tile),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
        if(lessonWithProgress.isLocked){
            Icon(
                painter = painterResource(Res.drawable.lock_icon),
                tint = Color.White,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
                    .padding(bottom = 20.dp)
            )
        }else{
            Text(
                text = lesson.teaserText,
                style = TextStyle(
                    color = Color(0xFFFFECC2),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.Center)
                    .padding(bottom = 20.dp)
            )
            if(lessonWithProgress.isCompleted){
                //tick icon
                Image(
                    painter = painterResource(Res.drawable.tick_icon),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }

    }

}