package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ChatMessage
import com.example.model.FeedbackState
import com.example.model.QuickSuggestion
import com.example.ui.theme.ClaudeAmber
import com.example.ui.theme.ClaudeErrorRose
import com.example.ui.theme.ClaudeSuccessGreen
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.theme.JetBrainsMonoFontFamily

/**
 * Claude-inspired warm Welcome View with text-only suggestion prompt chips.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CubeWelcomeEmptyState(
    suggestions: List<QuickSuggestion>,
    selectedModelName: String,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("welcome_empty_state"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Claude signature warm terracotta asterism emblem
        CubeHeroEmblem(size = 72.dp)

        Spacer(modifier = Modifier.height(18.dp))

        // Claude-style Serif greeting
        Text(
            text = "How can Cube help you today?",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 30.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 560.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Powered by Google AI Studio's Gemini 1.5 Flash API with full multi-turn conversation memory.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 480.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Claude-Style Text-Only Suggestion Prompt Chips
        FlowRow(
            modifier = Modifier.widthIn(max = 720.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 2
        ) {
            suggestions.forEach { suggestion ->
                ClaudeSuggestionChip(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion.prompt) },
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(min = 260.dp)
                )
            }
        }
    }
}

/**
 * Clean text-only prompt chip inspired by Claude's minimalist desktop and mobile UI.
 */
@Composable
private fun ClaudeSuggestionChip(
    suggestion: QuickSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("suggestion_chip_${suggestion.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = ClaudeTerracotta.copy(alpha = 0.85f),
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = suggestion.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * User message bubble: Clean, warm neutral rounded bubble.
 */
@Composable
fun UserMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("user_message_${message.id}"),
        horizontalAlignment = Alignment.End
    ) {
        // Metadata
        Text(
            text = "You • ${message.timestamp}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 5.dp, end = 6.dp)
        )

        // Staged attachments chips
        if (message.attachments.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                message.attachments.forEach { chip ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = ClaudeTerracotta,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = chip.fileName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Soft, humanistic rounded user bubble
        Box(
            modifier = Modifier
                .widthIn(max = 580.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Assistant (Cube / Claude-style) response card.
 */
@Composable
fun CubeAssistantMessageCard(
    message: ChatMessage,
    onCopyText: (String) -> Unit,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedRecently by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("assistant_message_${message.id}"),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Claude's signature warm terracotta asterism spark avatar
        ClaudeAsterismIcon(
            size = 32.dp,
            tint = ClaudeTerracotta
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Header with Model label & latency
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = "Cube",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ClaudeTerracotta.copy(alpha = 0.10f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = message.modelUsed,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = ClaudeTerracotta
                    )
                }
                Text(
                    text = if (message.isStreaming) "typing…" else "${message.thinkingDurationMs}ms • ${message.timestamp}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Message Body Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (message.isError) ClaudeErrorRose.copy(alpha = 0.06f)
                        else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        width = 1.dp,
                        color = if (message.isError) ClaudeErrorRose.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FormattedAssistantMessageBody(
                    rawText = message.text,
                    isStreaming = message.isStreaming,
                    onCopyCodeBlock = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        onCopyText(code)
                    }
                )
            }

            // Action Buttons Row below Assistant response
            AnimatedVisibility(
                visible = !message.isStreaming,
                enter = fadeIn() + slideInVertically()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .testTag("assistant_actions_${message.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Copy Text Button
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.text))
                            copiedRecently = true
                            onCopyText(message.text)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_copy_${message.id}")
                    ) {
                        Icon(
                            imageVector = if (copiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.action_copy),
                            tint = if (copiedRecently) ClaudeSuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Thumbs Up Button
                    IconButton(
                        onClick = onThumbsUp,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_thumbs_up_${message.id}")
                    ) {
                        val isLiked = message.feedback == FeedbackState.LIKED
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = stringResource(R.string.action_thumbs_up),
                            tint = if (isLiked) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Thumbs Down Button
                    IconButton(
                        onClick = onThumbsDown,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_thumbs_down_${message.id}")
                    ) {
                        val isDisliked = message.feedback == FeedbackState.DISLIKED
                        Icon(
                            imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                            contentDescription = stringResource(R.string.action_thumbs_down),
                            tint = if (isDisliked) ClaudeErrorRose else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Regenerate Response Button
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_regenerate_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.action_regenerate),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormattedAssistantMessageBody(
    rawText: String,
    isStreaming: Boolean,
    onCopyCodeBlock: (String) -> Unit
) {
    val segments = remember(rawText) { parseMarkdownSegments(rawText) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        segments.forEachIndexed { index, segment ->
            val isLastSegment = index == segments.lastIndex
            when (segment) {
                is MessageSegment.CodeBlock -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E22))
                            .border(1.dp, Color(0xFF333338), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF28282E))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = segment.language.ifBlank { "code" }.lowercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                color = ClaudeAmber
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onCopyCodeBlock(segment.code) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = Color(0xFFA8A29E),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Copy",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFFA8A29E)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(12.dp)
                        ) {
                            Text(
                                text = segment.code,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp
                                ),
                                color = Color(0xFFF5F5F4)
                            )
                        }
                    }
                }

                is MessageSegment.Prose -> {
                    val displayProse = segment.text
                        .replace("### ", "")
                        .replace("**", "")

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = displayProse,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isStreaming && isLastSegment) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp, bottom = 4.dp)
                                    .size(width = 6.dp, height = 15.dp)
                                    .background(ClaudeTerracotta)
                            )
                        }
                    }
                }
            }
        }
    }
}

private sealed class MessageSegment {
    data class Prose(val text: String) : MessageSegment()
    data class CodeBlock(val language: String, val code: String) : MessageSegment()
}

private fun parseMarkdownSegments(input: String): List<MessageSegment> {
    if (!input.contains("```")) {
        return listOf(MessageSegment.Prose(input))
    }
    val parts = input.split("```")
    val result = mutableListOf<MessageSegment>()
    parts.forEachIndexed { idx, part ->
        if (idx % 2 == 0) {
            val trimmed = part.trim()
            if (trimmed.isNotEmpty()) {
                result.add(MessageSegment.Prose(trimmed))
            }
        } else {
            val lines = part.lines()
            val lang = lines.firstOrNull()?.trim().orEmpty()
            val codeContent = if (lines.size > 1) {
                lines.drop(1).joinToString("\n").trimEnd()
            } else {
                part.trim()
            }
            result.add(MessageSegment.CodeBlock(language = lang, code = codeContent))
        }
    }
    return if (result.isEmpty()) listOf(MessageSegment.Prose(input)) else result
}

@Composable
fun CubeThinkingIndicator(
    modelName: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "claude_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cube_thinking_indicator"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ClaudeAsterismIcon(size = 32.dp, tint = ClaudeTerracotta)

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(ClaudeTerracotta.copy(alpha = pulse))
            )
            Text(
                text = "Cube ($modelName) is thinking…",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
