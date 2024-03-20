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

    private var textToSpeech: TextToSpeech = TextToSpeech(context) {
        if (it == TextToSpeech.ERROR) throw Exception("An error occurred while initializing the text-to-speech engine")
    }

    override fun startSpeaking(
        text: String,
        onFailure: (Exception) -> Unit,
        onSuccess: (() -> Unit)?
    ) {
        if (text.length > TextToSpeech.getMaxSpeechInputLength()) {
            onFailure(IllegalArgumentException("The text length is larger than the max supported speech input length"))
            return
        }
        textToSpeech.language = Locale.US
        val speakResult = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        if (speakResult == TextToSpeech.ERROR) onFailure(Exception("An internal error occurred while attempting to speak"))
        else onSuccess?.invoke()
    }

    override fun stopSpeakingAndClearResources() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}