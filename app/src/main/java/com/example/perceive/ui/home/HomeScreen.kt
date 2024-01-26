package com.example.perceive.ui.home

import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.perceive.R
import com.example.perceive.ui.components.AssistantResponseCard
import com.example.perceive.ui.components.CameraPreview

@Composable
fun HomeScreen(
    cameraController: LifecycleCameraController,
    userTextTranscription: String?,
    assistantResponse: String?,
    isListening: Boolean,
    onStartListening: () -> Unit,
    isLoadingResponse: Boolean,
    modifier: Modifier = Modifier,
) {
    val textGradientBrush = remember {
        Brush.horizontalGradient(
            listOf(Color(0xff4285f4), Color.White)
        )
    }
    val waveformAnimationComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_listening_anim)
    )
    val isMicIconVisible by remember(isListening, isLoadingResponse) {
        derivedStateOf { !isListening && !isLoadingResponse }
    }
    Box(modifier = modifier) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            controller = cameraController
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .clickable(isMicIconVisible) { onStartListening() }
                .background(Color(0xff121212))
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = assistantResponse != null) {
                AssistantResponseCard(
                    modifier = Modifier.fillMaxWidth(),
                    responseText = "Hello World ".repeat(20)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = userTextTranscription ?: "Hi there! Tap to get started",
                style = MaterialTheme.typography.titleLarge.copy(brush = textGradientBrush)
            )
            Spacer(modifier = Modifier.size(16.dp))
            if (isListening) {
                LottieAnimation(
                    composition = waveformAnimationComposition,
                    reverseOnRepeat = true,
                    iterations = LottieConstants.IterateForever
                )
            }
            if (isMicIconVisible) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_mic_24),
                    tint = Color(0xff4285f4),
                    contentDescription = null,
                )
            }
        }
        if (isLoadingResponse) {
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .systemBarsPadding(),
                trackColor = Color(0xff4285f4),
                strokeCap = StrokeCap.Butt
            )
        }
    }
}