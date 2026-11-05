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
     * Connects to Google Generative Language API using supported endpoints.
     * Default model is gemini-3.8-flash (with automatic fallback to gemini-2.5-flash / gemini-3.5-flash-lite).
     * Handles system instructions safely and sanitizes multi-turn alternating user/model conversation history.
     */
    fun generateContentStream(
        modelName: String = "gemini-3.8-flash",
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
                "To connect live to the Gemini API (`$modelName`):\n\n" +
                "1. Open the **Secrets panel** in the AI Studio sidebar.\n" +
                "2. The `GEMINI_API_KEY` secret is injected at runtime.\n" +
                "3. Re-send your message to generate real-time answers!"
            )
            return@flow
        }

        // Map any legacy or retired names to currently active, non-deprecated models
        val normalizedModel = when (modelName.lowercase()) {
            "gemini-2.0-flash", "gemini-2.0-flash-exp", "gemini-2.0-flash-latest" -> "gemini-3.8-flash"
            "gemini-1.5-flash", "gemini-1.5-flash-latest", "gemini-1.5-pro", "gemini-pro" -> "gemini-3.8-flash"
            else -> modelName
        }

        val candidates = buildCandidateList(normalizedModel)

        var lastErrorMsg = "Unknown error"
        var responseSuccess = false
        var fullText = ""

        for (candidate in candidates) {
            try {
                Log.d(TAG, "Attempting generation with apiVersion=${candidate.apiVersion}, model=${candidate.modelId}")
                val requestBodyString = buildRequestBody(
                    modelId = candidate.modelId,
                    systemInstruction = systemInstruction,
                    historyMessages = historyMessages,
                    newPrompt = newPrompt
                )

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = requestBodyString.toRequestBody(mediaType)
                val url = "https://generativelanguage.googleapis.com/${candidate.apiVersion}/models/${candidate.modelId}:generateContent?key=$apiKey"

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (response.isSuccessful) {
                    val parsed = parseResponseText(responseBody)
                    if (parsed.isNotBlank()) {
                        fullText = parsed
                        responseSuccess = true
                        Log.i(TAG, "Successfully generated response via ${candidate.modelId} (${candidate.apiVersion})")
                        break
                    }
                } else {
                    Log.w(TAG, "Candidate ${candidate.modelId} HTTP ${response.code}: $responseBody")
                    val extractedError = try {
                        val json = JSONObject(responseBody)
                        json.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                    } catch (_: Exception) {
                        "HTTP ${response.code}"
                    }
                    lastErrorMsg = extractedError

                    // If error is 404 (model not found) or contains deprecation notice, continue to next candidate
                    if (response.code == 404 || extractedError.contains("no longer available", ignoreCase = true) || extractedError.contains("deprecated", ignoreCase = true)) {
                        continue
                    } else if (response.code == 403 || response.code == 401) {
                        emit("⚠️ **Gemini API Key Error (${response.code})**: $extractedError\n\nPlease verify that your `GEMINI_API_KEY` in the AI Studio Secrets panel is valid and has Generative Language API enabled.")
                        return@flow
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Exception contacting candidate ${candidate.modelId}: ${e.message}")
                lastErrorMsg = e.localizedMessage ?: e.javaClass.simpleName
            }
        }

        if (!responseSuccess) {
            emit("⚠️ **Gemini API Error**: $lastErrorMsg\n\nPlease verify network connection or model availability.")
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

    private data class ApiCandidate(val apiVersion: String, val modelId: String)

    private fun buildCandidateList(primaryModel: String): List<ApiCandidate> {
        val list = mutableListOf<ApiCandidate>()
        // 1. Primary requested model on v1beta and v1
        list.add(ApiCandidate("v1beta", primaryModel))
        list.add(ApiCandidate("v1", primaryModel))

        // 2. Active, non-deprecated fallbacks only: gemini-3.8-flash, gemini-2.5-flash, gemini-3.5-flash-lite
        if (primaryModel != "gemini-3.8-flash") {
            list.add(ApiCandidate("v1beta", "gemini-3.8-flash"))
            list.add(ApiCandidate("v1", "gemini-3.8-flash"))
        }
        if (primaryModel != "gemini-2.5-flash") {
            list.add(ApiCandidate("v1beta", "gemini-2.5-flash"))
            list.add(ApiCandidate("v1", "gemini-2.5-flash"))
        }
        if (primaryModel != "gemini-3.5-flash-lite") {
            list.add(ApiCandidate("v1beta", "gemini-3.5-flash-lite"))
        }

        return list.distinct()
    }

    private fun buildRequestBody(
        modelId: String,
        systemInstruction: String,
        historyMessages: List<ChatMessage>,
        newPrompt: String
    ): String {
        val requestJson = JSONObject()

        // Native system_instruction for Gemini models
        if (systemInstruction.isNotBlank()) {
            val sysInstructionObj = JSONObject()
            val sysParts = JSONArray().apply {
                put(JSONObject().put("text", systemInstruction))
            }
            sysInstructionObj.put("parts", sysParts)
            requestJson.put("system_instruction", sysInstructionObj)
        }

        // Build clean alternating contents array
        val contentsArray = JSONArray()

        // Filter valid prior messages
        val validHistory = historyMessages.filter {
            it.text.isNotBlank() && !it.isStreaming && !it.isError
        }

        var lastRole: String? = null
        val turns = mutableListOf<Pair<String, String>>()

        for (msg in validHistory) {
            val role = if (msg.isUser) "user" else "model"
            if (role == lastRole && turns.isNotEmpty()) {
                // Merge consecutive turns of the same role
                val prev = turns.removeAt(turns.lastIndex)
                turns.add(Pair(role, "${prev.second}\n\n${msg.text}"))
            } else {
                turns.add(Pair(role, msg.text))
                lastRole = role
            }
        }

        // Ensure history starts with "user"
        while (turns.isNotEmpty() && turns.first().first != "user") {
            turns.removeAt(0)
        }

        // Append active user message
        if (turns.isNotEmpty() && turns.last().first == "user") {
            val prev = turns.removeAt(turns.lastIndex)
            turns.add(Pair("user", "${prev.second}\n\n$newPrompt"))
        } else {
            turns.add(Pair("user", newPrompt))
        }

        for (turn in turns) {
            val turnObj = JSONObject()
            turnObj.put("role", turn.first)
            val parts = JSONArray().apply {
                put(JSONObject().put("text", turn.second))
            }
            turnObj.put("parts", parts)
            contentsArray.put(turnObj)
        }

        requestJson.put("contents", contentsArray)
        return requestJson.toString()
    }

    private fun parseResponseText(responseBody: String): String {
        return try {
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
            sb.toString().trim()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse response JSON", e)
            ""
        }
    }
}
