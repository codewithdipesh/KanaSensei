package com.codewithdipesh.sharedfeature.learning.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.core.model.progress.ProgressUpdateResult
import com.codewithdipesh.kanasensei.core.model.user.User
import com.codewithdipesh.kanasensei.core.repository.FirebaseAuthRepository
import com.codewithdipesh.kanasensei.core.repository.LearningRepository
import com.codewithdipesh.kanasensei.core.repository.ProgressRepository
import com.codewithdipesh.kanasensei.core.svg.KanjiVgParser
import com.codewithdipesh.sharedfeature.learning.lesson.model.LessonCompletionResult
import com.codewithdipesh.sharedfeature.learning.lesson.model.LessonUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LessonViewModel(
    private val repo : LearningRepository,
    private val progressRepository : ProgressRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(LessonUiState())
    val state = _state.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    // forwards this back to the home screen (as a nav result) to drive the completion popup.
    private val _completionEvent = MutableSharedFlow<LessonCompletionResult>()
    val completionEvent = _completionEvent.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private var isProcessingNext = false
    private var isCompleting = false

    val networkStatus = connectivityObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = ConnectivityObserver.Status.Unavailable
        )

    init {
        viewModelScope.launch {
            _user.value = firebaseAuthRepository.currentUser()
        }
    }

    fun load(lessonId : String){
        viewModelScope.launch {
            isProcessingNext = false
            isCompleting = false
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                //pages and lesson detail
                val pages = async { repo.getLessonPages(lessonId) }
                val lesson = async { repo.getLesson(lessonId) }

                val pagesResult = pages.await()
                val lessonResult = lesson.await()

                print("LessonViewModel: loaded lessonId=$lessonId pages=${pagesResult.size} lesson=${lessonResult != null}")

                //load the kanas present there
                // INFO pages (and any non-kana page) carry a blank kanaId — skip them so we never
                // ask Firestore for an empty document path.
                val kanaIds = pagesResult
                    .mapNotNull { page ->
                        if (page.kanaId.isNotBlank()) {
                            page.kanaId
                        } else {
                            page.quizConfig?.source?.refIds?.firstOrNull()
                        }
                    }
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

                //complete status
                val complete = if (_user.value?.uid != null) {
                    progressRepository.isCompleted(_user.value!!.uid , lessonId)
                } else {
                    false
                }

                _state.update {
                    it.copy(
                       isLoading = false,
                       error = if (pagesResult.isEmpty()) "No lesson pages found" else null,
                       pages = pagesResult,
                       lesson = lessonResult,
                       kanas = kanaList,
                       kanaById = kanaById,
                       strokesById = strokesById,
                       isCompleted =  complete,
                       selectedPage = pagesResult.firstOrNull(),
                       currPage = if(pagesResult.isNotEmpty()) 1 else 0,
                       totalPage = pagesResult.size
                    )
                }
            }catch (e: Exception) {
                e.printStackTrace()

                println("ERROR TYPE = ${e::class.qualifiedName}")
                println("ERROR MSG = ${e.message}")

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
        if (isProcessingNext) return true
        
        val current = _state.value
        val index = current.pages.indexOf(current.selectedPage)
        if (index < 0 || index >= current.pages.lastIndex) return false
        
        isProcessingNext = true
        val nextPage = current.pages[index + 1]
        _state.update { it.copy(selectedPage = nextPage, currPage = index + 2) }
        
        // Debounce next call to prevent fast-click page skipping
        viewModelScope.launch {
            delay(400.milliseconds)
            isProcessingNext = false
        }
        
        return true
    }

    fun completeCurrentLesson(lessonId: String, chapterId: String) {
        if (isCompleting) return
        
        val uid = _user.value?.uid
        if(uid == null){
            viewModelScope.launch { _error.emit("Not signed in") }
            return
        }
        isCompleting = true
        viewModelScope.launch {
            if(!_state.value.isCompleted ){
                when (val result = progressRepository.completeLesson(_user.value!!.uid, lessonId, chapterId)) {
                    is ProgressUpdateResult.Success -> {
                        _completionEvent.emit(
                            LessonCompletionResult(
                                lessonId = lessonId,
                                shortDescription = _state.value.lesson?.shortDescription ?: "",
                                chapterCompleted = result.chapterCompleted,
                                advancedToNextChapter = result.advancedToNextChapter,
                                newChapterOrder = result.newCurrentChapter,
                                newLessonOrder = result.newCurrentLesson
                            )
                        )
                    }
                    else -> {
                        isCompleting = false
                        _error.emit("Failed to complete lesson")
                    }

                }
            } else {
                isCompleting = false
            }
        }

    }
    fun markKanaLearned(kanaId: String) {
        _user.value?.let {
            viewModelScope.launch {
                progressRepository.markKanaLearned(_user.value!!.uid, kanaId)
            }
        }
    }

}