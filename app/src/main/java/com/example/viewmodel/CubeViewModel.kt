package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AttachmentChip
import com.example.model.ChatMessage
import com.example.model.ChatSession
import com.example.model.CubeModelOption
import com.example.model.FeedbackState
import com.example.model.QuickSuggestion
import com.example.model.SuggestionIconType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class CubeUiState(
    val sessions: List<ChatSession> = emptyList(),
    val activeSessionId: String = "",
    val isSidebarExpanded: Boolean = true,
    val selectedModel: CubeModelOption = defaultModels.first(),
    val availableModels: List<CubeModelOption> = defaultModels,
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
                badge = "DEEP REASONING",
                description = "Flagship multi-step synthesis, system architecture, and deep code analysis.",
                latencyLabel = "~42 tok/s"
            ),
            CubeModelOption(
                id = "cube-fast",
                name = "Cube Fast",
                badge = "ULTRA-LOW LATENCY",
                description = "Sub-second streaming optimized for rapid iteration and instant answers.",
                latencyLabel = "~145 tok/s"
            ),
            CubeModelOption(
                id = "cube-quantum",
                name = "Cube Quantum",
                badge = "CODE & MATH",
                description = "Specialized formal verification, shaders, SQL telemetry, and algorithms.",
                latencyLabel = "~68 tok/s"
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

class CubeViewModel : ViewModel() {

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
                            To achieve a crisp **Grok-inspired #00F0FF edge luminance** without geometry aliasing, combine barycentric coordinates with an exponential distance falloff:

                            ```glsl
                            // Fragment Shader • Cube 1.0 Pro
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
                            • **Subtle Bloom**: Pair with a single-pass Gaussian blur post-process at `threshold = 0.72`.
                        """.trimIndent(),
                        timestamp = "10:14 AM",
                        modelUsed = "Cube 1.0 Pro",
                        thinkingDurationMs = 520
                    )
                )
            ),
            ChatSession(
                id = "session-rust-raft",
                title = "Zero-Trust Rust gRPC Mesh",
                updatedAt = "2h ago",
                categoryTag = "ARCH",
                messages = listOf(
                    ChatMessage(
                        id = "msg-seed-3",
                        isUser = true,
                        text = "Compare Tonic gRPC streaming vs raw QUIC streams for inter-node Raft heartbeats.",
                        timestamp = "08:40 AM",
                        modelUsed = "Cube Quantum",
                        webSearchUsed = true
                    ),
                    ChatMessage(
                        id = "msg-seed-4",
                        isUser = false,
                        text = """
                            ### Raft Transport Layer Benchmark: Tonic gRPC vs Raw QUIC (`quinn`)

                            1. **Head-of-Line (HoL) Blocking**
                               • **Tonic (HTTP/2 over TCP)**: Multiplexes streams over a single TCP connection; a single dropped packet stalls all in-flight append-entries.
                               • **Raw QUIC (`quinn`)**: Independent stream framing over UDP eliminates transport-level HoL blocking, reducing p99 tail latency by **38%**.

                            2. **mTLS & Zero-Trust Identity**
                               • QUIC embeds TLS 1.3 directly into the 1-RTT (and 0-RTT resumption) handshake, saving 1–2 round trips during node recovery.

                            ```rust
                            // Recommended QUIC Bi-Directional Heartbeat Stream
                            pub async fn send_heartbeat(conn: &quinn::Connection, payload: &[u8]) -> anyhow::Result<()> {
                                let (mut send, mut recv) = conn.open_bi().await?;
                                send.write_all(payload).await?;
                                send.finish()?;
                                Ok(())
                            }
                            ```
                        """.trimIndent(),
                        timestamp = "08:40 AM",
                        modelUsed = "Cube Quantum",
                        webSearchUsed = true,
                        thinkingDurationMs = 790
                    )
                )
            ),
            ChatSession(
                id = "session-cohort-sql",
                title = "Q4 Retention Cohort Telemetry",
                updatedAt = "Yesterday",
                categoryTag = "DATA",
                messages = listOf(
                    ChatMessage(
                        id = "msg-seed-5",
                        isUser = true,
                        text = "Give me a quick executive summary of why Day-14 retention drops when onboarding exceeds 3 steps.",
                        timestamp = "Yesterday",
                        modelUsed = "Cube Fast"
                    ),
                    ChatMessage(
                        id = "msg-seed-6",
                        isUser = false,
                        text = """
                            ### Onboarding Friction & Day-14 Retention Decay
                            • **Time-to-First-Value (TTFV)**: Users who execute their first prompt within **45 seconds** exhibit a **2.7x higher Day-14 retention** (`41.8%` vs `15.4%`).
                            • **Cognitive Drop-off**: Every mandatory configuration step before the workspace view compounds abandonment by ~18%.
                            • **Actionable Fix**: Defer profile setup and API configuration until after the user completes 3 interactive sessions.
                        """.trimIndent(),
                        timestamp = "Yesterday",
                        modelUsed = "Cube Fast",
                        thinkingDurationMs = 310
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
                statusBannerMessage = "Switched engine to ${model.name} (${model.latencyLabel})"
            )
        }
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
                statusBannerMessage = "Listening to neural voice stream…"
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
        val fullGeneratedText = craftCubeMockResponse(
            prompt = userMsg.text,
            modelName = currentState.selectedModel.name,
            webSearch = currentState.isWebSearchEnabled,
            attachments = userMsg.attachments
        )

        streamingJob = viewModelScope.launch {
            // Simulate 1-second neural thinking latency as specified in the prompt
            delay(950)

            val initialBotMsg = ChatMessage(
                id = responseId,
                isUser = false,
                text = "",
                timestamp = SimpleDateFormat("hh:mm a", Locale.US).format(Date()),
                modelUsed = currentState.selectedModel.name,
                isStreaming = true,
                webSearchUsed = currentState.isWebSearchEnabled,
                thinkingDurationMs = (420..890).random()
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

            // Stream chunks smoothly
            val words = fullGeneratedText.split(" ")
            val builder = StringBuilder()
            val chunkSize = 3
            var index = 0
            while (index < words.size) {
                val end = (index + chunkSize).coerceAtMost(words.size)
                val chunk = words.subList(index, end).joinToString(" ")
                if (builder.isNotEmpty()) builder.append(" ")
                builder.append(chunk)
                val partialText = builder.toString()

                _uiState.update { state ->
                    val updatedSessions = state.sessions.map { session ->
                        if (session.id == activeId) {
                            session.copy(
                                messages = session.messages.map { msg ->
                                    if (msg.id == responseId) msg.copy(text = partialText, isStreaming = true)
                                    else msg
                                }
                            )
                        } else session
                    }
                    state.copy(sessions = updatedSessions)
                }
                index = end
                delay(28)
            }

            // Mark streaming complete
            _uiState.update { state ->
                val updatedSessions = state.sessions.map { session ->
                    if (session.id == activeId) {
                        session.copy(
                            messages = session.messages.map { msg ->
                                if (msg.id == responseId) msg.copy(text = fullGeneratedText, isStreaming = false)
                                else msg
                            }
                        )
                    } else session
                }
                state.copy(
                    sessions = updatedSessions,
                    isStreamingResponse = false
                )
            }
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
            ?.text ?: "Synthesize an alternative optimization strategy."

        streamingJob?.cancel()

        val regeneratedText = craftCubeMockResponse(
            prompt = precedingUserPrompt,
            modelName = state.selectedModel.name,
            webSearch = state.isWebSearchEnabled,
            attachments = emptyList(),
            isRegeneration = true
        )

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
                    statusBannerMessage = "Regenerating response with ${current.selectedModel.name}…"
                )
            }

            delay(550)
            val words = regeneratedText.split(" ")
            val builder = StringBuilder()
            var index = 0
            while (index < words.size) {
                val end = (index + 4).coerceAtMost(words.size)
                val chunk = words.subList(index, end).joinToString(" ")
                if (builder.isNotEmpty()) builder.append(" ")
                builder.append(chunk)
                val currentPartial = builder.toString()

                _uiState.update { current ->
                    val updatedSessions = current.sessions.map { s ->
                        if (s.id == session.id) {
                            s.copy(
                                messages = s.messages.map { m ->
                                    if (m.id == assistantMessageId) m.copy(text = currentPartial, isStreaming = true)
                                    else m
                                }
                            )
                        } else s
                    }
                    current.copy(sessions = updatedSessions)
                }
                index = end
                delay(24)
            }

            _uiState.update { current ->
                val updatedSessions = current.sessions.map { s ->
                    if (s.id == session.id) {
                        s.copy(
                            messages = s.messages.map { m ->
                                if (m.id == assistantMessageId) m.copy(text = regeneratedText, isStreaming = false)
                                else m
                            }
                        )
                    } else s
                }
                current.copy(sessions = updatedSessions, isStreamingResponse = false)
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

    private fun craftCubeMockResponse(
        prompt: String,
        modelName: String,
        webSearch: Boolean,
        attachments: List<AttachmentChip>,
        isRegeneration: Boolean = false
    ): String {
        val lower = prompt.lowercase()
        val webHeader = if (webSearch) {
            "🌐 **Live Web Grounding Active** • Indexed 4 real-time technical sources (`latency: 84ms`)\n\n"
        } else ""

        val attachmentHeader = if (attachments.isNotEmpty()) {
            "📎 **Parsed Attachments**: ${attachments.joinToString { "`${it.fileName}` (${it.fileSize})" }}\n\n"
        } else ""

        val regenBadge = if (isRegeneration) "🔄 **Alternative Neural Pass ($modelName)**\n\n" else ""

        val coreBody = when {
            lower.contains("code") || lower.contains("shader") || lower.contains("webgl") || lower.contains("canvas") -> """
                ### 3D Isometric Particle Cube Engine
                Here is a complete, zero-dependency implementation engineered for **60fps rendering** with `#00F0FF` electric cyan edge illumination:

                ```javascript
                // Cube Neural Renderer • 60fps Canvas Pipeline
                const canvas = document.getElementById('cubeCanvas');
                const ctx = canvas.getContext('2d');

                const vertices = [
                  [-1, -1, -1], [1, -1, -1], [1, 1, -1], [-1, 1, -1],
                  [-1, -1,  1], [1, -1,  1], [1, 1,  1], [-1, 1,  1]
                ];

                const edges = [
                  [0,1], [1,2], [2,3], [3,0],
                  [4,5], [5,6], [6,7], [7,4],
                  [0,4], [1,5], [2,6], [3,7]
                ];

                function project([x, y, z], angle) {
                  const cos = Math.cos(angle), sin = Math.sin(angle);
                  const rx = x * cos - z * sin;
                  const rz = x * sin + z * cos;
                  const scale = 220 / (4 + rz);
                  return [canvas.width / 2 + rx * scale, canvas.height / 2 + y * scale];
                }

                function renderFrame(time) {
                  ctx.fillStyle = '#0B0F17';
                  ctx.fillRect(0, 0, canvas.width, canvas.height);
                  ctx.strokeStyle = '#00F0FF';
                  ctx.shadowColor = '#00F0FF';
                  ctx.shadowBlur = 14;
                  ctx.lineWidth = 1.8;

                  const pts = vertices.map(v => project(v, time * 0.0012));
                  edges.forEach(([i, j]) => {
                    ctx.beginPath();
                    ctx.moveTo(...pts[i]);
                    ctx.lineTo(...pts[j]);
                    ctx.stroke();
                  });
                  requestAnimationFrame(renderFrame);
                }
                requestAnimationFrame(renderFrame);
                ```

                • **Hardware Acceleration**: Uses `shadowBlur` compositing for subtle neon bloom without WebGL context overhead.
                • **Tip**: Tap the **HTML / CSS / JS Code** button in the top bar anytime to inspect or copy the full single-file web source.
            """.trimIndent()

            lower.contains("data") || lower.contains("sql") || lower.contains("retention") || lower.contains("cohort") -> """
                ### Telemetry & Retention Cohort Diagnosis
                Synthesized by **$modelName** across product activation funnels:

                1. **Primary Bottleneck Identified**
                   • Users who do not trigger an interactive prompt within the first **90 seconds** account for **68% of Week-2 churn**.
                   • Adding inline quick-start suggestion cards lifts immediate activation from `31.2%` to `74.6%`.

                ```sql
                -- Cohort Retention & Activation Velocity Query
                WITH user_first_action AS (
                    SELECT
                        user_id,
                        DATE_TRUNC('week', created_at) AS cohort_week,
                        EXTRACT(EPOCH FROM (first_prompt_at - created_at)) AS ttfv_seconds
                    FROM analytics.user_sessions
                )
                SELECT
                    cohort_week,
                    COUNT(*) FILTER (WHERE ttfv_seconds <= 60) AS fast_activators,
                    ROUND(AVG(ttfv_seconds)::numeric, 1) AS avg_ttfv_sec,
                    ROUND(100.0 * COUNT(*) FILTER (WHERE retained_d14) / COUNT(*), 2) AS d14_retention_pct
                FROM user_first_action
                GROUP BY 1
                ORDER BY 1 DESC;
                ```

                • **Recommendation**: Pre-populate 4 high-signal prompt cards on the empty welcome view to eliminate blank-canvas hesitation.
            """.trimIndent()

            lower.contains("brainstorm") || lower.contains("idea") -> """
                ### 4 High-Conviction AI Product Concepts
                Engineered for ultra-low latency and high-contrast developer workflows:

                1. **Cube Diff Sentinel**
                   • Real-time AST semantic analyzer that flags race conditions and memory leaks in `<12ms` as you type.
                2. **Neural Shader Forge**
                   • Natural-language to GLSL/WGSL live viewport with instant uniform sliders and frame-time profiling.
                3. **Zero-Config Telemetry Lens**
                   • Drops into any PostgreSQL or ClickHouse stream and synthesizes anomaly root-cause trees automatically.
                4. **Spec-to-UI Matrix**
                   • Converts dark-mode wireframe descriptions directly into production Tailwind CSS & Jetpack Compose components.
            """.trimIndent()

            else -> """
                ### Cube Neural Synthesis ($modelName)
                I have analyzed your request: *"$prompt"*

                • **Architectural Precision**: Designed around a deep `#0B0F17` stealth canvas with `#00F0FF` electric cyan accents for zero eye strain during extended sessions.
                • **Interactive Capabilities**:
                  1. **Collapsible Sidebar** with live session switching, inline rename, and instant deletion.
                  2. **Floating Grok-Style Console** with focus aura, auto-expanding multi-line input, Web Search grounding toggle, and file attachment chips.
                  3. **Full Web Code Inspector**: Tap the **`</>`** icon in the header to view and copy the complete standalone HTML, Tailwind CSS, and JavaScript implementation.

                ```json
                {
                  "engine": "$modelName",
                  "theme": "#0B0F17 Stealth Cyan",
                  "webGrounding": $webSearch,
                  "status": "READY_FOR_NEXT_COMMAND"
                }
                ```
            """.trimIndent()
        }

        return "$regenBadge$webHeader$attachmentHeader$coreBody"
    }
}
