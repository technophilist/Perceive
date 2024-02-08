package com.example.perceive.data.remote.languagemodel


import android.graphics.Bitmap
import androidx.compose.runtime.internal.illegalDecoyCallException
import com.example.perceive.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CancellationException
import java.lang.IllegalStateException
import javax.inject.Inject

class GeminiLanguageModelClient @Inject constructor() : MultiModalLanguageModelClient {
    private val generativeModel = GenerativeModel(
        modelName = GEMINI_PRO_VISION,
        apiKey = BuildConfig.GOOGLE_GEMINI_API_KEY
    )

    private var currentChatSession: Chat? = null

    // At the time of writing this, Gemini-pro-vision requires at least 1
    // image in the conversation history. Else, an exception would be
    // thrown. However, conversations may not always start with an image.
    // Use the following variable to insert an "empty" bitmap in conversation
    // history in case the conversation history doesn't have any bitmaps in it yet.
    private val emptyBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    private var doesCurrentChatSessionHasOneImage = false


    override fun startNewChatSession() {
        currentChatSession = generativeModel.startChat()
    }

    override suspend fun sendMessage(messageContents: List<MultiModalLanguageModelClient.MessageContent>): Result<String> {
        val content = content(role = "user") {
            messageContents.forEach { messageContent ->
                when (messageContent) {
                    is MultiModalLanguageModelClient.MessageContent.Image -> image(messageContent.bitmap)
                    is MultiModalLanguageModelClient.MessageContent.Text -> text(messageContent.text)
                }
            }
            // At the time of writing this, Gemini-pro-vision requires at least 1
            // image in the conversation history. Else, an exception would be
            // thrown. However, conversations may not always start with an image.
            // Hence, to circumvent this limitation, add an empty bitmap and set
            // the flag to true.
            if (!doesCurrentChatSessionHasOneImage) {
                image(emptyBitmap)
                doesCurrentChatSessionHasOneImage = true
            }
        }

        return try {
            val currentChatSession =
                currentChatSession ?: throw IllegalStateException(CHAT_SESSION_INVALID_MESSAGE)

            Result.success(currentChatSession.sendMessage(content).text!!)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

    override fun endChatSession() {
        currentChatSession = null
        doesCurrentChatSessionHasOneImage = false
    }

    private companion object {
        const val GEMINI_PRO_VISION = "gemini-pro-vision"
        const val CHAT_SESSION_INVALID_MESSAGE =
            "A chat session is not in progress. Please use startNewChat() before attempting to send new messages."
    }
}