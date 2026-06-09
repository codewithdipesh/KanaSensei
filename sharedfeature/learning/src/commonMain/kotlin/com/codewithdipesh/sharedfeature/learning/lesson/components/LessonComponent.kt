package com.codewithdipesh.sharedfeature.learning.lesson.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.codewithdipesh.kanasensei.ui.components.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.core.model.content.LessonPageType
import com.codewithdipesh.kanasensei.core.model.content.LessonPageType.STROKE
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.components.buttons.customClickable
import com.codewithdipesh.sharedfeature.learning.lesson.components.kana.KanaStage
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.rememberAudioManager
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.close_icon
import com.codewithdipesh.kanasensei.ui.resources.eye
import com.codewithdipesh.kanasensei.ui.resources.replay
import com.codewithdipesh.kanasensei.ui.resources.sound_icon
import com.codewithdipesh.kanasensei.ui.resources.tick_icon
import com.codewithdipesh.kanasensei.ui.resources.turtle_sound
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LessonComponent(
    kana : Character,
    strokes : KanaStrokes,
    title : String,
    type : LessonPageType,
    onCancel : ()-> Unit,
    onContinue : () -> Unit
){
    val audioManager = rememberAudioManager()

    // STROKE: replay the animation. WRITE: reset progress. Reset when the page/kana changes.
    var replayKey by remember(strokes, type) { mutableStateOf(0) }
    // WRITE: faint guide visible behind the user's ink (toggled by the eye button).
    var showGuide by remember(strokes, type) { mutableStateOf(false) }
    // WRITE: becomes true once every stroke has been written correctly.
    var writeComplete by remember(strokes, type) { mutableStateOf(false) }

    val continueEnabled = type != LessonPageType.WRITE || writeComplete

    var showCancelDialog by remember { mutableStateOf(false) }

    BackHandler {
        showCancelDialog = true
    }

    LaunchedEffect(writeComplete){
        if(writeComplete) audioManager.playFinished()
    }

    Scaffold(
        containerColor = KanaColors.learningBackground,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KanaColors.learningBackground
                ),
                navigationIcon = {
                    IconButton(onClick = { showCancelDialog = true }) {
                        Icon(
                            painter = painterResource(Res.drawable.close_icon),
                            contentDescription = "Cancel lesson",
                            tint = KanaColors.onLearningBackground
                        )
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .border(
                                1.dp,
                                KanaColors.learningSurface,
                                RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = title,
                            style = TextStyle(
                                color = KanaColors.onLearningBackground,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            AnimatedVisibility(
                visible = type == LessonPageType.WRITE && writeComplete,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ){
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(200.dp)
                        .background(KanaColors.success.copy(0.5f)),
                    contentAlignment = Alignment.BottomStart
                ){
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 50.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(Res.drawable.tick_icon),
                            contentDescription = null,
                        )
                        Text(
                            text = "Nice Work!",
                            style = TextStyle(
                                color = KanaColors.background,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){
            TopElement(
                kana = kana.romaji,
                onPlay = { if(kana.audioUrl.isNotEmpty()) audioManager.playUrl(kana.audioUrl, speed = 1f) },
                onTirtlePlay = { if(kana.audioUrl.isNotEmpty()) audioManager.playUrl(kana.audioUrl, speed = 0.6f) }
            )
            Spacer(Modifier.height(16.dp))

            KanaStage(
                strokes = strokes,
                type = type,
                replayKey = replayKey,
                showGuide = showGuide,
                onWriteComplete = { writeComplete = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            BottomElement(
                type = type,
                onRepeat = {
                    // Replay the stroke animation from the start.
                    audioManager.playTap()
                    replayKey++
                },
                onShowTrace = {
                    // Toggle the faint guide on the write page.
                    audioManager.playTap()
                    showGuide = !showGuide
                }
            )

            Spacer(Modifier.height(64.dp))

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                label = "Continue",
                clickable = continueEnabled,
                onClick = {
                    audioManager.playTap()
                    onContinue()
                },
                backgroundColor = KanaColors.onLearningBackground,
                labelColor = Color.White
            )
        }
    }

    if (showCancelDialog) {
        CancelAlertDialog(
            onConfirm = {
                showCancelDialog = false
                onCancel()
            },
            onDismiss = { showCancelDialog = false }
        )
    }

}

@Composable
fun TopElement(
    kana: String,
    onPlay : () -> Unit,
    onTirtlePlay : () -> Unit
){
    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            //sound
            Box(
                modifier = Modifier
                    .customClickable(
                        onClick = onPlay
                    )
                    .size(53.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KanaColors.onLearningBackground),
                contentAlignment = Alignment.Center

            ){
                Icon(
                    painter = painterResource(Res.drawable.sound_icon),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            //slow sound
            Box(
                modifier = Modifier
                    .customClickable(
                        onClick = onTirtlePlay
                    )
                    .size(53.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KanaColors.onLearningBackground),
                contentAlignment = Alignment.Center

            ){
                Icon(
                    painter = painterResource(Res.drawable.turtle_sound),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        //title
        Text(
            text = kana,
            style = TextStyle(
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BottomElement(
    type : LessonPageType,
    onRepeat : () -> Unit,
    onShowTrace : () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        when(type){
            STROKE -> {
                //replay
                Box(
                    modifier = Modifier
                        .customClickable(
                            onClick = onRepeat
                        )
                        .size(53.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KanaColors.onLearningBackground),
                    contentAlignment = Alignment.Center

                ){
                    Icon(
                        painter = painterResource(Res.drawable.replay),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            LessonPageType.WRITE -> {
                //eye icon
                Box(
                    modifier = Modifier
                        .customClickable(
                            onClick = onShowTrace
                        )
                        .size(53.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KanaColors.onLearningBackground),
                    contentAlignment = Alignment.Center

                ){
                    Icon(
                        painter = painterResource(Res.drawable.eye),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            else -> {
                //quiz
                //todo
            }
        }
    }
}


@Composable
fun CancelAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KanaColors.learningBackground,
        title = {
            Text(
                text = "Cancel current learning?",
                style = TextStyle(
                    color = KanaColors.onLearningBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = "You will lose your current progress in this lesson.",
                style = TextStyle(
                    color = KanaColors.onLearningBackground,
                    fontSize = 16.sp
                )
            )
        },
        confirmButton = {
            Text(
                text = "Cancel lesson",
                style = TextStyle(
                    color = KanaColors.onLearningBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .customClickable(onClick = onConfirm)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        },
        dismissButton = {
            Text(
                text = "Keep learning",
                style = TextStyle(
                    color = KanaColors.onLearningBackground.copy(0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .customClickable(onClick = onDismiss)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    )
}