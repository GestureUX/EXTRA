package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ChatSession
import com.example.ui.theme.CubeCoralRed
import com.example.ui.theme.CubeCyan
import com.example.ui.theme.CubeElectricBlue
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeTextMuted
import com.example.ui.theme.CubeTextPrimary
import com.example.ui.theme.CubeTextSecondary

@Composable
fun CubeSidebar(
    sessions: List<ChatSession>,
    activeSessionId: String,
    searchQuery: String,
    oledPitchMode: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onNewChat: () -> Unit,
    onSelectSession: (String) -> Unit,
    onRenameSession: (ChatSession) -> Unit,
    onDeleteSession: (String) -> Unit,
    onToggleThemeMode: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCodeInspector: () -> Unit,
    onCollapseSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredSessions = if (searchQuery.isBlank()) {
        sessions
    } else {
        sessions.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.categoryTag.contains(searchQuery, ignoreCase = true)
        }
    }

    Surface(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .testTag("cube_sidebar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            // 1. TOP SECTION: Brand Logo ("Cube" + futuristic cube icon) + Collapse + "New Chat" button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CubeIsometricIcon(size = 36.dp, animated = true)
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "CUBE",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = CubeTextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CubeCyan.copy(alpha = 0.14f))
                                        .border(1.dp, CubeCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "AI 2.4",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = CubeCyan
                                    )
                                }
                            }
                            Text(
                                text = "NEURAL INTERFACE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = CubeTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onCollapseSidebar,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("sidebar_collapse_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.toggle_sidebar),
                            tint = CubeTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prominent "New Chat" Button with Cyan Neon Border & Subtle Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    CubeCyan.copy(alpha = 0.18f),
                                    CubeElectricBlue.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(CubeCyan.copy(alpha = 0.7f), CubeElectricBlue.copy(alpha = 0.4f))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(onClick = onNewChat)
                        .testTag("sidebar_new_chat_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.new_chat),
                            tint = CubeCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.new_chat),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            ),
                            color = CubeTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compact Filter / Search bar for recent sessions
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.sidebar_search_hint),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
                            color = CubeTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search sessions",
                            tint = CubeTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear filter",
                                    tint = CubeTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.5.sp,
                        color = CubeTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        focusedBorderColor = CubeCyan.copy(alpha = 0.6f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sidebar_search_input")
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // 2. MIDDLE SECTION: Recent Chat History List with Rename & Delete icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.sidebar_recent_chats).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = CubeTextMuted
                )
                Text(
                    text = "${filteredSessions.size} THREADS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CubeCyan.copy(alpha = 0.8f)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("sidebar_history_list"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredSessions, key = { it.id }) { session ->
                    val isSelected = session.id == activeSessionId
                    SidebarSessionItem(
                        session = session,
                        isSelected = isSelected,
                        onSelect = { onSelectSession(session.id) },
                        onRename = { onRenameSession(session) },
                        onDelete = { onDeleteSession(session.id) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // 3. BOTTOM SECTION: HTML/CSS/JS Inspector Shortcut, Theme/Mode Indicator, User Profile & Settings
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Theme / Mode Indicator row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onToggleThemeMode)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("sidebar_theme_toggle"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (oledPitchMode) CubeEmerald else CubeCyan)
                        )
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = CubeCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (oledPitchMode) {
                                stringResource(R.string.theme_mode_oled)
                            } else {
                                stringResource(R.string.theme_mode_stealth)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = CubeTextPrimary
                        )
                    }
                    Text(
                        text = if (oledPitchMode) "#030508" else "#0B0F17",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CubeCyan
                    )
                }

                // Export / Inspect Single-File HTML/CSS/JS Source Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CubeCyan.copy(alpha = 0.08f))
                        .border(1.dp, CubeCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenCodeInspector)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("sidebar_code_inspector_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = CubeCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.view_code_inspector),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                            color = CubeTextPrimary
                        )
                    }
                    Text(
                        text = "VIEW",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CubeCyan
                    )
                }

                // User Profile + Settings Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(CubeCyan, CubeElectricBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AM",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF04131A)
                                )
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.user_profile_name),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.5.sp),
                                color = CubeTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.user_profile_tier),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = CubeCyan
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("sidebar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = CubeTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarSessionItem(
    session: ChatSession,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            CubeCyan.copy(alpha = 0.13f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        label = "session_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            CubeCyan.copy(alpha = 0.55f)
        } else {
            Color.Transparent
        },
        label = "session_border"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)
            .testTag("session_item_${session.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                tint = if (isSelected) CubeCyan else CubeTextMuted,
                modifier = Modifier.size(16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (isSelected) CubeTextPrimary else CubeTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = session.categoryTag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isSelected) CubeCyan else CubeTextMuted
                    )
                    Text(
                        text = "• ${session.updatedAt}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = CubeTextMuted
                    )
                }
            }
        }

        // Rename & Delete Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onRename,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("rename_session_${session.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.action_rename),
                    tint = CubeTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_session_${session.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = CubeCoralRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
