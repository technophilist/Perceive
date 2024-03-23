package com.example.perceive.domain.speech.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
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
    private val didTextToSpeechInitializeSuccessfully = CompletableDeferred<Boolean>()
    private var textToSpeech: TextToSpeech = TextToSpeech(context) {
        if (it == TextToSpeech.ERROR) didTextToSpeechInitializeSuccessfully.complete(false)
        else didTextToSpeechInitializeSuccessfully.complete(true)
    }

    override suspend fun startSpeaking(
        text: String,
        onFailure: (Exception) -> Unit,
        onSuccess: (() -> Unit)?
    ) {
        if (text.length > TextToSpeech.getMaxSpeechInputLength()) {
            onFailure(IllegalArgumentException("The text length is larger than the max supported speech input length"))
            return
        }
        // wait for tts engine to initialize
        val wasTtsInitializedSuccessfully = didTextToSpeechInitializeSuccessfully.await()
        if (!wasTtsInitializedSuccessfully) {
            onFailure(Exception("An internal error occurred when trying to initialize TTS engine."))
            return
        }
        textToSpeech.language = Locale.US
        val speakResult = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        if (speakResult == TextToSpeech.ERROR) onFailure(Exception("An internal error occurred while attempting to speak"))
        else onSuccess?.invoke()
    }

    override fun stop() {
        if (!textToSpeech.isSpeaking) return
        textToSpeech.stop()
    }

    override fun releaseResources() {
        textToSpeech.shutdown()
    }

    override fun stopSpeakingAndClearResources() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}