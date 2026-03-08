package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
fun SelectedLessonPanel(
    lesson: LessonWithProgress?,
    onStart: (LessonWithProgress) -> Unit = {},
    modifier: Modifier = Modifier
) {

    if (lesson == null) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = KanaColors.primary.copy(alpha = 0.1f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Text(
                    text = lesson.lesson.expandedTitle,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = lesson.lesson.detailedDescription,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = KanaColors.onBackground.copy(alpha = 0.75f)
                    )
                )
            }

            AppButton(
                modifier = Modifier.width(150.dp),
                label = if(lesson.isCompleted) "Revise" else "Start",
                onClick = { } , //todo
                backgroundColor = KanaColors.primary,
                labelColor = KanaColors.onPrimary,
                isRoundedCorner = true
            )
        }
    }
}