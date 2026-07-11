package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.content.Chapter
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
fun ChapterDetails(
    chapterWithProgress : ChapterWithProgress,
    modifier : Modifier = Modifier
){
    val progress = (chapterWithProgress.completedLessonsCount / chapterWithProgress.totalLessonsCount) * 1.0f
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KanaColors.secondaryBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "Chapter ${chapterWithProgress.chapter.orderNumber}",
            style = TextStyle(
                color = Color.White.copy(0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = chapterWithProgress.chapter.name,
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(Modifier.height(10.dp))
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(KanaColors.primary.copy(0.4f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(KanaColors.primary)
            )
        }

    }
}