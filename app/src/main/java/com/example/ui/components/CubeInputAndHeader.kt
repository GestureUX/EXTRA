package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AttachmentChip
import com.example.model.CubeModelOption
import com.example.ui.theme.CubeCoralRed
import com.example.ui.theme.CubeCyan
import com.example.ui.theme.CubeElectricBlue
import com.example.ui.theme.CubeTextMuted
import com.example.ui.theme.CubeTextPrimary
import com.example.ui.theme.CubeTextSecondary

@Composable
fun CubeTopHeader(
    selectedModel: CubeModelOption,
    availableModels: List<CubeModelOption>,
    showMenuButton: Boolean,
    onToggleSidebar: () -> Unit,
    onSelectModel: (CubeModelOption) -> Unit,
    onClearChat: () -> Unit,
    onOpenCodeInspector: () -> Unit,
    modifier: Modifier = Modifier
) {
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cube_top_header"),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
        tonalElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Sidebar Hamburger + "Cube AI" Title + Model Selector Dropdown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (showMenuButton) {
                        IconButton(
                            onClick = onToggleSidebar,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("header_toggle_sidebar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.toggle_sidebar),
                                tint = CubeTextPrimary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CubeIsometricIcon(size = 28.dp, animated = false)
                        Text(
                            text = stringResource(R.string.header_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CubeTextPrimary
                        )
                    }

                    // Model Selector Dropdown Pill
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = CubeCyan.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { modelDropdownExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("header_model_selector"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(CubeCyan)
                            )
                            Text(
                                text = selectedModel.name,
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                color = CubeTextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select AI Model",
                                tint = CubeCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = modelDropdownExpanded,
                            onDismissRequest = { modelDropdownExpanded = false },
                            modifier = Modifier
                                .width(320.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, CubeCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            availableModels.forEach { option ->
                                val isCurrent = option.id == selectedModel.id
                                DropdownMenuItem(
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = option.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.5.sp
                                                    ),
                                                    color = if (isCurrent) CubeCyan else CubeTextPrimary
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(CubeCyan.copy(alpha = 0.12f))
                                                        .border(1.dp, CubeCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = option.badge,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = CubeCyan
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Role: ${option.roleTitle} (${option.geminiModelId})",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                color = CubeElectricBlue
                                            )
                                            Text(
                                                text = option.description,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.5.sp),
                                                color = CubeTextSecondary
                                            )
                                        }
                                    },
                                    onClick = {
                                        onSelectModel(option)
                                        modelDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("model_option_${option.id}")
                                )
                            }
                        }
                    }
                }

                // Right: HTML/CSS/JS Code Inspector Button + Clear Chat Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onOpenCodeInspector,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CubeCyan.copy(alpha = 0.10f))
                            .border(1.dp, CubeCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .testTag("header_code_inspector_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = stringResource(R.string.view_code_inspector),
                            tint = CubeCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(onClick = onClearChat)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("header_clear_chat_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = stringResource(R.string.clear_chat),
                            tint = CubeCoralRed.copy(alpha = 0.9f),
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = stringResource(R.string.clear_chat),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                            color = CubeTextPrimary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outline)
                    .padding(top = 1.dp)
            )
        }
    }
}

@Composable
fun CubeFloatingInputConsole(
    inputText: String,
    isWebSearchEnabled: Boolean,
    isVoiceRecording: Boolean,
    stagedAttachments: List<AttachmentChip>,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onToggleWebSearch: () -> Unit,
    onToggleVoiceInput: () -> Unit,
    onAddAttachment: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isInputFocused by remember { mutableStateOf(false) }

    val borderAlpha by animateFloatAsState(
        targetValue = if (isInputFocused) 0.85f else 0.35f,
        label = "focus_border_alpha"
    )
    val glowElevation by animateDpAsState(
        targetValue = if (isInputFocused) 18.dp else 6.dp,
        label = "focus_glow_elevation"
    )

    val sendInteractionSource = remember { MutableInteractionSource() }
    val isSendPressed by sendInteractionSource.collectIsPressedAsState()
    val sendScale by animateFloatAsState(
        targetValue = if (isSendPressed) 0.92f else 1.0f,
        label = "send_scale"
    )

    val canSend = inputText.isNotBlank() || stagedAttachments.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 780.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = glowElevation,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = CubeCyan,
                    spotColor = CubeCyan
                )
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = if (isInputFocused) 1.5.dp else 1.dp,
                    brush = Brush.linearGradient(
                        colors = if (isInputFocused) {
                            listOf(
                                CubeCyan.copy(alpha = borderAlpha),
                                CubeElectricBlue.copy(alpha = 0.65f),
                                CubeCyan.copy(alpha = borderAlpha)
                            )
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.outlineVariant,
                                MaterialTheme.colorScheme.outline
                            )
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("floating_input_console"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Staged Attachment Chips Row
            if (stagedAttachments.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stagedAttachments, key = { it.id }) { chip ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CubeCyan.copy(alpha = 0.12f))
                                .border(1.dp, CubeCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${chip.fileName} • ${chip.fileSize}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = CubeTextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment",
                                tint = CubeCyan,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { onRemoveAttachment(chip.id) }
                            )
                        }
                    }
                }
            }

            // Auto-expanding Textarea
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 36.dp, max = 130.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = stringResource(R.string.input_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = CubeTextMuted
                    )
                }

                BasicTextField(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = CubeTextPrimary),
                    cursorBrush = SolidColor(CubeCyan),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSend) onSendMessage()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isInputFocused = it.isFocused }
                        .testTag("chat_input_field")
                )
            }

            // Bottom Control Bar inside Floating Input Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left tools: Attachment Icon, Web Search Toggle, Voice Input Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. Attachment Icon Button
                    IconButton(
                        onClick = onAddAttachment,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .testTag("input_attach_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = stringResource(R.string.attach_file),
                            tint = CubeTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 2. Web Search Toggle Pill
                    val webBg by animateColorAsState(
                        targetValue = if (isWebSearchEnabled) {
                            CubeCyan.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        label = "web_bg"
                    )
                    val webBorder by animateColorAsState(
                        targetValue = if (isWebSearchEnabled) {
                            CubeCyan.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        label = "web_border"
                    )

                    Row(
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(webBg)
                            .border(1.dp, webBorder, RoundedCornerShape(12.dp))
                            .clickable(onClick = onToggleWebSearch)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("input_web_search_toggle"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = stringResource(R.string.web_search),
                            tint = if (isWebSearchEnabled) CubeCyan else CubeTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.web_search),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                            color = if (isWebSearchEnabled) CubeCyan else CubeTextSecondary
                        )
                    }

                    // 3. Voice Input Button
                    IconButton(
                        onClick = onToggleVoiceInput,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isVoiceRecording) CubeCoralRed.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isVoiceRecording) CubeCoralRed else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("input_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = stringResource(R.string.voice_input),
                            tint = if (isVoiceRecording) CubeCoralRed else CubeTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Right: Prominent Send Button with smooth click animation
                val sendBgColor by animateColorAsState(
                    targetValue = if (canSend) CubeCyan else MaterialTheme.colorScheme.surfaceVariant,
                    label = "send_bg"
                )
                val sendContentColor by animateColorAsState(
                    targetValue = if (canSend) Color(0xFF03171C) else CubeTextMuted,
                    label = "send_fg"
                )

                Row(
                    modifier = Modifier
                        .scale(sendScale)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(sendBgColor)
                        .clickable(
                            interactionSource = sendInteractionSource,
                            indication = null,
                            enabled = canSend,
                            onClick = onSendMessage
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("input_send_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SEND",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = sendContentColor
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.send_message),
                        tint = sendContentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
