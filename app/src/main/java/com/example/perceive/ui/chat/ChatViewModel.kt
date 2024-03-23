package com.example.perceive.ui.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceive.data.local.bitmapstore.BitmapStore
import com.example.perceive.data.remote.languagemodel.MultiModalLanguageModelClient
import com.example.perceive.domain.chat.ChatMessage
import com.example.perceive.domain.speech.transcription.TranscriptionService
import com.example.perceive.domain.speech.tts.TextToSpeechService
import com.example.perceive.ui.navigation.PerceiveNavigationDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val transcriptionService: TranscriptionService,
    private val textToSpeechService: TextToSpeechService,
    private val languageModelClient: MultiModalLanguageModelClient,
    private val bitmapStore: BitmapStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialUserChatMessage = ChatMessage(
        message = savedStateHandle.get<String>(PerceiveNavigationDestinations.ChatScreen.NAV_ARG_INITIAL_USER_PROMPT)!!,
        role = ChatMessage.Role.USER
    )
    private val conversationImageBitmapUri = savedStateHandle
        .get<String>(PerceiveNavigationDestinations.ChatScreen.NAV_ARG_ASSOCIATED_IMAGE_URI_BASE64)!!
        .run {
            Base64.getDecoder().decode(this)
                .let(::String)
                .let(Uri::parse)
        }

    private val _uiState = MutableStateFlow(ChatScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _userSpeechTranscriptionStream = MutableStateFlow<String?>(null)
    val userSpeechTranscriptionStream = _userSpeechTranscriptionStream.asStateFlow()

    init {
        languageModelClient.startNewChatSession()
        generateResponseForInitialPromptAndImage()
    }

    fun startTranscription() {
        _userSpeechTranscriptionStream.update { "" }
        _uiState.update { it.copy(isListening = true) }
        transcriptionService.startListening(
            transcription = { transcription -> _userSpeechTranscriptionStream.update { transcription } },
            onEndOfSpeech = {
                _uiState.update { it.copy(isListening = false) }
                processTranscriptionAndGenerateResponse()
            },
            onError = { _uiState.update { it.copy(isListening = false, hasErrorOccurred = true) } }
        )
    }

    private fun processTranscriptionAndGenerateResponse() {
        val userTranscription =
            _userSpeechTranscriptionStream.value ?: return
        if (userTranscription.isBlank()) return
        // before adding the transcription to the chat, clear the transcription stream
        _userSpeechTranscriptionStream.update { null }
        val userTranscriptionChatMessage = ChatMessage(
            message = userTranscription,
            role = ChatMessage.Role.USER
        )
        _uiState.update {
            it.copy(messages = it.messages + userTranscriptionChatMessage)
        }
        val messageToModel = listOf(
            MultiModalLanguageModelClient.MessageContent.Text(text = userTranscription)
        )
        generateLanguageModelResponseUpdatingUiState(
            messageToModel = messageToModel,
            isAssistantMuted = _uiState.value.isAssistantMuted
        )
    }

    fun onAssistantMutedStateChange(isMuted: Boolean) {
        _uiState.update { it.copy(isAssistantMuted = isMuted) }
        // todo: if assitant is speaking and the user mutes the assistant, it should immediately stop.
    }

    private fun generateResponseForInitialPromptAndImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(messages = listOf(initialUserChatMessage)) }
            val initialBitmap = bitmapStore
                .retrieveBitmapForUri(conversationImageBitmapUri)
                ?: return@launch // todo: error handling / delete image after using it
            val messageToModel = listOf(
                MultiModalLanguageModelClient.MessageContent.Image(initialBitmap),
                MultiModalLanguageModelClient.MessageContent.Text(text = initialUserChatMessage.message),
            )
            generateLanguageModelResponseUpdatingUiState(
                messageToModel = messageToModel,
                isAssistantMuted = _uiState.value.isAssistantMuted
            )
        }
    }

    private fun generateLanguageModelResponseUpdatingUiState(
        messageToModel: List<MultiModalLanguageModelClient.MessageContent>,
        isAssistantMuted: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = languageModelClient.sendMessage(messageToModel)
                    .getOrThrow()
                    .let { ChatMessage(message = it, role = ChatMessage.Role.ASSISTANT) }
                _uiState.update { it.copy(messages = it.messages + response) }
                if (!isAssistantMuted) {
                    textToSpeechService.startSpeaking(response.message, {})
                }
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _uiState.update { it.copy(hasErrorOccurred = true) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    override fun onCleared() {
        languageModelClient.endChatSession()
        super.onCleared()
    }
}