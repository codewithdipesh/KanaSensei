package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.content.LessonPage

interface LearningRepository {
    suspend fun getLessonPages(lessonId : String) : List<LessonPage>

    suspend fun getLesson(lessonId: String) : Lesson?

    suspend fun getKana(kanaId : String): Character?

    /**
     * Fetches the raw KanjiVG SVG document at [svgUrl] (as text) so the UI can parse stroke
     * geometry. Returns null on any network/IO failure. Results are cached in-memory per session
     * to avoid refetching the same character while paging through a lesson.
     */
    suspend fun getKanaSvg(svgUrl : String): String?
}