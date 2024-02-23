package com.example.perceive.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceive.domain.speech.TranscriptionService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transcriptionService: TranscriptionService
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState as StateFlow<HomeScreenUiState>

    private val _userSpeechTranscriptionStream = MutableStateFlow<String?>(null)
    val userSpeechTranscriptionStream = _userSpeechTranscriptionStream as StateFlow<String?>

    private var currentCameraImage: Bitmap? = null

    // An image of what the user is currently pointing the camera at.
    fun startTranscription(currentCameraImage: Bitmap) {
        this.currentCameraImage = currentCameraImage
        _userSpeechTranscriptionStream.update { "" }
        // use a completely new state to remove old stale states
        _uiState.update { HomeScreenUiState(isListening = true) }
        transcriptionService.startListening(
            transcription = { transcription -> _userSpeechTranscriptionStream.update { transcription } },
            onEndOfSpeech = ::onEndOfSpeech,
            onError = { _uiState.update { it.copy(isListening = false, hasErrorOccurred = true) } }
        )
    }

    private fun onEndOfSpeech() {
        if (_userSpeechTranscriptionStream.value?.isBlank() == true || currentCameraImage == null) {
            _uiState.update { it.copy(isListening = false) }
            return
        }
        _uiState.update { it.copy(isListening = false) }
        viewModelScope.launch {
            // todo:  process request
            _uiState.update {
                delay(3.seconds)
                it.copy(isListening = false)
            }
        }
    }
}