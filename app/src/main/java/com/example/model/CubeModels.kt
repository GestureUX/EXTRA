package com.example.model

data class CubeModelOption(
    val id: String,
    val name: String,
    val geminiModelId: String,
    val badge: String,
    val roleTitle: String,
    val description: String,
    val latencyLabel: String,
    val defaultSystemInstruction: String
)

enum class FeedbackState {
    NONE,
    LIKED,
    DISLIKED
}

data class AttachmentChip(
    val id: String,
    val fileName: String,
    val fileSize: String,
    val type: String
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val modelUsed: String = "Cube 1.0 Pro",
    val isStreaming: Boolean = false,
    val isError: Boolean = false,
    val webSearchUsed: Boolean = false,
    val attachments: List<AttachmentChip> = emptyList(),
    val feedback: FeedbackState = FeedbackState.NONE,
    val thinkingDurationMs: Int = 450
)

data class ChatSession(
    val id: String,
    val title: String,
    val updatedAt: String,
    val categoryTag: String,
    val messages: List<ChatMessage>,
    val isPinned: Boolean = false
)

data class QuickSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val prompt: String,
    val badge: String,
    val iconType: SuggestionIconType
)

enum class SuggestionIconType {
    CODE,
    DATA,
    BRAINSTORM,
    ARCHITECTURE
}
