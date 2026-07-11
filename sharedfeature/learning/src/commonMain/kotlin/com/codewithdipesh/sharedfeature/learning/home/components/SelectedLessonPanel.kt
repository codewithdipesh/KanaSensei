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
import com.codewithdipesh.kanasensei.ui.components.buttons.customClickable
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.kanasensei.ui.theme.poppinsFontFamily

@Composable
fun SelectedLessonPanel(
    lesson: LessonWithProgress?,
    isStartable : Boolean,
    onStart: (LessonWithProgress) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (lesson == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(KanaColors.overlayedContainer)
    ) {
        //content column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = lesson.lesson.expandedTitle,
                style = TextStyle(
                    fontFamily = poppinsFontFamily(),
                    color = KanaColors.onOverlayedContainer,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = lesson.lesson.detailedDescription,
                style = TextStyle(
                    fontFamily = poppinsFontFamily(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = KanaColors.onSecondaryButton
                ),

                )
            Spacer(Modifier.height(12.dp))

            AppButton3D(
                modifier = Modifier
                    .fillMaxWidth(),
                label = "${ if (lesson.isCompleted) "Revise" else if (lesson.isCurrent) "Start" else "Finish Previous"} Learning",
                onClick = {
                    onStart(lesson)
                },
                backgroundColor = KanaColors.primary,
                contentColor = Color.White.copy(if(isStartable) 1f else 0.35f),
                clickable = isStartable,
                labelSize = 20,
                labelPadding = 8,
            )
        }
    }
}
