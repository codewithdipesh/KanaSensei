package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.tick_icon
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import org.jetbrains.compose.resources.painterResource

@Composable
fun LessonItem(
    modifier : Modifier = Modifier,
    lessonWithProgress: LessonWithProgress,
    isCurrent : Boolean,
    onStartLesson: () -> Unit = {},
    onSelectLesson: () -> Unit = {}
){
    val lesson = lessonWithProgress.lesson

    val connectorHeight = remember(lesson.id , isCurrent){
        Animatable(16.dp, Dp.VectorConverter)
    }

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isCurrent) {
        if (isCurrent) {
            connectorHeight.animateTo(
                targetValue = 160.dp,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            connectorHeight.animateTo(
                targetValue = 16.dp,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }


    Column(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ){
                onSelectLesson()
            }
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.Start
    ){
        // short title and short desc with image/teaser text
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(
                        60.dp,
                        if(lessonWithProgress.isCompleted) 68.dp else 60.dp
                    )
            ){
                Box(
                    modifier = Modifier.size(60.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = if(lessonWithProgress.isCompleted) MaterialTheme.colorScheme.scrim
                            else MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lesson.teaserText,
                            style = KanaSenseiTypography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1
                        )
                    }
                }
                if(lessonWithProgress.isCompleted){
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.scrim)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .align(Alignment.BottomCenter),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painter = painterResource(Res.drawable.tick_icon),
                            contentDescription = "Completed",
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = lesson.title,
                    style = KanaSenseiTypography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                Text(
                    text = lesson.shortDescription,
                    style = KanaSenseiTypography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
            }
        }
        // Connector line and lesson card
        Row(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ){
            // Vertical connector line
            Box(
                modifier = Modifier.width(60.dp)
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ){
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(connectorHeight.value)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if(lessonWithProgress.isCompleted) MaterialTheme.colorScheme.scrim
                            else MaterialTheme.colorScheme.surface
                        )
                )
            }

            // Animated lesson detail card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(
                        animationSpec = tween(300)
                    )
            ) {
                if (isCurrent) {
                    LessonCard(
                        lesson = lesson,
                        onStartLesson = onStartLesson,
                        isCompleted = lessonWithProgress.isCompleted,
                        isLocked = lessonWithProgress.isLocked
                    )
                }
            }

        }
    }
}

@Composable
private fun LessonCard(
    lesson: Lesson,
    isCompleted : Boolean = false,
    isLocked : Boolean = true,
    onStartLesson: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = lesson.expandedTitle ?: "",
            style = KanaSenseiTypography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = lesson.detailedDescription,
            style = KanaSenseiTypography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
            maxLines = 2
        )

        // Start button
        AppButton(
            label =
                if(isCompleted) "Completed"
                else "Start",
            labelSize = 14,
            clickable = !isLocked,
            onClick = onStartLesson,
            modifier = Modifier.widthIn(min = 80.dp,max = 140.dp),
            isRoundedCorner = true,
            backgroundColor = if(isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            labelColor = if(isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.background
        )
    }
}

@Preview
@Composable
fun LessonPreview(){
    LessonItem(
        lessonWithProgress = LessonWithProgress(
            lesson = Lesson(
                id = "1",
                title = "Lesson 1",
                shortDescription = "Learn the basics",
                expandedTitle = "Lesson 1: Learn the basics",
                detailedDescription = "This is a detailed description of lesson 1. It will help you understand the importance of this lesson.",
                teaserText = "1"
            ),
            isCompleted = false,
            isLocked = false,
            isCurrent = true
        ),
        isCurrent = true
    )
}