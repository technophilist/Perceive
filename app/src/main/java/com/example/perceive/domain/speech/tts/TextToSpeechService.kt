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
    suspend fun startSpeaking(
        text: String,
        onFailure: (Exception) -> Unit,
        onSuccess: (() -> Unit)? = null
    )

    /**
     * Stops speaking if the service is currently speaking.
     */
    fun stop()

    /**
     * Stops speaking and clears resources.
     */
    @Deprecated("Use stop() and releaseResources() method")
    fun stopSpeakingAndClearResources()

    /**
     * Used to release all resources held by a particular instance. The instance
     * cannot be reused. In other words, a new instance needs to be created
     * for the [startSpeaking] method to work.
     */
    fun releaseResources()
}