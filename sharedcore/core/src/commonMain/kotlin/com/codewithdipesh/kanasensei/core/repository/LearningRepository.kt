package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.content.LessonPage

interface LearningRepository {
    suspend fun getLessonPages(lessonId : String) : List<LessonPage>

    suspend fun getLesson(lessonId: String) : Lesson?

    suspend fun getKana(kanaId : String): Character?
}