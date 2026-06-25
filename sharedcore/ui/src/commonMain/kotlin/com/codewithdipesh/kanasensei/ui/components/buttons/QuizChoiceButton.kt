package com.codewithdipesh.kanasensei.ui.components.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.ui.components.haptic.rememberHapticManager
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun QuizChoiceButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
    clickable : Boolean = true,
    isCorrect : Boolean = false,
    isRoundedCorner : Boolean = true,
    height : Int = 50,
    xElevation: Int = -6,
    yElevation: Int = 6,
    labelSize : Int = 16,
) {
    val vibrator = rememberHapticManager()
    val player = rememberAudioManager()
    val scope = rememberCoroutineScope()

    var clicked by rememberSaveable(label) { mutableStateOf(false) }

    val backgroundColor: Color = when {
        !clicked -> KanaColors.onLearningPrimary
        clicked && isCorrect -> KanaColors.success
        clicked && !isCorrect -> KanaColors.error
        else -> KanaColors.onLearningPrimary
    }

    val textColor: Color = when {
        clicked -> Color.White
        else -> KanaColors.onLearningBackground
    }

    val cornerRadius = if (isRoundedCorner) 16.dp else 0.dp

    //3d effect
    var pressed by remember { mutableStateOf(false) }
    val soundDelay = 150

    val offsetY by animateDpAsState(
        targetValue = if (pressed) yElevation.dp else 0.dp,
        label = ""
    )
    val offsetX by animateDpAsState(
        targetValue = if (pressed) xElevation.dp else 0.dp,
        label = ""
    )

    Box(
        modifier = modifier
            .pointerInput(true) {
                detectTapGestures(
                    onPress = {
                       if(clickable){ //diff behavoior ..first clicked anim liek press off then show result ( show sound and vibration )
                           pressed = true
                           tryAwaitRelease()
                           pressed = false
                       }
                    },
                    onTap = {
                        scope.launch {
                            if ( clickable && !clicked) {
                                if(isCorrect){
                                    player.playFinished()
                                    vibrator.correctHaptic()
                                }else{
                                    player.playLockDenied()
                                    vibrator.wrongHaptic()
                                }
                                delay(soundDelay.milliseconds)
                                clicked = true
                                onClick()
                            }
                        }
                    }
                )
            }
    ){
       //shadow only if it's not clicked
        if(!clicked){
            Box(
                modifier = modifier
                    .height(height.dp)
                    .offset(y = yElevation.dp , x = xElevation.dp)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(backgroundColor.copy(0.3f))
            )
        }

        //Main Button
        Box(
            modifier = modifier
                .height(height.dp)
                .offset(y = offsetY , x = offsetX)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = KanaSenseiTypography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = labelSize.sp,
                    color = textColor
                )
            )
        }
    }
}