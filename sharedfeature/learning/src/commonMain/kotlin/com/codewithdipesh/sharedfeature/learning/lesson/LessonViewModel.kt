package com.codewithdipesh.sharedfeature.learning.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.kanasensei.core.repository.LearningRepository
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

                val kanaDeferreds = kanaIds
                    .map {
                        async { repo.getKana(it) }
                    }
                val kanaList = kanaDeferreds.awaitAll()

                _state.update {
                    it.copy(
                       isLoading = false,
                       error = if (pagesResult.isEmpty()) "No lesson pages found" else null,
                       pages = pagesResult,
                       lesson = lessonResult,
                       kanas = kanaList,
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

}