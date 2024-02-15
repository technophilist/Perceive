package com.example.perceive.ui.home

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.perceive.ui.components.AnimatedMicButtonWithTranscript
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
    Box(modifier = modifier) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            controller = cameraController
        )
        AnimatedMicButtonWithTranscript(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            userTextTranscription = userTextTranscription,
            isListening = isListening,
            onStartListening = onStartListening
        )
    }
}
