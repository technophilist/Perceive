package com.example.perceive.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
//  todo: fix AnimatedMicButton anim after 1full rotation
@Composable
fun AnimatedMicButton(
    isAnimationRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentRotationDegrees by remember { mutableFloatStateOf(360f) }
    val animatedCurrentRotationDegrees by animateFloatAsState(
        targetValue = currentRotationDegrees,
        label = ""
    )
    val localHapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(isAnimationRunning) {
        if (!isAnimationRunning) return@LaunchedEffect
        while (true) {
            ensureActive()
            delay(20)
            currentRotationDegrees =
                (currentRotationDegrees - 1).takeIf { it >= 0 } ?: 360f // todo: fix animation
        }
    }
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