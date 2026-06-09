package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.components.haptic.rememberHapticManager
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.AudioManager
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.components.text.Text3D
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.lesson_locked_tile
import com.codewithdipesh.kanasensei.ui.resources.lesson_tile
import com.codewithdipesh.kanasensei.ui.resources.tick_icon
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun LessonTile(
    lessonWithProgress: LessonWithProgress,
    isSelected: Boolean,
    onSelect: () -> Unit,
    showTickIcon : Boolean = false,
    modifier: Modifier = Modifier
) {
    val lesson = lessonWithProgress.lesson
    val hapticManager = rememberHapticManager()
    val audioManager = rememberAudioManager()
    val isFirstComposition = rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val tile = when {
        lessonWithProgress.isLocked -> Res.drawable.lesson_locked_tile
        else -> Res.drawable.lesson_tile
    }

    val scaleXCo = remember { Animatable(1f) }
    val scaleYCo = remember { Animatable(1f) }

    // pops in with a bounce when the tick is revealed after the completion popup is dismissed.
    val tickScale = remember { Animatable(if (showTickIcon) 1f else 0f) }
    LaunchedEffect(showTickIcon) {
        if (showTickIcon) {
            if (tickScale.value < 1f) {
                audioManager.playTick()
                hapticManager.softBounce()
                //pop
                tickScale.animateTo(1.8f,tween(200))
                //settle
                tickScale.animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                )
            }
        } else {
            tickScale.snapTo(0f)
        }
    }


    LaunchedEffect(isSelected) {
        //if already selected then no animation
        if (isFirstComposition.value) {
            if (isSelected) {
                scaleXCo.snapTo(1.15f)
                scaleYCo.snapTo(1.15f)
            }
            isFirstComposition.value = false
            return@LaunchedEffect
        }
        //if selected by tap ..then
        //a smooth vibration bouncy effect in UI
        if (isSelected) {
            // Pop
            scaleXCo.animateTo(1.18f, tween(50))
            scaleYCo.animateTo(1.18f, tween(50))

            // Settle
            scaleXCo.animateTo(
                1.15f,
                spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow)
            )
            scaleYCo.animateTo(
                1.15f,
                spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow)
            )
        } else {
            scaleXCo.animateTo(1f, spring())
            scaleYCo.animateTo(1f, spring())
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                scaleX = scaleXCo.value
                scaleY = scaleYCo.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ){
                if(lessonWithProgress.isLocked){
                    //locked so show jiggle anim -> vibration and sound of lock
                    audioManager.playLockDenied()
                    hapticManager.softBounce()
                    scope.launch {
                        //tap animation squish
                        scaleXCo.animateTo(0.96f, tween(100))
                        scaleYCo.animateTo(0.92f, tween(100)) // slight Y squash


                        scaleXCo.animateTo(1f, spring())
                        scaleYCo.animateTo(1f, spring())
                    }
                }else{
                   if(!isSelected){
                       audioManager.playTap()
                       onSelect()
                   }else{
                       onSelect()
                   }
                }
            }
    ) {

        Image(
            painter = painterResource(tile),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )

        if (!lessonWithProgress.isLocked) {

            Text(
                text = lesson.teaserText,
                style = TextStyle(
                    color = Color(0xFFFFECC2),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 20.dp)

            )

            if (tickScale.value > 0f) {
                Image(
                    painter = painterResource(Res.drawable.tick_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .graphicsLayer {
                            scaleX = tickScale.value
                            scaleY = tickScale.value
                        }
                )
            }
        }
    }
}