package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiApiService
import com.example.model.AttachmentChip
import com.example.model.ChatMessage
import com.example.model.ChatSession
import com.example.model.CubeModelOption
import com.example.model.FeedbackState
import com.example.model.QuickSuggestion
import com.example.model.SuggestionIconType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "CubeViewModel"

data class CubeUiState(
    val sessions: List<ChatSession> = emptyList(),
    val activeSessionId: String = "",
    val isSidebarExpanded: Boolean = true,
    val selectedModel: CubeModelOption = defaultModels.first(),
    val availableModels: List<CubeModelOption> = defaultModels,
    val customSystemInstruction: String = defaultModels.first().defaultSystemInstruction,
    val quickSuggestions: List<QuickSuggestion> = defaultSuggestions,
    val inputDraft: String = "",
    val isWebSearchEnabled: Boolean = false,
    val isVoiceRecording: Boolean = false,
    val stagedAttachments: List<AttachmentChip> = emptyList(),
    val isWaitingForCube: Boolean = false,
    val isStreamingResponse: Boolean = false,
    val isDarkTheme: Boolean = false,
    val searchHistoryQuery: String = "",
    val showCodeInspectorModal: Boolean = false,
    val showSettingsModal: Boolean = false,
    val sessionToRename: ChatSession? = null,
    val statusBannerMessage: String? = null
) {
    val activeSession: ChatSession?
        get() = sessions.find { it.id == activeSessionId }

    val activeMessages: List<ChatMessage>
        get() = activeSession?.messages ?: emptyList()

    val isWelcomeViewVisible: Boolean
        get() = activeMessages.isEmpty()

    companion object {
        val defaultModels = listOf(
            CubeModelOption(
                id = "cube-3-8-flash",
                name = "Cube 3.8 Flash",
                geminiModelId = "gemini-3.8-flash",
                badge = "CURRENT DEFAULT",
                roleTitle = "Frontier Intelligence Workhorse",
                description = "Google's frontier-class model for autonomous multi-turn reasoning, coding, and fast response generation.",
                latencyLabel = "gemini-3.8-flash",
                defaultSystemInstruction = "You are Cube, an intelligent, thoughtful, articulate, and warm assistant inspired by Claude. You communicate with clarity, precision, and intellectual honesty. When asked to code or reason through problems, provide clean, idiomatic solutions formatted in readable markdown with syntax-highlighted code blocks. Maintain conversation context across all previous turns."
            ),
            CubeModelOption(
                id = "cube-2-5-flash",
                name = "Cube 2.5 Flash",
                geminiModelId = "gemini-2.5-flash",
                badge = "VERSATILE",
                roleTitle = "Advanced Reasoning & Speed",
                description = "Supported Gemini model engineered for complex problem solving, low latency, and comprehensive code generation.",
                latencyLabel = "gemini-2.5-flash",
                defaultSystemInstruction = "You are Cube 2.5 Flash, an elite reasoning assistant and senior software architect. Provide comprehensive, deeply thought-out explanations and production-grade architectures with meticulous attention to detail."
            ),
            CubeModelOption(
                id = "cube-3-5-flash-lite",
                name = "Cube 3.5 Flash-Lite",
                geminiModelId = "gemini-3.5-flash-lite",
                badge = "ULTRA FAST",
                roleTitle = "Next-Gen Velocity Engine",
                description = "High-velocity, ultra-low latency model designed for rapid conversational agility and immediate answers.",
                latencyLabel = "gemini-3.5-flash-lite",
                defaultSystemInstruction = "You are Cube 3.5 Flash-Lite, an ultra-fast and responsive conversational assistant. Deliver crisp, actionable answers without unnecessary preamble."
            )
        )

        val defaultSuggestions = listOf(
            QuickSuggestion(
                id = "sug-code",
                title = "Write code",
                subtitle = "Clean TypeScript utility for deep object diffing with immutable patches",
                prompt = "Write code for a clean, type-safe TypeScript utility that computes deep diffs between two JSON objects and returns immutable patch operations.",
                badge = "CODE",
                iconType = SuggestionIconType.CODE
            ),
            QuickSuggestion(
                id = "sug-explain",
                title = "Explain concept",
                subtitle = "Compare vector embeddings with traditional relational indexes",
                prompt = "Explain the intuitive difference between vector embeddings for semantic search and traditional B-tree relational database indexes.",
                badge = "CONCEPT",
                iconType = SuggestionIconType.DATA
            ),
            QuickSuggestion(
                id = "sug-brainstorm",
                title = "Brainstorm ideas",
                subtitle = "Ergonomic principles for building calm, minimalist developer tools",
                prompt = "Brainstorm ideas for 5 UX principles to design calm, distraction-free developer software inspired by Scandinavian and Japanese minimalism.",
                badge = "IDEATION",
                iconType = SuggestionIconType.BRAINSTORM
            ),
            QuickSuggestion(
                id = "sug-architecture",
                title = "System design",
                subtitle = "Resilient event-driven architecture using Kafka & Outbox pattern",
                prompt = "Outline an architectural blueprint for a financial ledger service using event sourcing, transactional outbox pattern, and Apache Kafka.",
                badge = "DESIGN",
                iconType = SuggestionIconType.ARCHITECTURE
            )
        )
    }
}

class CubeViewModel(
    private val geminiApiService: GeminiApiService = GeminiApiService()
) : ViewModel() {

    private var streamingJob: Job? = null
    private val initialNewSessionId = "session-welcome-new"

    private val _uiState = MutableStateFlow(
        CubeUiState(
            sessions = createSeedSessions(initialNewSessionId),
            activeSessionId = initialNewSessionId
        )
    )
    val uiState: StateFlow<CubeUiState> = _uiState.asStateFlow()

    private fun createSeedSessions(welcomeId: String): List<ChatSession> {
        return listOf(
            ChatSession(
                id = welcomeId,
                title = "New conversation",
                updatedAt = "Just now",
                categoryTag = "ACTIVE",
                messages = emptyList(),
                isPinned = true
            ),
            ChatSession(
                id = "session-claude-design",
                title = "Warm Minimalism in UI",
                updatedAt = "20m ago",
                categoryTag = "DESIGN",
                messages = listOf(
                    ChatMessage(
                        id = "msg-seed-1",
                        isUser = true,
                        text = "Why do interfaces inspired by Claude choose warm cream backgrounds (#FBF9F5) and terracotta accents over high-contrast dark neon?",
                        timestamp = "10:14 AM",
                        modelUsed = "Cube 3.8 Flash"
                    ),
                    ChatMessage(
                        id = "msg-seed-2",
                        isUser = false,
                        text = """
                            Warm minimalism prioritizes **cognitive comfort** and **editorial readability**:

                            1. **Paper-Like Tone (#FBF9F5)**: High-contrast pure white (#FFFFFF) causes optical fatigue over long reading sessions. A warm cream background mimics natural book stock and ambient light.
                            2. **Terracotta & Amber (#CC785C / #D97706)**: Earthy pigments feel grounded and humanistic, contrasting with the cold, hyper-stimulating neon aesthetics of sci-fi cyberware.
                            3. **Serif Display Pairing (Lora + Inter)**: Using an editorial serif for headings gives the conversational partner a literary, thoughtful voice rather than a machine terminal.
                            4. **Uncluttered White Space**: Generous padding and subtle hairline borders guide focus to the text itself rather than surrounding chrome.
                        """.trimIndent(),
                        timestamp = "10:14 AM",
                        modelUsed = "Cube 3.8 Flash",
                        thinkingDurationMs = 380
                    )
                )
            )
        )
    }

    fun toggleSidebar() {
        _uiState.update { it.copy(isSidebarExpanded = !it.isSidebarExpanded) }
    }

    fun setSidebarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isSidebarExpanded = expanded) }
    }

    fun toggleThemeMode() {
        _uiState.update { state ->
            val next = !state.isDarkTheme
            state.copy(
                isDarkTheme = next,
                statusBannerMessage = if (next) "Theme: Warm Dark (#18181B)" else "Theme: Warm Light (#FBF9F5)"
            )
        }
    }

    fun selectModel(model: CubeModelOption) {
        _uiState.update {
            it.copy(
                selectedModel = model,
                customSystemInstruction = model.defaultSystemInstruction,
                statusBannerMessage = "Active model: ${model.name} (${model.geminiModelId})"
            )
        }
    }

    fun updateCustomSystemInstruction(instruction: String) {
        _uiState.update { it.copy(customSystemInstruction = instruction) }
    }

    fun updateInputDraft(text: String) {
        _uiState.update { it.copy(inputDraft = text) }
    }

    fun toggleWebSearch() {
        _uiState.update { state ->
            val next = !state.isWebSearchEnabled
            state.copy(
                isWebSearchEnabled = next,
                statusBannerMessage = if (next) "Web Grounding enabled" else "Web Grounding disabled"
            )
        }
    }

    fun toggleVoiceSimulation() {
        val currentlyRecording = _uiState.value.isVoiceRecording
        if (currentlyRecording) {
            _uiState.update { it.copy(isVoiceRecording = false) }
            return
        }
        _uiState.update {
            it.copy(
                isVoiceRecording = true,
                statusBannerMessage = "Listening to voice input…"
            )
        }
        viewModelScope.launch {
            delay(1400)
            _uiState.update { state ->
                if (!state.isVoiceRecording) return@update state
                val voicePrompt = "Explain how transformer self-attention mechanisms compute token weights."
                state.copy(
                    isVoiceRecording = false,
                    inputDraft = if (state.inputDraft.isBlank()) voicePrompt else "${state.inputDraft} $voicePrompt",
                    statusBannerMessage = "Voice input captured"
                )
            }
        }
    }

    fun addMockAttachment() {
        val samplePool = listOf(
            AttachmentChip(UUID.randomUUID().toString(), "claude_design_tokens.json", "24 KB", "JSON"),
            AttachmentChip(UUID.randomUUID().toString(), "gemini_system_prompt.txt", "8 KB", "TEXT"),
            AttachmentChip(UUID.randomUUID().toString(), "architecture_spec.md", "42 KB", "DOC"),
            AttachmentChip(UUID.randomUUID().toString(), "dataset_sample.csv", "180 KB", "CSV")
        )
        _uiState.update { state ->
            val currentNames = state.stagedAttachments.map { it.fileName }.toSet()
            val nextChip = samplePool.firstOrNull { it.fileName !in currentNames } ?: samplePool.random()
            state.copy(
                stagedAttachments = state.stagedAttachments + nextChip,
                statusBannerMessage = "Attached ${nextChip.fileName}"
            )
        }
    }

    fun removeAttachment(id: String) {
        _uiState.update { state ->
            state.copy(stagedAttachments = state.stagedAttachments.filterNot { it.id == id })
        }
    }

    fun startNewChat() {
        streamingJob?.cancel()
        val existingEmpty = _uiState.value.sessions.firstOrNull { it.messages.isEmpty() }
        if (existingEmpty != null) {
            _uiState.update {
                it.copy(
                    activeSessionId = existingEmpty.id,
                    inputDraft = "",
                    stagedAttachments = emptyList(),
                    isWaitingForCube = false,
                    isStreamingResponse = false
                )
            }
            return
        }

        val newId = "session-${UUID.randomUUID().toString().take(8)}"
        val newSession = ChatSession(
            id = newId,
            title = "New conversation",
            updatedAt = "Just now",
            categoryTag = "ACTIVE",
            messages = emptyList(),
            isPinned = true
        )
        _uiState.update { state ->
            state.copy(
                sessions = listOf(newSession) + state.sessions,
                activeSessionId = newId,
                inputDraft = "",
                stagedAttachments = emptyList(),
                isWaitingForCube = false,
                isStreamingResponse = false
            )
        }
    }

    fun clearCurrentChat() {
        streamingJob?.cancel()
        val activeId = _uiState.value.activeSessionId
        _uiState.update { state ->
            val updatedSessions = state.sessions.map { session ->
                if (session.id == activeId) {
                    session.copy(
                        title = "New conversation",
                        updatedAt = "Just now",
                        messages = emptyList()
                    )
                } else session
            }
            state.copy(
                sessions = updatedSessions,
                isWaitingForCube = false,
                isStreamingResponse = false,
                inputDraft = "",
                stagedAttachments = emptyList(),
                statusBannerMessage = "Conversation cleared"
            )
        }
    }

    fun selectSession(sessionId: String) {
        streamingJob?.cancel()
        _uiState.update {
            it.copy(
                activeSessionId = sessionId,
                isWaitingForCube = false,
                isStreamingResponse = false
            )
        }
    }

    fun deleteSession(sessionId: String) {
        streamingJob?.cancel()
        _uiState.update { state ->
            val remaining = state.sessions.filterNot { it.id == sessionId }.toMutableList()
            if (remaining.isEmpty()) {
                val fallback = ChatSession(
                    id = "session-${UUID.randomUUID().toString().take(8)}",
                    title = "New conversation",
                    updatedAt = "Just now",
                    categoryTag = "ACTIVE",
                    messages = emptyList()
                )
                remaining.add(fallback)
            }
            val nextActiveId = if (state.activeSessionId == sessionId) {
                remaining.first().id
            } else {
                state.activeSessionId
            }
            state.copy(
                sessions = remaining,
                activeSessionId = nextActiveId,
                statusBannerMessage = "Session removed"
            )
        }
    }

    fun openRenameDialog(session: ChatSession) {
        _uiState.update { it.copy(sessionToRename = session) }
    }

    fun dismissRenameDialog() {
        _uiState.update { it.copy(sessionToRename = null) }
    }

    fun renameSession(sessionId: String, newTitle: String) {
        val trimmed = newTitle.trim()
        if (trimmed.isEmpty()) {
            dismissRenameDialog()
            return
        }
        _uiState.update { state ->
            val updated = state.sessions.map {
                if (it.id == sessionId) it.copy(title = trimmed) else it
            }
            state.copy(
                sessions = updated,
                sessionToRename = null,
                statusBannerMessage = "Renamed to \"$trimmed\""
            )
        }
    }

    fun updateSearchHistoryQuery(query: String) {
        _uiState.update { it.copy(searchHistoryQuery = query) }
    }

    fun setShowCodeInspectorModal(show: Boolean) {
        _uiState.update { it.copy(showCodeInspectorModal = show) }
    }

    fun setShowSettingsModal(show: Boolean) {
        _uiState.update { it.copy(showSettingsModal = show) }
    }

    fun clearStatusBanner() {
        _uiState.update { it.copy(statusBannerMessage = null) }
    }

    /**
     * Sends a message to Google AI Studio's Gemini API (default: gemini-3.8-flash).
     * Maintains full multi-turn conversation history and streams token chunks.
     */
    fun sendMessage(overridePrompt: String? = null) {
        val currentState = _uiState.value
        val rawPrompt = (overridePrompt ?: currentState.inputDraft).trim()
        if (rawPrompt.isEmpty() && currentState.stagedAttachments.isEmpty()) return

        streamingJob?.cancel()

        val timeStamp = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        val userMsg = ChatMessage(
            id = "msg-user-${UUID.randomUUID().toString().take(8)}",
            isUser = true,
            text = if (rawPrompt.isEmpty()) "Analyze attached files: ${currentState.stagedAttachments.joinToString { it.fileName }}" else rawPrompt,
            timestamp = timeStamp,
            modelUsed = currentState.selectedModel.name,
            webSearchUsed = currentState.isWebSearchEnabled,
            attachments = currentState.stagedAttachments
        )

        val activeId = currentState.activeSessionId
        val derivedTitle = deriveTitleFromPrompt(userMsg.text)

        // Capture previous conversation history BEFORE adding the new message
        val priorMessages = currentState.activeSession?.messages ?: emptyList()

        _uiState.update { state ->
            val updatedSessions = state.sessions.map { session ->
                if (session.id == activeId) {
                    val shouldUpdateTitle = session.messages.isEmpty() || session.title == "New conversation"
                    session.copy(
                        title = if (shouldUpdateTitle) derivedTitle else session.title,
                        updatedAt = "Just now",
                        categoryTag = inferCategoryTag(userMsg.text),
                        messages = session.messages + userMsg
                    )
                } else session
            }
            state.copy(
                sessions = updatedSessions,
                inputDraft = "",
                stagedAttachments = emptyList(),
                isWaitingForCube = true,
                isStreamingResponse = false
            )
        }

        val responseId = "msg-cube-${UUID.randomUUID().toString().take(8)}"
        val startTime = System.currentTimeMillis()

        streamingJob = viewModelScope.launch {
            try {
                // Initialize assistant placeholder message
                val initialBotMsg = ChatMessage(
                    id = responseId,
                    isUser = false,
                    text = "",
                    timestamp = SimpleDateFormat("hh:mm a", Locale.US).format(Date()),
                    modelUsed = currentState.selectedModel.name,
                    isStreaming = true,
                    webSearchUsed = currentState.isWebSearchEnabled,
                    thinkingDurationMs = 0
                )

                _uiState.update { state ->
                    val updatedSessions = state.sessions.map { session ->
                        if (session.id == activeId) {
                            session.copy(messages = session.messages + initialBotMsg)
                        } else session
                    }
                    state.copy(
                        sessions = updatedSessions,
                        isWaitingForCube = false,
                        isStreamingResponse = true
                    )
                }

                // Call Google AI Studio Gemini API with 60s timeout
                var lastStreamedText = ""
                withTimeout(60_000L) {
                    val streamFlow = geminiApiService.generateContentStream(
                        modelName = currentState.selectedModel.geminiModelId,
                        systemInstruction = currentState.customSystemInstruction,
                        historyMessages = priorMessages,
                        newPrompt = userMsg.text
                    )

                    streamFlow.collect { currentChunk ->
                        lastStreamedText = currentChunk
                        _uiState.update { state ->
                            val updatedSessions = state.sessions.map { session ->
                                if (session.id == activeId) {
                                    session.copy(
                                        messages = session.messages.map { msg ->
                                            if (msg.id == responseId) {
                                                msg.copy(
                                                    text = currentChunk,
                                                    isStreaming = true,
                                                    thinkingDurationMs = (System.currentTimeMillis() - startTime).toInt()
                                                )
                                            } else msg
                                        }
                                    )
                                } else session
                            }
                            state.copy(sessions = updatedSessions)
                        }
                    }
                }

                val finalOutput = lastStreamedText.ifBlank {
                    "No textual response generated by the model."
                }
                val totalDuration = (System.currentTimeMillis() - startTime).toInt()

                // Mark streaming complete
                _uiState.update { state ->
                    val updatedSessions = state.sessions.map { session ->
                        if (session.id == activeId) {
                            session.copy(
                                messages = session.messages.map { msg ->
                                    if (msg.id == responseId) {
                                        msg.copy(
                                            text = finalOutput,
                                            isStreaming = false,
                                            thinkingDurationMs = totalDuration
                                        )
                                    } else msg
                                }
                            )
                        } else session
                    }
                    state.copy(
                        sessions = updatedSessions,
                        isStreamingResponse = false,
                        statusBannerMessage = "Answered via ${currentState.selectedModel.name}"
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini inference timed out after 60s", e)
                handleInferenceError(
                    activeId = activeId,
                    responseId = responseId,
                    errorMessage = "Generation timed out after 60 seconds. Please check your network connectivity."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini generation failed", e)
                val displayMsg = e.localizedMessage?.takeIf { it.isNotBlank() }
                    ?: "Gemini request failed (${e.javaClass.simpleName}). Please verify your connection."
                handleInferenceError(
                    activeId = activeId,
                    responseId = responseId,
                    errorMessage = displayMsg
                )
            }
        }
    }

    private fun handleInferenceError(activeId: String, responseId: String, errorMessage: String) {
        _uiState.update { state ->
            val updatedSessions = state.sessions.map { session ->
                if (session.id == activeId) {
                    session.copy(
                        messages = session.messages.map { msg ->
                            if (msg.id == responseId) {
                                msg.copy(
                                    text = "⚠️ $errorMessage",
                                    isStreaming = false,
                                    isError = true
                                )
                            } else msg
                        }
                    )
                } else session
            }
            state.copy(
                sessions = updatedSessions,
                isWaitingForCube = false,
                isStreamingResponse = false,
                statusBannerMessage = "Error: $errorMessage"
            )
        }
    }

    fun regenerateResponse(assistantMessageId: String) {
        val state = _uiState.value
        val session = state.activeSession ?: return
        val msgIndex = session.messages.indexOfFirst { it.id == assistantMessageId }
        if (msgIndex == -1) return

        val precedingUserPrompt = session.messages
            .take(msgIndex)
            .lastOrNull { it.isUser }
            ?.text ?: return

        val priorHistory = session.messages.take(msgIndex).dropLast(1)

        streamingJob?.cancel()
        val startTime = System.currentTimeMillis()

        streamingJob = viewModelScope.launch {
            _uiState.update { current ->
                val updatedSessions = current.sessions.map { s ->
                    if (s.id == session.id) {
                        s.copy(
                            messages = s.messages.map { m ->
                                if (m.id == assistantMessageId) {
                                    m.copy(
                                        text = "",
                                        isStreaming = true,
                                        isError = false,
                                        modelUsed = current.selectedModel.name,
                                        feedback = FeedbackState.NONE
                                    )
                                } else m
                            }
                        )
                    } else s
                }
                current.copy(
                    sessions = updatedSessions,
                    isStreamingResponse = true,
                    statusBannerMessage = "Regenerating with ${current.selectedModel.name}…"
                )
            }

            try {
                var lastStreamedText = ""
                withTimeout(60_000L) {
                    val streamFlow = geminiApiService.generateContentStream(
                        modelName = state.selectedModel.geminiModelId,
                        systemInstruction = state.customSystemInstruction,
                        historyMessages = priorHistory,
                        newPrompt = precedingUserPrompt
                    )

                    streamFlow.collect { currentChunk ->
                        lastStreamedText = currentChunk
                        _uiState.update { current ->
                            val updatedSessions = current.sessions.map { s ->
                                if (s.id == session.id) {
                                    s.copy(
                                        messages = s.messages.map { m ->
                                            if (m.id == assistantMessageId) {
                                                m.copy(
                                                    text = currentChunk,
                                                    isStreaming = true,
                                                    thinkingDurationMs = (System.currentTimeMillis() - startTime).toInt()
                                                )
                                            } else m
                                        }
                                    )
                                } else s
                            }
                            current.copy(sessions = updatedSessions)
                        }
                    }
                }

                val finalOutput = lastStreamedText.ifBlank {
                    "No content generated by model."
                }
                val totalDuration = (System.currentTimeMillis() - startTime).toInt()

                _uiState.update { current ->
                    val updatedSessions = current.sessions.map { s ->
                        if (s.id == session.id) {
                            s.copy(
                                messages = s.messages.map { m ->
                                    if (m.id == assistantMessageId) {
                                        m.copy(
                                            text = finalOutput,
                                            isStreaming = false,
                                            thinkingDurationMs = totalDuration
                                        )
                                    } else m
                                }
                            )
                        } else s
                    }
                    current.copy(sessions = updatedSessions, isStreamingResponse = false)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Regeneration timed out", e)
                handleInferenceError(
                    activeId = session.id,
                    responseId = assistantMessageId,
                    errorMessage = "Regeneration timed out after 60 seconds."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Regeneration failed", e)
                handleInferenceError(
                    activeId = session.id,
                    responseId = assistantMessageId,
                    errorMessage = e.localizedMessage ?: "Regeneration failed."
                )
            }
        }
    }

    fun toggleMessageFeedback(messageId: String, target: FeedbackState) {
        _uiState.update { state ->
            val updatedSessions = state.sessions.map { session ->
                if (session.id == state.activeSessionId) {
                    session.copy(
                        messages = session.messages.map { msg ->
                            if (msg.id == messageId) {
                                val nextFeedback = if (msg.feedback == target) FeedbackState.NONE else target
                                msg.copy(feedback = nextFeedback)
                            } else msg
                        }
                    )
                } else session
            }
            val banner = when (target) {
                FeedbackState.LIKED -> "Feedback recorded: Helpful"
                FeedbackState.DISLIKED -> "Feedback recorded: Needs improvement"
                FeedbackState.NONE -> null
            }
            state.copy(sessions = updatedSessions, statusBannerMessage = banner)
        }
    }

    fun notifyCopiedToClipboard() {
        _uiState.update { it.copy(statusBannerMessage = "Copied to clipboard") }
    }

    private fun deriveTitleFromPrompt(prompt: String): String {
        val cleaned = prompt.replace("\n", " ").trim()
        return if (cleaned.length <= 30) cleaned else cleaned.take(28).trimEnd() + "…"
    }

    private fun inferCategoryTag(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("code") || lower.contains("typescript") || lower.contains("python") || lower.contains("function") -> "CODE"
            lower.contains("data") || lower.contains("sql") || lower.contains("metric") || lower.contains("embed") -> "DATA"
            lower.contains("idea") || lower.contains("brainstorm") || lower.contains("minimal") -> "IDEAS"
            else -> "CHAT"
        }
    }
}
