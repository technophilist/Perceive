package com.example.perceive.ui.home

import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.example.perceive.ui.components.AnimatedMicButton
import com.example.perceive.ui.components.CameraPreview

@Composable
fun HomeScreen(
    cameraController: LifecycleCameraController,
    transcriptionText: String?,
    homeScreenUiState: HomeScreenUiState,
    onStartListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeScreen(
        modifier = modifier,
        cameraController = cameraController,
        userTextTranscription = transcriptionText,
        isListening = homeScreenUiState.isListening,
        onStartListening = onStartListening,
    )

}

@Composable
fun HomeScreen(
    cameraController: LifecycleCameraController,
    userTextTranscription: String?,
    isListening: Boolean,
    onStartListening: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textGradientBrush = remember {
        Brush.horizontalGradient(listOf(Color(0xff4285f4), Color.White))
    }

    Box(modifier = modifier) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            controller = cameraController
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            AnimatedVisibility(userTextTranscription != null, enter = fadeIn()) {
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
                        text = userTextTranscription ?: "Hi there! Tap to get started",
                        style = MaterialTheme.typography.titleLarge.copy(brush = textGradientBrush)
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
}
