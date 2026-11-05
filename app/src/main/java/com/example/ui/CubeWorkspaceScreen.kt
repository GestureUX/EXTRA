package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.FeedbackState
import com.example.ui.components.CubeAssistantMessageCard
import com.example.ui.components.CubeFloatingInputConsole
import com.example.ui.components.CubeSettingsDialog
import com.example.ui.components.CubeSidebar
import com.example.ui.components.CubeThinkingIndicator
import com.example.ui.components.CubeTopHeader
import com.example.ui.components.CubeWebCodeInspectorDialog
import com.example.ui.components.CubeWelcomeEmptyState
import com.example.ui.components.RenameSessionDialog
import com.example.ui.components.UserMessageBubble
import com.example.ui.theme.CubeCyan
import com.example.ui.theme.CubeElectricBlue
import com.example.ui.theme.CubeTextPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CubeViewModel
import kotlinx.coroutines.delay

@Composable
fun CubeWorkspaceScreen(
    viewModel: CubeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-clear transient status banner after 2.6 seconds
    LaunchedEffect(uiState.statusBannerMessage) {
        if (uiState.statusBannerMessage != null) {
            delay(2600)
            viewModel.clearStatusBanner()
        }
    }

    MyApplicationTheme(oledPitchMode = uiState.oledPitchMode) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .drawBehind {
                    // Subtle futuristic radial aura + precision dot matrix grid
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CubeCyan.copy(alpha = 0.06f),
                                CubeElectricBlue.copy(alpha = 0.02f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.18f),
                            radius = size.minDimension * 0.75f
                        )
                    )
                    val step = 32.dp.toPx()
                    var x = step
                    while (x < size.width) {
                        var y = step
                        while (y < size.height) {
                            drawCircle(
                                color = CubeCyan.copy(alpha = 0.045f),
                                radius = 1.dp.toPx(),
                                center = Offset(x, y)
                            )
                            y += step
                        }
                        x += step
                    }
                }
        ) {
            val isWideScreen = maxWidth >= 760.dp

            // Sync default sidebar expansion when crossing form-factor breakpoints
            LaunchedEffect(isWideScreen) {
                viewModel.setSidebarExpanded(isWideScreen)
            }

            // BackHandler for modal dialogs or open mobile drawer
            BackHandler(
                enabled = uiState.showCodeInspectorModal ||
                    uiState.showSettingsModal ||
                    uiState.sessionToRename != null ||
                    (!isWideScreen && uiState.isSidebarExpanded)
            ) {
                when {
                    uiState.showCodeInspectorModal -> viewModel.setShowCodeInspectorModal(false)
                    uiState.showSettingsModal -> viewModel.setShowSettingsModal(false)
                    uiState.sessionToRename != null -> viewModel.dismissRenameDialog()
                    !isWideScreen && uiState.isSidebarExpanded -> viewModel.setSidebarExpanded(false)
                }
            }

            val listState = rememberLazyListState()
            val messageCount = uiState.activeMessages.size
            val lastMessageTextLen = uiState.activeMessages.lastOrNull()?.text?.length ?: 0

            // Auto-scroll smoothly as new messages arrive or stream
            LaunchedEffect(messageCount, lastMessageTextLen, uiState.isWaitingForCube) {
                if (messageCount > 0) {
                    listState.animateScrollToItem((messageCount - 1).coerceAtLeast(0))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Persistent Collapsible Sidebar on Wide Screens (Tablet / Landscape / Desktop)
                if (isWideScreen) {
                    AnimatedVisibility(
                        visible = uiState.isSidebarExpanded,
                        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    ) {
                        CubeSidebar(
                            sessions = uiState.sessions,
                            activeSessionId = uiState.activeSessionId,
                            searchQuery = uiState.searchHistoryQuery,
                            oledPitchMode = uiState.oledPitchMode,
                            onSearchQueryChange = viewModel::updateSearchHistoryQuery,
                            onNewChat = viewModel::startNewChat,
                            onSelectSession = viewModel::selectSession,
                            onRenameSession = viewModel::openRenameDialog,
                            onDeleteSession = viewModel::deleteSession,
                            onToggleThemeMode = viewModel::toggleOledPitchMode,
                            onOpenSettings = { viewModel.setShowSettingsModal(true) },
                            onOpenCodeInspector = { viewModel.setShowCodeInspectorModal(true) },
                            onCollapseSidebar = viewModel::toggleSidebar
                        )
                    }
                }

                // Main Chat Interface Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Header Bar
                        CubeTopHeader(
                            selectedModel = uiState.selectedModel,
                            availableModels = uiState.availableModels,
                            showMenuButton = !isWideScreen || !uiState.isSidebarExpanded,
                            onToggleSidebar = viewModel::toggleSidebar,
                            onSelectModel = viewModel::selectModel,
                            onClearChat = viewModel::clearCurrentChat,
                            onOpenCodeInspector = { viewModel.setShowCodeInspectorModal(true) }
                        )

                        // Scrollable Chat Viewport (Welcome View OR Active Message List)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("chat_messages_viewport"),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 190.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            if (uiState.isWelcomeViewVisible) {
                                item(key = "welcome_view") {
                                    CubeWelcomeEmptyState(
                                        suggestions = uiState.quickSuggestions,
                                        selectedModelName = uiState.selectedModel.name,
                                        onSuggestionClick = { prompt ->
                                            viewModel.sendMessage(overridePrompt = prompt)
                                        }
                                    )
                                }
                            } else {
                                items(
                                    items = uiState.activeMessages,
                                    key = { it.id }
                                ) { message ->
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 780.dp)
                                            .fillMaxWidth()
                                    ) {
                                        if (message.isUser) {
                                            UserMessageBubble(message = message)
                                        } else {
                                            CubeAssistantMessageCard(
                                                message = message,
                                                onCopyText = { viewModel.notifyCopiedToClipboard() },
                                                onThumbsUp = {
                                                    viewModel.toggleMessageFeedback(
                                                        messageId = message.id,
                                                        target = FeedbackState.LIKED
                                                    )
                                                },
                                                onThumbsDown = {
                                                    viewModel.toggleMessageFeedback(
                                                        messageId = message.id,
                                                        target = FeedbackState.DISLIKED
                                                    )
                                                },
                                                onRegenerate = {
                                                    viewModel.regenerateResponse(message.id)
                                                }
                                            )
                                        }
                                    }
                                }

                                if (uiState.isWaitingForCube) {
                                    item(key = "thinking_indicator") {
                                        Box(
                                            modifier = Modifier
                                                .widthIn(max = 780.dp)
                                                .fillMaxWidth()
                                        ) {
                                            CubeThinkingIndicator(
                                                modelName = uiState.selectedModel.name
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Transient Status Toast Pill
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.statusBannerMessage != null,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 74.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFF0E1726).copy(alpha = 0.95f))
                                .border(1.dp, CubeCyan.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = uiState.statusBannerMessage.orEmpty(),
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                color = CubeCyan
                            )
                        }
                    }

                    // Floating Grok-Style Bottom Input Console
                    CubeFloatingInputConsole(
                        inputText = uiState.inputDraft,
                        isWebSearchEnabled = uiState.isWebSearchEnabled,
                        isVoiceRecording = uiState.isVoiceRecording,
                        stagedAttachments = uiState.stagedAttachments,
                        onInputTextChange = viewModel::updateInputDraft,
                        onSendMessage = { viewModel.sendMessage() },
                        onToggleWebSearch = viewModel::toggleWebSearch,
                        onToggleVoiceInput = viewModel::toggleVoiceSimulation,
                        onAddAttachment = viewModel::addMockAttachment,
                        onRemoveAttachment = viewModel::removeAttachment,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            // Collapsible Sidebar Overlay Drawer on Compact / Mobile Screens
            if (!isWideScreen) {
                AnimatedVisibility(
                    visible = uiState.isSidebarExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.65f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.setSidebarExpanded(false) }
                            )
                    )
                }

                AnimatedVisibility(
                    visible = uiState.isSidebarExpanded,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        CubeSidebar(
                            sessions = uiState.sessions,
                            activeSessionId = uiState.activeSessionId,
                            searchQuery = uiState.searchHistoryQuery,
                            oledPitchMode = uiState.oledPitchMode,
                            onSearchQueryChange = viewModel::updateSearchHistoryQuery,
                            onNewChat = {
                                viewModel.startNewChat()
                                viewModel.setSidebarExpanded(false)
                            },
                            onSelectSession = { id ->
                                viewModel.selectSession(id)
                                viewModel.setSidebarExpanded(false)
                            },
                            onRenameSession = viewModel::openRenameDialog,
                            onDeleteSession = viewModel::deleteSession,
                            onToggleThemeMode = viewModel::toggleOledPitchMode,
                            onOpenSettings = { viewModel.setShowSettingsModal(true) },
                            onOpenCodeInspector = { viewModel.setShowCodeInspectorModal(true) },
                            onCollapseSidebar = { viewModel.setSidebarExpanded(false) }
                        )
                    }
                }
            }

            // Modals
            uiState.sessionToRename?.let { session ->
                RenameSessionDialog(
                    session = session,
                    onConfirmRename = { newTitle ->
                        viewModel.renameSession(session.id, newTitle)
                    },
                    onDismiss = viewModel::dismissRenameDialog
                )
            }

            if (uiState.showSettingsModal) {
                CubeSettingsDialog(
                    oledPitchMode = uiState.oledPitchMode,
                    isWebSearchEnabled = uiState.isWebSearchEnabled,
                    onToggleOledMode = viewModel::toggleOledPitchMode,
                    onToggleWebSearch = viewModel::toggleWebSearch,
                    onDismiss = { viewModel.setShowSettingsModal(false) }
                )
            }

            if (uiState.showCodeInspectorModal) {
                CubeWebCodeInspectorDialog(
                    onCopyCode = viewModel::notifyCopiedToClipboard,
                    onDismiss = { viewModel.setShowCodeInspectorModal(false) }
                )
            }
        }
    }
}
