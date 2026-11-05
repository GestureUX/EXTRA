package com.example.data

import android.util.Log
import com.example.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.flow.Flow

private const val TAG = "GeminiChatRepo"

class GeminiChatRepository {

    /**
     * Streams a response from Gemini using Firebase AI Logic while maintaining
     * multi-turn conversation history and applying role-specific system instructions.
     */
    fun streamMultiTurnChat(
        geminiModelId: String,
        systemInstruction: String,
        historyMessages: List<ChatMessage>,
        newPrompt: String
    ): Flow<GenerateContentResponse> {
        Log.d(TAG, "Initializing model=$geminiModelId with history count=${historyMessages.size}")

        val systemContent = if (systemInstruction.isNotBlank()) {
            Content(parts = listOf(TextPart(systemInstruction)))
        } else null

        val model = Firebase.ai.generativeModel(
            modelName = geminiModelId,
            systemInstruction = systemContent
        )

        // Convert prior messages to Firebase Content objects
        // Filtering out in-flight streaming, empty, or failed messages
        val historyContent: List<Content> = historyMessages.mapNotNull { msg ->
            if (msg.text.isBlank() || msg.isStreaming || msg.isError) return@mapNotNull null
            val role = if (msg.isUser) "user" else "model"
            Content(
                role = role,
                parts = listOf(TextPart(msg.text))
            )
        }

        val chatSession = model.startChat(history = historyContent)
        return chatSession.sendMessageStream(newPrompt)
    }
}
