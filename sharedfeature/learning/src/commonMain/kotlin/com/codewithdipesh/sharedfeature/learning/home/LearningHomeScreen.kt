package com.codewithdipesh.sharedfeature.learning.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.ChapterVisibility
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.flattenLessons
import com.codewithdipesh.kanasensei.ui.components.progressbar.AppLoadingIndicator
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import com.codewithdipesh.sharedfeature.learning.home.components.ChapterHeader
import com.codewithdipesh.sharedfeature.learning.home.components.LessonItem
import com.codewithdipesh.sharedfeature.learning.home.components.LessonTile
import com.codewithdipesh.sharedfeature.learning.home.components.calculateOffset
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.sharedfeature.learning.home.components.SelectedLessonPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHomeScreen(
    isLoading: Boolean,
    chapters: List<ChapterWithProgress>,
    selectedLesson: LessonWithProgress?,
    onLessonStart: (LessonWithProgress) -> Unit,
    onLessonSelect: (LessonWithProgress) -> Unit,
    snackBarHost : @Composable () -> Unit
) {
    val lessons = chapters.flattenLessons()

    Scaffold(
        containerColor = KanaColors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Learning",
                        style = KanaSenseiTypography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = KanaColors.onBackground
                )
            )
        },
        snackbarHost = snackBarHost,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                AppLoadingIndicator()
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val tileSize = 96.dp
                val horizontalPadding = 30.dp
                val availableWidth = maxWidth - tileSize - (horizontalPadding * 2)

                Box(modifier = Modifier.fillMaxSize()){
                    LazyColumn(
                        reverseLayout = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding),
                        contentPadding = PaddingValues(bottom = 130.dp)
                    ) {

                        itemsIndexed(lessons) { index, lesson ->

                            val offsetFraction = calculateOffset(index)

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LessonTile(
                                    lessonWithProgress = lesson,
                                    isSelected = lesson == selectedLesson,
                                    onSelect = { onLessonSelect(lesson) },
                                    modifier = Modifier.offset(
                                        x = availableWidth * offsetFraction
                                    )
                                )
                            }
                        }
                    }

                    SelectedLessonPanel(
                        lesson = selectedLesson,
                        onStart = { lesson ->
                            onLessonStart(lesson)
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .height(110.dp)
                    )

                }
            }
        }
    }
}