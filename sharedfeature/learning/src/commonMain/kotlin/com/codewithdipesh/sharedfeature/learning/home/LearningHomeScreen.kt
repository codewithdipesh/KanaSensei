package com.codewithdipesh.sharedfeature.learning.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import com.codewithdipesh.sharedfeature.learning.home.components.ChapterItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHomeScreen(
    isLoading : Boolean,
    visibleChapters : List<ChapterWithProgress>,
    semiVisibleChapter : String?,
    onLessonStart : (LessonWithProgress) -> Unit,
    onLessonSelect : (LessonWithProgress) -> Unit,
    currentLessonWithProgress: LessonWithProgress,
    selectedLesson : LessonWithProgress
){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Learning",
                        style = KanaSenseiTypography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ){padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ){
            items(visibleChapters){ chapterWithProgress ->
                ChapterItem(
                    chapterWithProgress = chapterWithProgress,
                    onStartLesson = onLessonStart,
                    onSelectLesson = onLessonSelect,
                    selectedLesson = selectedLesson
                )

            }
        }
    }
}