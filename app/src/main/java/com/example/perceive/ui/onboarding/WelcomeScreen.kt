package com.example.perceive.ui.onboarding

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.perceive.R
import kotlinx.coroutines.delay

// TODO blur doesn't work on old devices
// TODO try to make the gif repeat in a "forward-backward-forward" fashion.
@Composable
fun WelcomeScreen(onNavigateToHomeScreenButtonClick: () -> Unit) {
    val localHapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    // background gif
    val bfGifImageLoader = remember {
        ImageLoader.Builder(context = context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .build()
    }
    val bgGifImageRequest = remember {
        ImageRequest.Builder(context = context)
            .data(R.drawable.welcome_screen_bg)
            .build()
    }
    val asyncImagePainter = rememberAsyncImagePainter(
        model = bgGifImageRequest,
        imageLoader = bfGifImageLoader
    )
    // welcome text blur
    // need to keep value as dp in order for it to be compatible with default saver
    var welcomeTextBlurRadiusInInt by rememberSaveable { mutableStateOf(64) }
    val animatedWelcomeTextBlurRadius by animateDpAsState(
        targetValue = welcomeTextBlurRadiusInInt.dp,
        label = "Welcome Text Blur Radius",
        animationSpec = tween(800)
    )
    val textGradientBrush = remember {
        Brush.horizontalGradient(listOf(Color(0xff4285f4), Color.White))
    }
    // navigate to home screen button animation
    var isNavigateToHomeScreenButtonVisible by rememberSaveable { mutableStateOf(false) }

    // animation related one-time coroutines
    LaunchedEffect(Unit) {
        delay(400)
        isNavigateToHomeScreenButtonVisible = true
        localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    LaunchedEffect(Unit) {
        delay(100)
        welcomeTextBlurRadiusInInt = 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // background gif
        Image(
            modifier = Modifier
                .fillMaxSize()
                .rotate(180f)
                .blur(64.dp),
            painter = asyncImagePainter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        // scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .blur(
                    radius = animatedWelcomeTextBlurRadius,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                ),
            text = "Welcome to Perceive",
            style = MaterialTheme.typography.displaySmall,
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isNavigateToHomeScreenButtonVisible,
                enter = slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow,
                    ),
                    initialOffsetY = { it }
                )
            ) {
                Button(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp),
                    onClick = onNavigateToHomeScreenButtonClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Let's Go!",
                        style = MaterialTheme.typography.titleLarge.copy(brush = textGradientBrush)
                    )
                }
            }
        }
    }

}