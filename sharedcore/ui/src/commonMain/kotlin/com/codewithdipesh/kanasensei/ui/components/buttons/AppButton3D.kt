package com.codewithdipesh.kanasensei.ui.components.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppButton3D(
    label: String,
    modifier: Modifier = Modifier,
    height : Int = 50,
    onClick: () -> Unit,
    clickable: Boolean = true,
    labelSize : Int = 16,
    labelPadding : Int = 8,
    cornerRadius: Int = 16,
    xElevation: Int = -6,
    yElevation: Int = 6,
    backgroundColor: Color = KanaColors.primary,
    shadowColor: Color = KanaColors.shadowPrimary,
    contentColor: Color = Color.White
) {
    var pressed by remember { mutableStateOf(false) }
    val soundDelay: Long = 150L

    val player = rememberAudioManager()
    val scope = rememberCoroutineScope()


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
            .pointerInput(clickable) {
                detectTapGestures(
                    onPress = {
                        player.playTap()
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = {
                        if (clickable){
                           scope.launch {
                               delay(soundDelay)
                               onClick()
                           }
                        }
                    }
                )
            }
    ) {
        //shadow
        Box(
            modifier = modifier
                .height(height.dp)
                .offset(y = yElevation.dp , x = xElevation.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )

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
                style = MaterialTheme.typography.titleLarge.copy(
                    color = contentColor
                ),
                modifier = Modifier.padding(labelPadding.dp)
            )
        }
    }
}