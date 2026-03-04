package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress

@Composable
fun LessonTile(
    lesson: LessonWithProgress,
    modifier: Modifier = Modifier
) {

    val background = when {

        lesson.isCompleted -> Color(0xFFD4B07A)

        lesson.isCurrent -> Color(0xFF8A5A33)

        lesson.isLocked -> Color(0xFF9A9A9A)

        else -> Color(0xFFB07A45)

    }

    Box(
        modifier = modifier
            .size(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = lesson.lesson.title,
            fontSize = 22.sp
        )

        if (lesson.isLocked) {
            Icon(Icons.Default.Lock, null)
        }

        if (lesson.isCompleted) {
            Icon(Icons.Default.Check, null)
        }

    }

}