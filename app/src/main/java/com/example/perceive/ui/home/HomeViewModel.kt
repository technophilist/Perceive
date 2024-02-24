package com.example.perceive.ui.home

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
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
    
    fun startTranscription(currentCameraImageProxy: ImageProxy, onEndOfSpeech: (String) -> Unit) {
        // todo: send currentCameraImageProxy to LLM client
        _userSpeechTranscriptionStream.update { "" }
        // use a completely new state to remove old stale states
        _uiState.update { HomeScreenUiState(isListening = true) }
        transcriptionService.startListening(
            transcription = { transcription -> _userSpeechTranscriptionStream.update { transcription } },
            onEndOfSpeech = {
                val transcription = _userSpeechTranscriptionStream.value ?: return@startListening
                _uiState.update { HomeScreenUiState() } // reset state to defaults
                onEndOfSpeech(transcription)
            },
            onError = { _uiState.update { it.copy(isListening = false, hasErrorOccurred = true) } }
        )
    }
}