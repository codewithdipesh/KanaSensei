package com.codewithdipesh.sharedfeature.learning.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.core.repository.LearningRepository
import com.codewithdipesh.kanasensei.core.svg.KanjiVgParser
import com.codewithdipesh.sharedfeature.learning.home.LearningUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LessonViewModel(
    private val repo : LearningRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LessonUiState())
    val state = _state.asStateFlow()

    fun load(lessonId : String){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                //pages and lesson detail
                val pages = async { repo.getLessonPages(lessonId) }
                val lesson = async { repo.getLesson(lessonId) }

                val pagesResult = pages.await()
                val lessonResult = lesson.await()

                println("LessonViewModel: loaded lessonId=$lessonId pages=${pagesResult.size} lesson=${lessonResult != null}")

                //load the kanas present there
                val kanaIds = pagesResult
                    .map { it.kanaId }
                    .distinct()

                val kanaList = kanaIds
                    .map { id -> async { repo.getKana(id) } }
                    .awaitAll()

                // Key characters by the kanaId each page references (Character.id may be blank).
                val kanaById = kanaIds.zip(kanaList)
                    .mapNotNull { (id, kana) -> kana?.let { id to it } }
                    .toMap()

                // Fetch + parse each kana's KanjiVG SVG into stroke geometry, in parallel.
                val strokesById = kanaById
                    .filterValues { it.svgUrl.isNotBlank() }
                    .map { (id, kana) ->
                        async {
                            val strokes = repo.getKanaSvg(kana.svgUrl)
                                ?.let { KanjiVgParser.parse(it) }
                                ?: KanaStrokes()
                            id to strokes
                        }
                    }
                    .awaitAll()
                    .toMap()

                _state.update {
                    it.copy(
                       isLoading = false,
                       error = if (pagesResult.isEmpty()) "No lesson pages found" else null,
                       pages = pagesResult,
                       lesson = lessonResult,
                       kanas = kanaList,
                       kanaById = kanaById,
                       strokesById = strokesById,
                       selectedPage = pagesResult.firstOrNull(),
                       currPage = if(pagesResult.isNotEmpty()) 1 else 0,
                       totalPage = pagesResult.size
                    )
                }
            } catch (e: Exception) {
                println("LessonViewModel: failed to load lessonId=$lessonId -> ${e.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load lesson"
                    )
                }
            }
        }
    }

    /** Advances to the next lesson page. Returns false (and stays put) if already on the last page. */
    fun next(): Boolean {
        val current = _state.value
        val index = current.pages.indexOf(current.selectedPage)
        if (index < 0 || index >= current.pages.lastIndex) return false
        val nextPage = current.pages[index + 1]
        _state.update { it.copy(selectedPage = nextPage, currPage = index + 2) }
        return true
    }

}