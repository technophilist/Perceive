package com.example.perceive.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.perceive.R
import com.example.perceive.ui.onboarding.WelcomeScreen
import com.example.perceive.ui.theme.PerceiveTheme
import com.example.perceive.ui.theme.RoundedStarShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/**
 * Creates an animated microphone button that has a rotating "curvy circle" background.
 *
 * @param isAnimationRunning boolean that determines whether the microphone animation is active.
 * @param onClick callback to invoked when the button is clicked.
 * @param modifier [Modifier] to be applied to the composable.
 */
@Composable
fun AnimatedMicButton(
    isAnimationRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedCurrentRotationDegrees by infiniteTransition.animateFloat(
        label = "",
        initialValue = 0f,
        targetValue = if (isAnimationRunning) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5_000, easing = LinearEasing)
        ),
    )
    val localHapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    Button(
        modifier = modifier,
        onClick = {
            scope.launch {
                // custom haptic feedback
                repeat(2) {
                    localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(100)
                }
            }
            onClick()
        },
        shape = RoundedStarShape(rotation = animatedCurrentRotationDegrees),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            modifier = Modifier
                .padding(32.dp)
                .size(32.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.outline_mic_24),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null,
        )
    }
}

@PreviewLightDark
@PreviewDynamicColors
@Composable
private fun AnimatedMicButtonPreview() {
    PerceiveTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = { AnimatedMicButton(isAnimationRunning = false, onClick = {}) }
        )
    }
}