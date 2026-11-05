package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiChatRepository
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
    val oledPitchMode: Boolean = false,
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
                id = "cube-1-pro",
                name = "Cube 1.0 Pro",
                geminiModelId = "gemini-3.8-flash",
                badge = "GENERAL TASKS",
                roleTitle = "Master Neural Generalist",
                description = "Balanced flagship intelligence for general multi-turn reasoning, comprehensive technical queries, and code analysis.",
                latencyLabel = "gemini-3.8-flash",
                defaultSystemInstruction = "You are Cube, an ultra-sleek, futuristic AI assistant inspired by Grok. You provide direct, insightful, witty, and technically rigorous responses with crisp markdown formatting, clear code blocks, and zero unnecessary fluff. Maintain conversation context across all previous turns."
            ),
            CubeModelOption(
                id = "cube-fast",
                name = "Cube Fast",
                geminiModelId = "gemini-3.1-flash-lite",
                badge = "FAST RESPONSES",
                roleTitle = "Ultra-Fast Speedster",
                description = "Sub-second streaming optimized for tasks that should happen fast, rapid iteration, and immediate answers.",
                latencyLabel = "gemini-3.1-flash-lite",
                defaultSystemInstruction = "You are Cube Fast, a high-velocity AI assistant optimized for ultra-low latency. Keep answers succinct, high-impact, direct, and actionable without wordiness. Maintain full multi-turn conversational context."
            ),
            CubeModelOption(
                id = "cube-quantum",
                name = "Cube Quantum",
                geminiModelId = "gemini-4-argon",
                badge = "COMPLEX TASKS",
                roleTitle = "Deep Architecture & STEM Specialist",
                description = "Most powerful model engineered for particularly complex reasoning, large-scale software architecture, formal verification, and advanced math.",
                latencyLabel = "gemini-4-argon",
                defaultSystemInstruction = "You are Cube Quantum, an advanced AI system architect and elite senior software engineer specialized in complex problem-solving, advanced coding, formal logic, mathematics, system blueprints, and deep technical reasoning. Provide exhaustive, rigorous, production-grade solutions."
            )
        )

        val defaultSuggestions = listOf(
            QuickSuggestion(
                id = "sug-code",
                title = "Write code",
                subtitle = "Interactive 3D WebGL shader & particle system in clean TypeScript",
                prompt = "Write code for a sleek interactive 3D isometric particle cube using HTML5 Canvas and requestAnimationFrame with cyan neon lighting.",
                badge = "SYNTHESIS",
                iconType = SuggestionIconType.CODE
            ),
            QuickSuggestion(
                id = "sug-data",
                title = "Analyze data",
                subtitle = "Diagnose SaaS retention cohorts & generate SQL anomaly pipelines",
                prompt = "Analyze data for a high-growth AI SaaS product: break down cohort retention bottlenecks and provide an optimized PostgreSQL window-function query.",
                badge = "TELEMETRY",
                iconType = SuggestionIconType.DATA
            ),
            QuickSuggestion(
                id = "sug-brainstorm",
                title = "Brainstorm ideas",
                subtitle = "Next-gen developer workflows powered by sub-10ms edge inference",
                prompt = "Brainstorm ideas for 4 futuristic developer productivity tools that combine local neural models with real-time IDE telemetry.",
                badge = "IDEATION",
                iconType = SuggestionIconType.BRAINSTORM
            ),
            QuickSuggestion(
                id = "sug-arch",
                title = "System blueprint",
                subtitle = "Zero-trust distributed Rust mesh with gRPC & Raft consensus",
                prompt = "Design a fault-tolerant distributed microservices blueprint using Rust, gRPC streaming, and Raft consensus for global low-latency state sync.",
                badge = "BLUEPRINT",
                iconType = SuggestionIconType.ARCHITECTURE
            )
        )
    }
}

class CubeViewModel(
    private val geminiRepository: GeminiChatRepository = GeminiChatRepository()
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
                title = "New Neural Thread",
                updatedAt = "Just now",
                categoryTag = "ACTIVE",
                messages = emptyList(),
                isPinned = true
            ),
            ChatSession(
                id = "session-webgl-shader",
                title = "Isometric WebGL Cube Shader",
                updatedAt = "14m ago",
                categoryTag = "CODE",
                messages = listOf(
                    ChatMessage(
                        id = "msg-seed-1",
                        isUser = true,
                        text = "How do I structure a minimalist GLSL fragment shader for a glowing cyan wireframe cube?",
                        timestamp = "10:14 AM",
                        modelUsed = "Cube 1.0 Pro"
                    ),
                    ChatMessage(
                        id = "msg-seed-2",
                        isUser = false,
                        text = """
                            ### Isometric Glow Shader Architecture
                            To achieve a crisp **#00F0FF edge luminance** without geometry aliasing, combine barycentric coordinates with screen-space derivative falloff:

                            ```glsl
                            precision highp float;
                            varying vec3 vBarycentric;
                            uniform float uTime;

                            float edgeFactor() {
                                vec3 d = fwidth(vBarycentric);
                                vec3 a3 = smoothstep(vec3(0.0), d * 1.35, vBarycentric);
                                return min(min(a3.x, a3.y), a3.z);
                            }

                            void main() {
                                float edge = 1.0 - edgeFactor();
                                vec3 obsidian = vec3(0.043, 0.059, 0.090); // #0B0F17
                                vec3 cyanGlow = vec3(0.0, 0.941, 1.0);     // #00F0FF
                                float pulse = 0.85 + 0.15 * sin(uTime * 2.4);
                                vec3 finalColor = mix(obsidian, cyanGlow * pulse, edge);
                                gl_FragColor = vec4(finalColor, 1.0);
                            }
                            ```

                            • **Anti-aliased Wireframe**: `fwidth()` calculates screen-space derivatives so lines stay 1.5px sharp at any zoom level.
                            • **Subtle Bloom**: Pair with a single-pass Gaussian blur post-process at threshold 0.72.
                        """.trimIndent(),
                        timestamp = "10:14 AM",
                        modelUsed = "Cube 1.0 Pro",
                        thinkingDurationMs = 520
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

    fun selectModel(model: CubeModelOption) {
        _uiState.update {
            it.copy(
                selectedModel = model,
                customSystemInstruction = model.defaultSystemInstruction,
                statusBannerMessage = "Engine: ${model.name} (${model.geminiModelId})"
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
                statusBannerMessage = if (next) "Live Web Grounding enabled" else "Web Grounding disabled"
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
                statusBannerMessage = "Listening to voice stream…"
            )
        }
        viewModelScope.launch {
            delay(1400)
            _uiState.update { state ->
                if (!state.isVoiceRecording) return@update state
                val voicePrompt = "Design a responsive futuristic dashboard layout with glowing cyan telemetry cards."
                state.copy(
                    isVoiceRecording = false,
                    inputDraft = if (state.inputDraft.isBlank()) voicePrompt else "${state.inputDraft} $voicePrompt",
                    statusBannerMessage = "Voice transcription captured"
                )
            }
        }
    }

    fun addMockAttachment() {
        val samplePool = listOf(
            AttachmentChip(UUID.randomUUID().toString(), "cube_ui_spec_v2.fig", "4.2 MB", "FIGMA"),
            AttachmentChip(UUID.randomUUID().toString(), "telemetry_metrics.json", "128 KB", "JSON"),
            AttachmentChip(UUID.randomUUID().toString(), "neural_shader.glsl", "14 KB", "SHADER"),
            AttachmentChip(UUID.randomUUID().toString(), "architecture_diagram.png", "1.8 MB", "IMAGE")
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
            title = "New Neural Thread",
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
                        title = "New Neural Thread",
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
                statusBannerMessage = "Conversation cleared — reset to Welcome View"
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
                    title = "New Neural Thread",
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
                statusBannerMessage = "Session removed from history"
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
                statusBannerMessage = "Renamed thread to \"$trimmed\""
            )
        }
    }

    fun updateSearchHistoryQuery(query: String) {
        _uiState.update { it.copy(searchHistoryQuery = query) }
    }

    fun toggleOledPitchMode() {
        _uiState.update { state ->
            val next = !state.oledPitchMode
            state.copy(
                oledPitchMode = next,
                statusBannerMessage = if (next) "Theme: OLED Pitch #030508" else "Theme: Stealth #0B0F17"
            )
        }
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
     * Sends a message to the Gemini API using Firebase AI Logic.
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
                    val shouldUpdateTitle = session.messages.isEmpty() || session.title == "New Neural Thread"
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

                // Call Gemini via Firebase AI Logic with 60s timeout
                val accumulated = StringBuilder()
                withTimeout(60_000L) {
                    val streamFlow = geminiRepository.streamMultiTurnChat(
                        geminiModelId = currentState.selectedModel.geminiModelId,
                        systemInstruction = currentState.customSystemInstruction,
                        historyMessages = priorMessages,
                        newPrompt = userMsg.text
                    )

                    streamFlow.collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulated.append(chunkText)
                            val currentText = accumulated.toString()

                            _uiState.update { state ->
                                val updatedSessions = state.sessions.map { session ->
                                    if (session.id == activeId) {
                                        session.copy(
                                            messages = session.messages.map { msg ->
                                                if (msg.id == responseId) {
                                                    msg.copy(
                                                        text = currentText,
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
                }

                val finalOutput = accumulated.toString().ifBlank {
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
                        statusBannerMessage = "Response received via ${currentState.selectedModel.geminiModelId}"
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini inference timed out after 60s", e)
                handleInferenceError(
                    activeId = activeId,
                    responseId = responseId,
                    errorMessage = "Generation timed out after 60 seconds. Please check your connectivity or try a shorter prompt."
                )
            } catch (e: CancellationException) {
                // Must always rethrow CancellationException for cooperative coroutines
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini generation failed", e)
                val displayMsg = e.localizedMessage?.takeIf { it.isNotBlank() }
                    ?: "Gemini request failed (${e.javaClass.simpleName}). Please verify connection and retry."
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

        // Take conversation up to the preceding user message
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
                    statusBannerMessage = "Regenerating with ${current.selectedModel.geminiModelId}…"
                )
            }

            try {
                val accumulated = StringBuilder()
                withTimeout(60_000L) {
                    val streamFlow = geminiRepository.streamMultiTurnChat(
                        geminiModelId = state.selectedModel.geminiModelId,
                        systemInstruction = state.customSystemInstruction,
                        historyMessages = priorHistory,
                        newPrompt = precedingUserPrompt
                    )

                    streamFlow.collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulated.append(chunkText)
                            val currentText = accumulated.toString()

                            _uiState.update { current ->
                                val updatedSessions = current.sessions.map { s ->
                                    if (s.id == session.id) {
                                        s.copy(
                                            messages = s.messages.map { m ->
                                                if (m.id == assistantMessageId) {
                                                    m.copy(
                                                        text = currentText,
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
                }

                val finalOutput = accumulated.toString().ifBlank {
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
                FeedbackState.LIKED -> "Feedback recorded: Helpful response"
                FeedbackState.DISLIKED -> "Feedback recorded: Flagged for calibration"
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
            lower.contains("code") || lower.contains("shader") || lower.contains("javascript") || lower.contains("html") -> "CODE"
            lower.contains("data") || lower.contains("sql") || lower.contains("cohort") || lower.contains("metric") -> "DATA"
            lower.contains("brainstorm") || lower.contains("idea") -> "IDEAS"
            else -> "SYNTH"
        }
    }
}
