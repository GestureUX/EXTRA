package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CubeWebSourceRepository
import com.example.model.ChatSession
import com.example.ui.theme.CubeCyan
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeTextMuted
import com.example.ui.theme.CubeTextPrimary
import com.example.ui.theme.CubeTextSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun RenameSessionDialog(
    session: ChatSession,
    onConfirmRename: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var draftTitle by remember(session.id) { mutableStateOf(session.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = CubeTextPrimary,
        textContentColor = CubeTextSecondary,
        title = {
            Text(
                text = "Rename Conversation",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Update the session label in your Cube sidebar history:",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = draftTitle,
                    onValueChange = { draftTitle = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CubeCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = CubeTextPrimary,
                        unfocusedTextColor = CubeTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_dialog_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRename(draftTitle) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CubeCyan,
                    contentColor = Color(0xFF03171C)
                ),
                modifier = Modifier.testTag("rename_dialog_confirm")
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CubeTextSecondary)
            }
        }
    )
}

@Composable
fun CubeSettingsDialog(
    oledPitchMode: Boolean,
    isWebSearchEnabled: Boolean,
    onToggleOledMode: () -> Unit,
    onToggleWebSearch: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = CubeTextPrimary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CubeIsometricIcon(size = 28.dp)
                Text(
                    text = "Cube Workspace Settings",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OLED Pitch Matrix (#030508)",
                            style = MaterialTheme.typography.titleMedium,
                            color = CubeTextPrimary
                        )
                        Text(
                            text = "Switch between #0B0F17 Stealth Slate and ultra-deep OLED black.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = CubeTextSecondary
                        )
                    }
                    Switch(
                        checked = oledPitchMode,
                        onCheckedChange = { onToggleOledMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF03171C),
                            checkedTrackColor = CubeCyan
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Real-Time Web Search Grounding",
                            style = MaterialTheme.typography.titleMedium,
                            color = CubeTextPrimary
                        )
                        Text(
                            text = "Include live citation telemetry in simulated Cube responses.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = CubeTextSecondary
                        )
                    }
                    Switch(
                        checked = isWebSearchEnabled,
                        onCheckedChange = { onToggleWebSearch() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF03171C),
                            checkedTrackColor = CubeCyan
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CubeCyan,
                    contentColor = Color(0xFF03171C)
                )
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun CubeWebCodeInspectorDialog(
    onCopyCode: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val fullSource = CubeWebSourceRepository.fullSingleFileHtmlCode

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 860.dp)
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.86f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, CubeCyan.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
                .testTag("web_code_inspector_modal"),
            color = Color(0xFF080C14)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Modal Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1624))
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = CubeCyan
                        )
                        Column {
                            Text(
                                text = "Cube UI — Full Single-File HTML / Tailwind CSS / JS",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CubeTextPrimary
                            )
                            Text(
                                text = "Ready-to-run responsive web mockup file (index.html)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                color = CubeTextMuted
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(fullSource))
                                copied = true
                                onCopyCode()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (copied) CubeEmerald else CubeCyan,
                                contentColor = Color(0xFF03171C)
                            ),
                            modifier = Modifier.testTag("copy_full_html_button")
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = if (copied) "Copied HTML" else "Copy Full Code",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close code inspector",
                                tint = CubeTextSecondary
                            )
                        }
                    }
                }

                // Scrollable Code Viewer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = fullSource,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}
