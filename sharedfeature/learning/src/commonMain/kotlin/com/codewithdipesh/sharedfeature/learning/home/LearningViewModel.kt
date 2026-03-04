package com.codewithdipesh.sharedfeature.learning.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.model.progress.ProgressUpdateResult
import com.codewithdipesh.kanasensei.core.model.progress.SyncResult
import com.codewithdipesh.kanasensei.core.model.user.User
import com.codewithdipesh.kanasensei.core.repository.FirebaseAuthRepository
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
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val TAG = "LearningViewModel"

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isUserLoaded = MutableStateFlow(false)
    val isUserLoaded = _isUserLoaded.asStateFlow()

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
        viewModelScope.launch {
            _user.value = firebaseAuthRepository.currentUser()
            _isUserLoaded.value = true
            if (_user.value != null) {
                initializeContent()
                observeNetworkForSync()
            }
        }
    }

    private fun initializeContent() {
        viewModelScope.launch {
            // Sync content from Firestore if needed

            Napier.d("hasLocalContent: ${contentSyncManager.hasLocalContent()}", tag = "ProgressRepo")

            if (!contentSyncManager.hasLocalContent()) {
                _uiState.update { it.copy(isLoading = true) }
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
            progressRepository.initializeProgress(_user.value!!.uid)

            // Sync from cloud to get latest progress
            val syncFromResult = progressRepository.syncFromCloud(_user.value!!.uid)
            Napier.d("syncFromCloud result: $syncFromResult", tag = TAG)

            // Push any unsynced local progress to cloud
            val syncToResult = progressRepository.syncToCloud(_user.value!!.uid)
            Napier.d("syncToCloud result: $syncToResult", tag = TAG)

            // Start observing chapters with lessons
            observeChapters()
        }
    }

    private fun observeChapters() {
        viewModelScope.launch {
            progressRepository.observeChaptersWithProgress(_user.value!!.uid)
                .catch { e ->
                    Napier.e("Error loading chapters", e, TAG)
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { chapters ->
                    _uiState.update { state ->
                        state.copy(
                            chapters = chapters,
                            selectedLessonId = chapters.map { //find in chapters
                                it.lessons.find { it.isCurrent } //where lesson isCurrent is true
                            }.first()?.lesson?.id,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun selectLesson(lessonId: String?) {
       if(lessonId != null){
           _uiState.update { it.copy(selectedLessonId = lessonId) }
       }
    }

    fun completeCurrentLesson(lessonId: String, chapterId: String) {
        viewModelScope.launch {
            when (val result = progressRepository.completeLesson(_user.value!!.uid, lessonId, chapterId)) {
                is ProgressUpdateResult.Success -> {
                    _events.emit(
                        LearningEvent.LessonCompleted(
                            chapterCompleted = result.chapterCompleted,
                            advancedToNextChapter = result.advancedToNextChapter,
                            newChapterOrder = result.newCurrentChapter,
                            newLessonOrder = result.newCurrentLesson
                        )
                    )
                }
                is ProgressUpdateResult.Error -> {
                    _events.emit(LearningEvent.Error(result.message))
                }
            }
        }
    }

    fun markKanaLearned(kanaId: String) {
        viewModelScope.launch {
            progressRepository.markKanaLearned(_user.value!!.uid, kanaId)
        }
    }

    fun refreshFromCloud() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncStatus = SyncStatus.Syncing) }

            // Refresh content
            contentSyncManager.syncChaptersAndLessons()

            // Refresh progress
            when (progressRepository.syncFromCloud(_user.value!!.uid)) {
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
                    progressRepository.syncToCloud(_user.value!!.uid)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun calculateOffset(index : Int) : Float {
        val curveRight = listOf(
            0.40f,
            0.25f,
            0.55f,
            0.75f,
            0.90f
        )
        val curveLeft = listOf(
            0.85f,
            0.65f,
            0.45f,
            0.25f,
            0.10f
        )
        val segmentSize = 5

        val segment = index / segmentSize
        val pos = index % segmentSize

        val pattern = if(segment % 2 == 0) curveRight else curveRight
        return pattern[pos]
    }
}
