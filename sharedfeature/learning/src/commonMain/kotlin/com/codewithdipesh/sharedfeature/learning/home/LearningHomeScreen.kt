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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.blur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.codewithdipesh.sharedfeature.learning.home.uistates.GrievienceState
import com.codewithdipesh.sharedfeature.learning.home.components.GreivianceForm
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
    showGrievienceForm : Boolean = false,
    grievienceState: GrievienceState = GrievienceState(),
    onGrievienceClick : () -> Unit = {},
    onGrievienceTitleChange : (String) -> Unit = {},
    onGrievienceDescriptionChange : (String) -> Unit = {},
    onGrievienceMediaSelected : (androidx.compose.ui.graphics.ImageBitmap) -> Unit = {},
    onGrievienceMediaRemove : (Int) -> Unit = {},
    onGrievienceConfirm : () -> Unit = {},
    onGrievienceDismiss : () -> Unit = {},
    snackBarHost : @Composable () -> Unit
) {
    val lessons = remember(chapters){ chapters.flattenLessons() }
    val hazeState = remember { HazeState() }
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val lessonsPadding by animateIntAsState(
        targetValue = if(selectedLesson != null) 220 else 30,
        animationSpec = tween(400)
    )

    //focused and scrolled to the selected lesson ( for the first time )
    LaunchedEffect(isLoading,lessons) {
        if (!isLoading && selectedLesson != null && lessons.isNotEmpty()) {
            val index = lessons.indexOfFirst { it.lesson.id == selectedLesson.lesson.id }
            if (index != -1) {
                val viewportHeight = listState.layoutInfo.viewportSize.height
                if (viewportHeight > 0) {
                    val itemHeightPx = with(density) { 96.dp.toPx() }
                    val centerOffset = (viewportHeight / 2 - itemHeightPx / 2).toInt()
                    listState.animateScrollToItem(index, -centerOffset)
                } else {
                    listState.animateScrollToItem(index)
                }
            }
        }
    }


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
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .blur(if(isLoading) 5.dp else 0.dp)
                .padding(padding)
                .pointerInput(Unit) {
                    if(isLoading){
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
        ) {

            val screenWidth = maxWidth
            val tileSize = 96.dp
            val horizontalPadding = 30.dp
            val availableWidth = screenWidth - tileSize - (horizontalPadding * 2)

            Box(modifier = Modifier.fillMaxSize()) {

                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding)
                        .hazeSource(hazeState),
                    contentPadding = PaddingValues( bottom = lessonsPadding.dp )
                ) {

                    itemsIndexed(
                        items = lessons,
                        key = { _, lesson -> lesson.lesson.id },
                        contentType = { _, _ -> "lesson" }
                    ) { index, lesson ->

                        val isLessonSelected = selectedLesson?.lesson?.id == lesson.lesson.id
                        val isCompletedGated = lesson.isCompleted && pendingCompletion?.lessonId != lesson.lesson.id

                        val offsetFraction = calculateTileOffset(index)
                        val offset = availableWidth * offsetFraction

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(if (isLessonSelected) 10f else 0f)
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
                                isSelected = isLessonSelected,
                                onSelect = { onLessonSelect(lesson) },
                                showTickIcon = isCompletedGated,
                                modifier = Modifier
                                    .offset(x = offset)
                                    .align(Alignment.BottomStart)
                            )
                            //grass
                            if(index % 2 == 0){
                                val grassOffsetFraction = if((index/2) % 2 == 0) 0f else 1f
                                val grassOffset = availableWidth * grassOffsetFraction
                                val grassOffsetFix = if((index/2) % 2 == 0) (-36).dp else 36.dp
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
                            if (isLessonSelected) {

                                val (bubbleX, trianglePadding) = when {
                                    offsetFraction > 0.9f -> {
                                        // Right-most tile: align bubble right edge to the screen bounds,
                                        // and shift triangle pointer to the right to hit tile center
                                        Pair(availableWidth - 54.dp, 27.dp)
                                    }
                                    offsetFraction < 0.1f -> {
                                        // Left-most tile: align bubble left edge to the screen bounds,
                                        // and shift triangle pointer to the left to hit tile center
                                        Pair(0.dp, (-27).dp)
                                    }
                                    else -> {
                                        // Center tile: center bubble perfectly over the tile
                                        Pair(offset - 27.dp, 0.dp)
                                    }
                                }

                                AnimatedVisibility(
                                    visible = isLessonSelected,
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
                                            .offset(x = bubbleX, y = (-40).dp)
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
                        //startable when its completed or its current
                        isStartable = if(selectedLesson?.isCompleted == true) true else selectedLesson?.isCurrent == true,
                        onStart = {
                            selectedLesson?.let { onLessonStart(it) }
                        }
                    )
                }


                //topbar
                TopBar(
                    hazeState = hazeState,
                    onGrievanceClicked = onGrievienceClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }

            if(showGrievienceForm){
                GreivianceForm(
                    state = grievienceState,
                    onTitleChange = onGrievienceTitleChange,
                    onDescriptionChange = onGrievienceDescriptionChange,
                    onMediaSelected = onGrievienceMediaSelected,
                    onRemoveMedia = onGrievienceMediaRemove,
                    onConfirm = onGrievienceConfirm,
                    onDismiss = onGrievienceDismiss
                )
            }
        }
    }
}