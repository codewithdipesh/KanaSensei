package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton3D
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
fun SelectedLessonPanel(
    lesson: LessonWithProgress?,
    onStart: (LessonWithProgress) -> Unit = {},
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val player = rememberAudioManager()
    if (lesson == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(KanaColors.overlayedContainer.copy(0.94f))
            .border(1.5.dp, KanaColors.primary.copy(0.22f), RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ){
                //nothing
                //it will save form mistap to background element
            }
    ) {
        //background blur via haze
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(22.dp))
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = KanaColors.overlayedContainer,
                        tints = listOf(
                            HazeTint(KanaColors.overlayedContainer.copy(alpha = 0.7f))
                        ),
                        blurRadius = 20.dp
                    )
                )
        )
        //content column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Lesson ${lesson.lesson.orderNumber}",
                style = TextStyle(
                    color = KanaColors.onOverlayedContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = lesson.lesson.expandedTitle,
                style = TextStyle(
                    color = KanaColors.onOverlayedContainer,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = lesson.lesson.detailedDescription,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = KanaColors.onOverlayedContainer.copy(alpha = 0.75f)
                )
            )
            Spacer(Modifier.height(8.dp))
            AppButton3D(
                modifier = Modifier.fillMaxWidth(),
                label = if(lesson.isCompleted) "Revise" else if (lesson.isCurrent) "Start" else "Start",
                onClick = {
                    player.playTap()
                } ,
                labelSize = 18,
                labelPadding = 8,
                clickable = lesson.isCurrent || lesson.isCompleted
            )
        }
    }
}