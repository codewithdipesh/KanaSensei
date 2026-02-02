package com.codewithdipesh.sharedfeature.learning.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.model.progress.ProgressUpdateResult
import com.codewithdipesh.kanasensei.core.model.progress.SyncResult
import com.codewithdipesh.kanasensei.core.repository.ProgressRepository
import com.codewithdipesh.kanasensei.core.sync.ContentSyncManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LearningViewModel(
    private val progressRepository: ProgressRepository,
    private val contentSyncManager: ContentSyncManager,
    private val connectivityObserver: ConnectivityObserver,
    private val userId: String
) : ViewModel() {

    private val TAG = "LearningViewModel"

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LearningEvent>()
    val events = _events.asSharedFlow()

    val networkStatus = connectivityObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = ConnectivityObserver.Status.Unavailable
        )

    init {
        initializeContent()
        observeNetworkForSync()
    }

    private fun initializeContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Sync content from Firestore if needed
            if (!contentSyncManager.hasLocalContent()) {
                val synced = contentSyncManager.syncChaptersAndLessons()
                if (!synced) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load content. Please check your connection."
                        )
                    }
                    return@launch
                }
            }

            // Initialize user progress
            progressRepository.initializeProgress(userId)

            // Sync from cloud to get latest progress
            progressRepository.syncFromCloud(userId)

            // Start observing
            loadProgress()
        }
    }

    private fun loadProgress() {
        viewModelScope.launch {
            progressRepository.observeChaptersWithProgress(userId)
                .catch { e ->
                    Napier.e("Error loading chapters", e, TAG)
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { chapters ->
                    val currentChapter = chapters.find { it.isCurrent }
                    _uiState.update { state ->
                        state.copy(
                            chapters = chapters,
                            currentChapterOrder = currentChapter?.chapter?.orderNumber ?: 1,
                            selectedChapterId = state.selectedChapterId ?: currentChapter?.chapter?.id,
                            isLoading = false
                        )
                    }

                    // Load lessons for selected/current chapter
                    val chapterToLoad = _uiState.value.selectedChapterId
                    if (chapterToLoad != null) {
                        loadLessonsForChapter(chapterToLoad)
                    }
                }
        }
    }

    private fun loadLessonsForChapter(chapterId: String) {
        viewModelScope.launch {
            progressRepository.observeLessonsWithProgress(userId, chapterId)
                .catch { e ->
                    Napier.e("Error loading lessons", e, TAG)
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { lessons ->
                    val currentLesson = lessons.find { it.isCurrent }
                    _uiState.update { state ->
                        state.copy(
                            currentChapterLessons = lessons,
                            currentLessonOrder = currentLesson?.lesson?.orderNumber ?: 1
                        )
                    }
                }
        }
    }

    fun selectChapter(chapterId: String) {
        _uiState.update { it.copy(selectedChapterId = chapterId) }
        loadLessonsForChapter(chapterId)
    }

    fun completeCurrentLesson(lessonId: String, chapterId: String) {
        viewModelScope.launch {
            when (val result = progressRepository.completeLesson(userId, lessonId, chapterId)) {
                is ProgressUpdateResult.Success -> {
                    _events.emit(
                        LearningEvent.LessonCompleted(
                            chapterCompleted = result.chapterCompleted,
                            advancedToNextChapter = result.advancedToNextChapter,
                            newChapterOrder = result.newCurrentChapter,
                            newLessonOrder = result.newCurrentLesson
                        )
                    )

                    // If advanced to new chapter, update selected chapter
                    if (result.advancedToNextChapter) {
                        val newChapter = _uiState.value.chapters
                            .find { it.chapter.orderNumber == result.newCurrentChapter }
                        newChapter?.let { selectChapter(it.chapter.id) }
                    }
                }
                is ProgressUpdateResult.Error -> {
                    _events.emit(LearningEvent.Error(result.message))
                }
            }
        }
    }

    fun markKanaLearned(kanaId: String) {
        viewModelScope.launch {
            progressRepository.markKanaLearned(userId, kanaId)
        }
    }

    fun refreshFromCloud() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncStatus = SyncStatus.Syncing) }

            // Refresh content
            contentSyncManager.syncChaptersAndLessons()

            // Refresh progress
            when (progressRepository.syncFromCloud(userId)) {
                is SyncResult.Success -> {
                    _uiState.update { it.copy(syncStatus = SyncStatus.Success) }
                    _events.emit(LearningEvent.SyncCompleted)
                }
                is SyncResult.Error -> {
                    _uiState.update { it.copy(syncStatus = SyncStatus.Error) }
                }
                else -> {
                    _uiState.update { it.copy(syncStatus = SyncStatus.Idle) }
                }
            }
        }
    }

    private fun observeNetworkForSync() {
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    progressRepository.syncToCloud(userId)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}