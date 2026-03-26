package com.codewithdipesh.kanasensei.ui.components.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography

@Composable
fun AppButton3D(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clickable: Boolean = true,
    labelSize : Int = 16,
    labelPadding : Int = 8,
    cornerRadius: Int = 16,
    xElevation: Int = -4,
    yElevation: Int = 6,
    backgroundColor: Color = KanaColors.primary,
    shadowColor: Color = KanaColors.shadowPrimary,
    contentColor: Color = Color.White
) {
    var pressed by remember { mutableStateOf(false) }

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
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = {
                        if (clickable){
                            pressed = true
                            onClick()
                            pressed = false
                        }
                    }
                )
            }
    ) {
        //shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = yElevation.dp , x = xElevation.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )

         //Main Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    color = contentColor
                ),
                modifier = Modifier.padding(labelPadding.dp)
            )
        }
    }
}