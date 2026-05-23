package com.codewithdipesh.sharedfeature.learning.lesson

import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.KanaType
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.content.LessonPage

data class LessonUiState(
    val pages : List<LessonPage> = emptyList(),
    val lesson : Lesson? = null,
    val kanas : List<Character?> = emptyList(),
    val selectedPage : LessonPage? = null,
    val isLoading : Boolean = true,
    val error : String? = null,
    val totalPage : Int= 0,
    val currPage : Int= 0
)
