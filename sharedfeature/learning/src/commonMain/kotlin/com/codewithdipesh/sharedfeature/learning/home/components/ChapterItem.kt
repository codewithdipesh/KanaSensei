package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography

@Composable
fun ChapterItem(
    chapterWithProgress: ChapterWithProgress,
    onSelectLesson : (LessonWithProgress) -> Unit,
    onStartLesson : (LessonWithProgress) -> Unit,
    selectedLesson : LessonWithProgress
){
    val chapter = chapterWithProgress.chapter
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(
            text = chapter.name,
            style = KanaSenseiTypography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${chapterWithProgress.completedLessonsCount}/${chapterWithProgress.totalLessonsCount} lessons completed",
            style = KanaSenseiTypography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
        )
        Spacer(Modifier.height(24.dp))
        chapterWithProgress.lessons.forEach {
            LessonItem(
                lessonWithProgress = it,
                isCurrent = it.lesson.id == selectedLesson.lesson.id,
                onStartLesson = { onStartLesson(it) },
                onSelectLesson = { onSelectLesson(it) }
            )
        }

    }
}