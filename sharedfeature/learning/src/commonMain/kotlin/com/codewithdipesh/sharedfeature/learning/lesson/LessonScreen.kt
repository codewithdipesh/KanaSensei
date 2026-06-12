package com.codewithdipesh.sharedfeature.learning.lesson

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.content.LessonPage
import com.codewithdipesh.kanasensei.core.model.content.LessonPageType.*
import com.codewithdipesh.sharedfeature.learning.lesson.components.LessonComponent
import com.codewithdipesh.sharedfeature.learning.lesson.components.LoadingScreen

@Composable
fun LessonScreen(
    modifier : Modifier = Modifier,
    isLoading : Boolean = true,
    error : String? = null,
    lessonPages: List<LessonPage>,
    kanas : List<Character?>,
    kanaById : Map<String, Character> = emptyMap(),
    strokesById : Map<String, KanaStrokes> = emptyMap(),
    selectedPage : LessonPage?,
    lessonTitle : String,
    totalPage : Int,
    currentPageNumber : Int,
    onClose : () -> Unit = {},
    onContinue : () -> Unit = {}
){
    // Stays true until LoadingScreen signals it's done (enforces the s minimum),
    var showLoading by rememberSaveable { mutableStateOf(true) }

    AnimatedContent(
        targetState = showLoading,
        transitionSpec = {
            fadeIn(tween(500)).togetherWith(
                fadeOut(tween(200))
            )
        }
    ){ loading ->
        when {
            loading -> LoadingScreen(
                isDataLoaded = !isLoading,
                onFinished = { showLoading = false }
            )
            selectedPage == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "No lesson content available")
                }
            }
            else -> {
                val kana = kanaById[selectedPage.kanaId] ?: Character()
                val strokes = strokesById[selectedPage.kanaId] ?: KanaStrokes()
                LessonComponent(
                    kana = kana,
                    strokes = strokes,
                    title = lessonTitle,
                    type = selectedPage.type,
                    infoContent = selectedPage.content,
                    onCancel = onClose,
                    onContinue = onContinue
                )
            }
        }
    }

}