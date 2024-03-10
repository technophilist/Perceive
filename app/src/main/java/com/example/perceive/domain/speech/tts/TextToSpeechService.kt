package com.example.perceive.domain.speech.tts

import java.lang.Exception

/**
 * A service that provides text-to-speech functionality.
 */
interface TextToSpeechService {

    /**
     * Starts speaking the given text.
     *
     * @param text The text to speak.
     * @param onFailure A callback that is invoked if an error occurs while attempting to speak.
     * @param onSuccess A callback that is invoked if the text is successfully spoken.
     */
    fun startSpeaking(text: String, onFailure: (Exception) -> Unit, onSuccess: (() -> Unit)? = null)

    /**
     * Stops speaking and clears resources.
     */
    fun stopSpeakingAndClearResources()
}