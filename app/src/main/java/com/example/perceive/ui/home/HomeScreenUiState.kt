package com.example.perceive.ui.home

/**
 * A data class that represents the current UI state of the [HomeScreen].
 *
 * @property isListening Indicates whether the system is currently listening to the user.
 * @property isLoadingAssistantResponse Indicates whether a response from the assistant is being loaded.
 * @property assistantResponse The latest response received from the assistant, or null if no response is available.
 * @property hasErrorOccurred Indicates whether an error has occurred.
 */
data class HomeScreenUiState(
    val isListening: Boolean = false,
    val isLoadingAssistantResponse: Boolean = false,
    val assistantResponse: String? = null,
    val hasErrorOccurred: Boolean = false,
)