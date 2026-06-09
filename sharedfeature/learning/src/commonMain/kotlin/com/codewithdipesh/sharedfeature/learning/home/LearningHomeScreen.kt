package com.codewithdipesh.sharedfeature.learning.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.flattenLessons
import com.codewithdipesh.kanasensei.ui.components.progressbar.AppLoadingIndicator
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.grass
import com.codewithdipesh.kanasensei.ui.resources.tile_shadow
import com.codewithdipesh.sharedfeature.learning.home.components.LessonTile
import com.codewithdipesh.sharedfeature.learning.home.components.calculateTileOffset
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.sharedfeature.learning.home.components.ChapterDetails
import com.codewithdipesh.sharedfeature.learning.home.components.LessonBubble
import com.codewithdipesh.sharedfeature.learning.home.components.SNAKE_CURVE_SIZE
import com.codewithdipesh.sharedfeature.learning.home.components.SelectedLessonPanel
import com.codewithdipesh.sharedfeature.learning.home.components.TopBar
import com.codewithdipesh.sharedfeature.learning.home.components.calculatePropOffset
import com.codewithdipesh.sharedfeature.learning.home.components.getProp
import com.codewithdipesh.sharedfeature.learning.home.components.getPropOffsetFix
import com.codewithdipesh.sharedfeature.learning.lesson.model.LessonCompletionResult
import org.jetbrains.compose.resources.painterResource

//tile shadow
//tile
//grass
//props
//bubble selected

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHomeScreen(
    isLoading: Boolean,
    chapters: List<ChapterWithProgress>,
    selectedLesson: LessonWithProgress?,
    onLessonStart: (LessonWithProgress) -> Unit,
    onLessonSelect: (LessonWithProgress) -> Unit,
    pendingCompletion : LessonCompletionResult?,
    snackBarHost : @Composable () -> Unit
) {
    val lessons = chapters.flattenLessons()
    val hazeState = remember { HazeState() }

    val lessonsPadding by animateIntAsState(
        targetValue = if(selectedLesson != null) 220 else 30,
        animationSpec = tween(400)
    )

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
                        contentPadding = PaddingValues( bottom = lessonsPadding.dp )
                    ) {

                        itemsIndexed(lessons) { index, lesson ->

                            val offsetFraction = calculateTileOffset(index)
                            val offset = availableWidth * offsetFraction

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .zIndex(if (lesson == selectedLesson) 10f else 0f)
                            ) {

                                //tile shadow
                                Icon(
                                    painter = painterResource(Res.drawable.tile_shadow),
                                    contentDescription = "tile shadow",
                                    tint = Color.Black.copy(0.8f),
                                    modifier = Modifier
                                        .offset(x = offset,y = 28.dp)
                                        .align(Alignment.BottomStart)
                                )
                                //tile
                                LessonTile(
                                    lessonWithProgress = lesson,
                                    isSelected = lesson == selectedLesson,
                                    onSelect = { onLessonSelect(lesson) },
                                    showTickIcon = lesson.isCompleted  && pendingCompletion?.lessonId != lesson.lesson.id,
                                    modifier = Modifier
                                        .offset(x = offset)
                                        .align(Alignment.BottomStart)
                                )
                                //grass
                                if(index % 2 == 0){
                                    val grassOffsetFraction = if((index/2) % 2 == 0) 0f else 1f
                                    val grassOffset = availableWidth * grassOffsetFraction
                                    val grassOffsetFix = if((index/2) % 2 == 0) -36.dp else 36.dp
                                    val yRotation = if((index/2) % 2 == 0) 0f else 180f

                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .wrapContentSize(
                                                align = Alignment.BottomStart,
                                                unbounded = true
                                            )
                                    ){
                                        Image(
                                            painter = painterResource(Res.drawable.grass),
                                            contentDescription = "grass",
                                            modifier = Modifier
                                                .offset(x = grassOffset + grassOffsetFix )
                                                .graphicsLayer{
                                                    rotationY = yRotation
                                                }
                                        )
                                    }

                                }
                                //props - wrapped so it doesn't inflate row height
                                if(index % 2 == 0){
                                    val propOffsetFraction = calculatePropOffset(index)
                                    if(propOffsetFraction != null){
                                        val propOffset = availableWidth * propOffsetFraction
                                        val k = (index - 2) / 6
                                        val yRotation = if(k % 2 == 0) 0f else 180f
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .wrapContentSize(
                                                    align = Alignment.BottomStart,
                                                    unbounded = true
                                                )
                                        ) {
                                            Image(
                                                painter = painterResource(getProp(index)),
                                                contentDescription = "prop",
                                                modifier = Modifier
                                                    .offset(x = propOffset + getPropOffsetFix(index).dp, y = 24.dp)
                                                    .graphicsLayer{
                                                        rotationY = yRotation
                                                    }
                                            )
                                        }
                                    }
                                }
                                //selected bubble on top
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

                    AnimatedVisibility(
                        visible = selectedLesson != null,
                        enter = fadeIn(tween(600)),
                        exit = fadeOut(tween(600)),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ){
                        SelectedLessonPanel(
                            lesson = selectedLesson,
                            onStart = { selectedLesson?.let { onLessonStart(it) } }
                        )
                    }

//                    //chapter details
//                    ChapterDetails(
//                        chapterWithProgress = chapters.first(), //todo :for now
//                        modifier = Modifier
//                            .align(Alignment.BottomCenter)
//                    )


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