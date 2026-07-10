package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.completion_design
import com.codewithdipesh.kanasensei.ui.resources.tick_icon
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import org.jetbrains.compose.resources.painterResource


@Composable
fun LessonCompleteDialog(
    username : String?,
    lessonDetails : String? = null,
    isFirstLesson : Boolean = false,
    onContinue: () -> Unit
) {
    val contentScale = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        contentScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Dialog(
        onDismissRequest = onContinue,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = contentScale.value
                    scaleY = contentScale.value
                }
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.completion_design),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
                    .zIndex(2f)
            )
            Column(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(KanaColors.learningBackground)
                    .padding(horizontal = 28.dp, vertical = 32.dp)
                    .fillMaxWidth()
                    .zIndex(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Spacer(Modifier.height(16.dp))
                lessonDetails?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = KanaColors.onLearningBackground.copy(0.4f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "Congratulation ${username?: ""} ",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = KanaColors.onLearningBackground,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if(isFirstLesson) "First Lesson Complete!" else "Lesson Complete!",
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = KanaColors.onLearningBackground,
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "You nailed it , Keep going you're on a roll",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KanaColors.onLearningBackground.copy(alpha = 0.76f)
                    ),
                    textAlign = TextAlign.Center
                )

                AppButton(
                    label = "Continue",
                    onClick = onContinue,
                    backgroundColor = KanaColors.background,
                    labelColor = KanaColors.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
