package com.example.perceive.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * This composable is an extension of the [AnimatedMicButton] that also displays the
 * [userTextTranscription] above the button.
 */
@Composable
fun AnimatedMicButtonWithTranscript(
    userTextTranscription: String?,
    isListening: Boolean,
    onStartListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        AnimatedVisibility(visible = userTextTranscription != null, enter = fadeIn()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateContentSize(),
                    text = userTextTranscription ?: "",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        AnimatedMicButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            isAnimationRunning = isListening,
            onClick = onStartListening
        )
    }
}