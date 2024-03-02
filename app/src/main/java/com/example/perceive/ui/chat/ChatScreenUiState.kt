package com.example.perceive.ui.chat

import com.example.perceive.domain.chat.ChatMessage

/**
 * Represents the state of the [ChatScreen].
 */
data class ChatScreenUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isListening: Boolean = false,
    val isLoading: Boolean = false,
    val hasErrorOccurred: Boolean = false
)