package com.example.perceive.ui.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.perceive.domain.chat.ChatMessage
import com.example.perceive.ui.components.AnimatedMicButton
import com.example.perceive.ui.components.AnimatedMicButtonWithTranscript
import com.example.perceive.ui.components.ChatMessageCard
import com.example.perceive.ui.components.Role
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    isAssistantResponseLoading: Boolean,
    chatMessages: List<ChatMessage>,
    currentTranscription: String?,
    isListening: Boolean,
    onStartListening: () -> Unit,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Gemini",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackButtonClick,
                    content = {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                )
            },
            actions = {}
        )
        ChatMessagesList(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            chatMessages = chatMessages
        )
        Divider()
        AnimatedContent(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            targetState = isAssistantResponseLoading,
            label = ""
        ) { isResponseLoading ->
            if (isResponseLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(64.dp)
                )
            } else {
                AnimatedMicButtonWithTranscript(
                    userTextTranscription = currentTranscription,
                    isListening = isListening,
                    onStartListening = onStartListening
                )
            }
        }
    }
}

@Composable
private fun ChatMessagesList(chatMessages: List<ChatMessage>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(items = chatMessages, key = { it.id }) { chatMessage ->
            Box(modifier = Modifier.fillMaxWidth()) {
                val alignment = remember {
                    if (chatMessage.role == ChatMessage.Role.USER) Alignment.CenterEnd
                    else Alignment.CenterStart
                }
                ChatMessageCard(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(alignment = alignment)
                        .fillMaxWidth(0.7f),
                    messageContent = chatMessage.message,
                    role = if (chatMessage.role == ChatMessage.Role.USER) Role.USER
                    else Role.RESPONDER
                )
            }
        }
    }
}
