package com.example.perceive.domain.speech.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.Locale
import javax.inject.Inject

/**
 * A [TextToSpeechService] implementation that uses the Android [TextToSpeech] under the hood
 * to speak text.
 */
class AndroidTextToSpeechService @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeechService {

    private var textToSpeech: TextToSpeech? = null

    override fun startSpeaking(
        text: String,
        onFailure: (Exception) -> Unit,
        onSuccess: (() -> Unit)?
    ) {
        if (text.length > TextToSpeech.getMaxSpeechInputLength()) {
            onFailure(IllegalArgumentException("The text length is larger than the max supported speech input length"))
            return
        }
        textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                onFailure(Exception("Failed to initialize TextToSpeech"))
                return@TextToSpeech
            }
            textToSpeech?.language = Locale.US
            val speakResult = textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
            if (speakResult == TextToSpeech.ERROR) onFailure(Exception("An internal error occurred while attempting to speak"))
            else onSuccess?.invoke()
        }
    }

    override fun stopSpeakingAndClearResources() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}