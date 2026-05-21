package com.codewithdipesh.sharedfeature.learning.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.content.LessonPage
import com.codewithdipesh.kanasensei.core.model.content.LessonPageType.*
import com.codewithdipesh.sharedfeature.learning.lesson.components.LoadingScreen

@Composable
fun LessonScreen(
    modifier : Modifier = Modifier,
    isLoading : Boolean = true,
    lessonPage: LessonPage,
    kana : Character,
    lessonTitle : String
){
    AnimatedVisibility(
        isLoading,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(200))
    ){
        LoadingScreen()
    }
    if(!isLoading){
        Box(Modifier.fillMaxSize())
    }
}