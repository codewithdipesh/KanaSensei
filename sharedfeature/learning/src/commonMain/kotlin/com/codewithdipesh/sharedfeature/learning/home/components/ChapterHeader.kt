package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.progress.ChapterVisibility
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import androidx.compose.material3.MaterialTheme
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
fun ChapterHeader(
    chapterWithProgress: ChapterWithProgress
) {
    val chapter = chapterWithProgress.chapter
    val isSemiVisible = chapterWithProgress.visibility == ChapterVisibility.SEMI_VISIBLE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = chapter.name,
            style = MaterialTheme.typography.headlineLarge,
            color = if (isSemiVisible)
                KanaColors.onBackground.copy(0.5f)
            else
                KanaColors.onBackground
        )

        if (!isSemiVisible) {
            Text(
                text = "${chapterWithProgress.completedLessonsCount}/${chapterWithProgress.totalLessonsCount} lessons completed",
                style = MaterialTheme.typography.titleLarge,
                color = KanaColors.onBackground.copy(0.8f)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}