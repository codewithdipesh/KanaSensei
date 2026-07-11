package com.codewithdipesh.sharedfeature.learning.home

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.SyncResult
import com.codewithdipesh.kanasensei.core.model.user.User
import com.codewithdipesh.kanasensei.core.repository.FirebaseAuthRepository
import com.codewithdipesh.kanasensei.core.repository.ProgressRepository
import com.codewithdipesh.kanasensei.core.sync.ContentSyncManager
import com.codewithdipesh.sharedfeature.learning.home.uistates.GrievienceState
import com.codewithdipesh.sharedfeature.learning.home.uistates.LearningEvent
import com.codewithdipesh.sharedfeature.learning.home.uistates.LearningUiState
import com.codewithdipesh.sharedfeature.learning.home.uistates.SyncStatus
import com.codewithdipesh.sharedfeature.learning.home.util.toByteArray
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
            val hasLocal = contentSyncManager.hasLocalContent()
            Napier.d("hasLocalContent: $hasLocal", tag = "ProgressRepo")

            // Show loading only on first launch (no cached data)
            if (!hasLocal) {
                _uiState.update { it.copy(isLoading = true) }
            }

            // Always sync from Firestore to pick up updated content
            val synced = contentSyncManager.syncChaptersAndLessons()
            if (!synced && !hasLocal) {
                // Only block if we have no cached fallback
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load content. Please check your connection."
                    )
                }
                return@launch
            }

            // Initialize user progress
            progressRepository.initializeProgress(_user.value!!.uid)

            // Sync from cloud to get latest progress
            val syncFromResult = progressRepository.syncFromCloud(_user.value!!.uid)
            Napier.d("  result: $syncFromResult", tag = TAG)

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
                            selectedLesson = chapters.flatMap{
                                it.lessons
                            }.find {
                                it.isCurrent
                            },
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun selectLesson(lesson: LessonWithProgress?) {
        _uiState.update { it.copy(selectedLesson = lesson) }
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

    fun showGrievienceForm(show: Boolean) {
        _uiState.update { it.copy(showGrievienceForm = show) }
        if (!show) {
            _uiState.update { it.copy(grievienceState = GrievienceState()) }
        }
    }

    fun updateGrievienceTitle(title: String) {
        _uiState.update { it.copy(grievienceState = it.grievienceState.copy(title = title)) }
    }

    fun updateGrievienceDescription(description: String) {
        _uiState.update { it.copy(grievienceState = it.grievienceState.copy(description = description)) }
    }

    fun addGrievienceMedia(media: ImageBitmap) {
        _uiState.update {
            it.copy(grievienceState = it.grievienceState.copy(
                attachedMedia = it.grievienceState.attachedMedia + media
            ))
        }
    }

    fun removeGrievienceMedia(index: Int) {
        _uiState.update {
            val newList = it.grievienceState.attachedMedia.toMutableList()
            if (index in newList.indices) {
                newList.removeAt(index)
            }
            it.copy(grievienceState = it.grievienceState.copy(attachedMedia = newList))
        }
    }

    fun submitGrievience() {
        viewModelScope.launch {
            val state = _uiState.value.grievienceState
            val mediaBytes = state.attachedMedia.map { it.toByteArray() }
            
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                progressRepository.postGrievience(
                    title = state.title,
                    description = state.description,
                    name = _user.value?.name ?: "",
                    attachedMedia = mediaBytes
                )
                _events.emit(LearningEvent.Message("Thanks a lot ${_user.value?.name?.takeWhile { it != ' ' }} for your help . Lots of love ^^"))
                showGrievienceForm(false)
            } catch (e: Exception) {
                Napier.e("Failed to submit grievance", e, TAG)
                _events.emit(LearningEvent.Message("Failed to submit grievance. Please try again later."))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
