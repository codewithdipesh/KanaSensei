package com.codewithdipesh.sharedfeature.learning.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.ChapterVisibility
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.flattenLessons
import com.codewithdipesh.kanasensei.ui.components.progressbar.AppLoadingIndicator
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import com.codewithdipesh.sharedfeature.learning.home.components.ChapterHeader
import com.codewithdipesh.sharedfeature.learning.home.components.LessonItem
import com.codewithdipesh.sharedfeature.learning.home.components.LessonTile
import com.codewithdipesh.sharedfeature.learning.home.components.calculateOffset
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.sharedfeature.learning.home.components.LessonBubble
import com.codewithdipesh.sharedfeature.learning.home.components.SNAKE_CURVE_SIZE
import com.codewithdipesh.sharedfeature.learning.home.components.SelectedLessonPanel
import com.codewithdipesh.sharedfeature.learning.home.components.TopBar

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
    val hazeState = remember { HazeState() }

    Scaffold(
        containerColor = KanaColors.background,
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

                val screenWidth = maxWidth
                val tileSize = 96.dp
                val horizontalPadding = 30.dp
                val availableWidth = screenWidth - tileSize - (horizontalPadding * 2)

                Box(modifier = Modifier.fillMaxSize()) {

                    LazyColumn(
                        reverseLayout = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding)
                            .hazeSource(hazeState),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {

                        itemsIndexed(lessons) { index, lesson ->

                            val offsetFraction = calculateOffset(index)
                            val offset = availableWidth * offsetFraction

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .zIndex(if (lesson == selectedLesson) 10f else 0f)
                            ) {

                                LessonTile(
                                    lessonWithProgress = lesson,
                                    isSelected = lesson == selectedLesson,
                                    onSelect = { onLessonSelect(lesson) },
                                    modifier = Modifier
                                        .offset(x = offset)
                                        .align(Alignment.BottomStart)
                                )

                                if (lesson == selectedLesson) {
                                    val bubbleOffset =
                                        if(index % SNAKE_CURVE_SIZE == 0) offset - 40.dp
                                        else if(index % SNAKE_CURVE_SIZE == SNAKE_CURVE_SIZE - 1) offset + 40.dp
                                        else offset

                                    val trianglePadding =
                                        if(index % SNAKE_CURVE_SIZE == 0) 24.dp
                                        else if(index % SNAKE_CURVE_SIZE == SNAKE_CURVE_SIZE - 1) (-24).dp
                                        else 0.dp

                                    AnimatedVisibility(
                                        visible = lesson == selectedLesson,
                                        enter = scaleIn(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            initialScale = 0.6f
                                        ) + fadeIn(animationSpec = tween(durationMillis = 200)),

                                        exit = scaleOut(
                                            animationSpec = tween(durationMillis = 300),
                                            targetScale = 0.6f
                                        ) + fadeOut(animationSpec = tween(durationMillis = 200))
                                    ){
                                        LessonBubble(
                                            title = lesson.lesson.title,
                                            description = lesson.lesson.shortDescription,
                                            trianglePadding = trianglePadding,
                                            modifier = Modifier
                                                .offset(x = -20.dp) //little adjustment
                                                .offset(x = bubbleOffset, y = (-40).dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SelectedLessonPanel(
                        lesson = selectedLesson,
                        onStart = { selectedLesson?.let { onLessonStart(it) } },
                        hazeState = hazeState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 90.dp)
                    )

                    //topbar
                    TopBar(
                        hazeState = hazeState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}