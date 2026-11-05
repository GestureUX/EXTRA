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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.ui.graphics.Brush
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
import com.example.model.SuggestionIconType
import com.example.ui.theme.CubeAmber
import com.example.ui.theme.CubeCoralRed
import com.example.ui.theme.CubeCyan
import com.example.ui.theme.CubeElectricBlue
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeNeonPurple
import com.example.ui.theme.CubeTextMuted
import com.example.ui.theme.CubeTextPrimary
import com.example.ui.theme.CubeTextSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily

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
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("welcome_empty_state"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Central glowing Cube Emblem
        CubeHeroEmblem(size = 86.dp)

        Spacer(modifier = Modifier.height(18.dp))

        // Active engine badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(CubeCyan.copy(alpha = 0.10f))
                .border(1.dp, CubeCyan.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = "$selectedModelName • NEURAL CORE ONLINE",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = CubeCyan
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Greeting Title
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            color = CubeTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 580.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = CubeTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 500.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 4 Quick-Start Suggestion Cards
        FlowRow(
            modifier = Modifier.widthIn(max = 760.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            suggestions.forEach { suggestion ->
                QuickSuggestionCard(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion.prompt) },
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(min = 240.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickSuggestionCard(
    suggestion: QuickSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (accentColor, iconVector) = when (suggestion.iconType) {
        SuggestionIconType.CODE -> CubeCyan to Icons.Default.Terminal
        SuggestionIconType.DATA -> CubeElectricBlue to Icons.Default.Analytics
        SuggestionIconType.BRAINSTORM -> CubeNeonPurple to Icons.Default.Lightbulb
        SuggestionIconType.ARCHITECTURE -> CubeEmerald to Icons.Default.Hub
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.45f),
                        MaterialTheme.colorScheme.outline
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("suggestion_card_${suggestion.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.14f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = suggestion.badge,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = accentColor
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = CubeTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = suggestion.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CubeTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = suggestion.subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
            color = CubeTextSecondary
        )
    }
}

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
        // Metadata row above user bubble
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 6.dp, end = 4.dp)
        ) {
            if (message.webSearchUsed) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CubeCyan.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = CubeCyan,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "WEB",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = CubeCyan
                    )
                }
            }
            Text(
                text = "YOU • ${message.timestamp}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = CubeTextMuted
            )
        }

        // Attached files if any
        if (message.attachments.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                message.attachments.forEach { chip ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, CubeCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = CubeCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${chip.fileName} (${chip.fileSize})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = CubeTextPrimary
                        )
                    }
                }
            }
        }

        // Distinct right-aligned user message bubble
        Box(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 4.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF15263B),
                            Color(0xFF111C2D)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CubeCyan.copy(alpha = 0.55f),
                            CubeElectricBlue.copy(alpha = 0.25f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 4.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = CubeTextPrimary
            )
        }
    }
}

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

    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("assistant_message_${message.id}"),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Unique Cube Avatar on the left
        CubeIsometricIcon(
            size = 36.dp,
            animated = message.isStreaming
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Header with Cube model & latency badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = "CUBE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = CubeTextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CubeCyan.copy(alpha = 0.12f))
                        .border(1.dp, CubeCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = message.modelUsed,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = CubeCyan
                    )
                }
                Text(
                    text = if (message.isStreaming) "STREAMING…" else "${message.thinkingDurationMs}ms • ${message.timestamp}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (message.isStreaming) CubeCyan else CubeTextMuted
                )
            }

            // Assistant Content Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .background(
                        if (message.isError) CubeCoralRed.copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        width = 1.dp,
                        color = when {
                            message.isError -> CubeCoralRed.copy(alpha = 0.75f)
                            message.isStreaming -> CubeCyan.copy(alpha = 0.45f)
                            else -> MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FormattedAssistantMessageBody(
                    rawText = message.text,
                    isStreaming = message.isStreaming,
                    cursorAlpha = cursorAlpha,
                    onCopyCodeBlock = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        onCopyText(code)
                    }
                )
            }

            // Action Buttons Row below AI response: Copy text, Thumbs Up, Thumbs Down, Regenerate response
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
                            .size(48.dp)
                            .testTag("action_copy_${message.id}")
                    ) {
                        Icon(
                            imageVector = if (copiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.action_copy),
                            tint = if (copiedRecently) CubeEmerald else CubeTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Thumbs Up Button
                    IconButton(
                        onClick = onThumbsUp,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("action_thumbs_up_${message.id}")
                    ) {
                        val isLiked = message.feedback == FeedbackState.LIKED
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = stringResource(R.string.action_thumbs_up),
                            tint = if (isLiked) CubeCyan else CubeTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Thumbs Down Button
                    IconButton(
                        onClick = onThumbsDown,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("action_thumbs_down_${message.id}")
                    ) {
                        val isDisliked = message.feedback == FeedbackState.DISLIKED
                        Icon(
                            imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                            contentDescription = stringResource(R.string.action_thumbs_down),
                            tint = if (isDisliked) CubeCoralRed else CubeTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Regenerate Response Button
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("action_regenerate_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.action_regenerate),
                            tint = CubeTextSecondary,
                            modifier = Modifier.size(18.dp)
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
    cursorAlpha: Float,
    onCopyCodeBlock: (String) -> Unit
) {
    // Split text into normal paragraphs and ```code``` blocks for clean technical display
    val segments = remember(rawText) { parseMarkdownSegments(rawText) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        segments.forEachIndexed { index, segment ->
            val isLastSegment = index == segments.lastIndex
            when (segment) {
                is MessageSegment.CodeBlock -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF070A10))
                            .border(1.dp, CubeCyan.copy(alpha = 0.30f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0E1522))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = segment.language.ifBlank { "code" }.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = CubeCyan
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
                                    contentDescription = "Copy code snippet",
                                    tint = CubeTextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "COPY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                    color = CubeTextSecondary
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
                                color = Color(0xFFE2E8F0)
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
                            color = CubeTextPrimary,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isStreaming && isLastSegment) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp, bottom = 3.dp)
                                    .size(width = 8.dp, height = 16.dp)
                                    .background(CubeCyan.copy(alpha = cursorAlpha))
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
    val infiniteTransition = rememberInfiniteTransition(label = "thinking_dots")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
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
        CubeIsometricIcon(size = 36.dp, animated = true)

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, CubeCyan.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CubeCyan.copy(alpha = pulse))
            )
            Text(
                text = "$modelName is synthesizing response…",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp),
                color = CubeCyan
            )
        }
    }
}
