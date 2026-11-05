package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TAG = "GeminiApiService"

class GeminiApiService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    /**
     * Connects to Google AI Studio's free Gemini API using gemini-1.5-flash.
     * Reads API key safely from BuildConfig.GEMINI_API_KEY (.env).
     * Maintains multi-turn conversation history and system instructions.
     */
    fun generateContentStream(
        modelName: String = "gemini-1.5-flash",
        systemInstruction: String,
        historyMessages: List<ChatMessage>,
        newPrompt: String
    ): Flow<String> = flow {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (_: Throwable) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY" || apiKey == "MY_GEMINI_API_KEY") {
            emit(
                "⚠️ **Google AI Studio API Key Not Configured**\n\n" +
                "To connect live to Google AI Studio's free Gemini API (`$modelName`):\n\n" +
                "1. Open the **Secrets panel** in the AI Studio sidebar.\n" +
                "2. Add your free key as **`GEMINI_API_KEY`** (get one free at [aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)).\n" +
                "3. Re-send your message to generate real-time answers!"
            )
            return@flow
        }

        val requestJson = JSONObject()

        // 1. System Instruction (Claude/Assistant role definition)
        if (systemInstruction.isNotBlank()) {
            val sysInstructionObj = JSONObject()
            val sysParts = JSONArray().apply {
                put(JSONObject().put("text", systemInstruction))
            }
            sysInstructionObj.put("parts", sysParts)
            requestJson.put("system_instruction", sysInstructionObj)
        }

        // 2. Multi-turn conversation contents
        val contentsArray = JSONArray()

        historyMessages.forEach { msg ->
            if (msg.text.isNotBlank() && !msg.isStreaming && !msg.isError) {
                val turnRole = if (msg.isUser) "user" else "model"
                val turnObj = JSONObject()
                turnObj.put("role", turnRole)
                val parts = JSONArray().apply {
                    put(JSONObject().put("text", msg.text))
                }
                turnObj.put("parts", parts)
                contentsArray.put(turnObj)
            }
        }

        // Append active user message
        val currentTurn = JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().put("text", newPrompt))
            })
        }
        contentsArray.put(currentTurn)
        requestJson.put("contents", contentsArray)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            Log.e(TAG, "Gemini API HTTP ${response.code}: $responseBody")
            val errorMsg = try {
                val json = JSONObject(responseBody)
                json.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}: ${response.message}"
            } catch (_: Exception) {
                "HTTP ${response.code}: ${response.message}"
            }
            emit("⚠️ **Gemini API Error (${response.code})**: $errorMsg")
            return@flow
        }

        val fullText = try {
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val sb = StringBuilder()
            if (parts != null) {
                for (idx in 0 until parts.length()) {
                    val p = parts.optJSONObject(idx)
                    sb.append(p?.optString("text").orEmpty())
                }
            }
            sb.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini response JSON", e)
            throw e
        }

        if (fullText.isBlank()) {
            emit("Model returned an empty response.")
            return@flow
        }

        // Stream smoothly chunk by chunk for fluid reading
        val words = fullText.split(" ")
        val chunkWords = 3
        var i = 0
        val accumulated = StringBuilder()
        while (i < words.size) {
            val end = (i + chunkWords).coerceAtMost(words.size)
            val chunk = words.subList(i, end).joinToString(" ")
            if (accumulated.isNotEmpty()) accumulated.append(" ")
            accumulated.append(chunk)
            emit(accumulated.toString())
            i = end
            delay(16)
        }
        emit(fullText)
    }.flowOn(Dispatchers.IO)
}
