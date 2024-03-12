package com.example.perceive.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.perceive.ui.chat.ChatScreen
import com.example.perceive.ui.chat.ChatViewModel
import com.example.perceive.ui.home.HomeScreen
import com.example.perceive.ui.home.HomeViewModel
import com.example.perceive.ui.navigation.PerceiveNavigationDestinations
import com.example.perceive.ui.onboarding.WelcomeScreen
import com.example.perceive.utils.createRecognitionListener
import com.example.perceive.utils.takePicture

@Composable
fun PerceiveApp(navHostController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navHostController,
        startDestination = PerceiveNavigationDestinations.HomeScreen.route
    ) {
        composable(route = PerceiveNavigationDestinations.WelcomeScreen.route) {
            // TODO: make welcome screen visible only once
            WelcomeScreen(
                onNavigateToHomeScreenButtonClick = {
                    navHostController.navigate(PerceiveNavigationDestinations.HomeScreen.route)
                }
            )
        }
        homeScreen(
            route = PerceiveNavigationDestinations.HomeScreen.route,
            navController = navHostController
        )
        chatScreen(
            route = PerceiveNavigationDestinations.ChatScreen.route,
            navController = navHostController
        )
    }
}

private fun NavGraphBuilder.homeScreen(navController: NavController, route: String) {
    composable(route = route) {
        val context = LocalContext.current
        val homeViewModel = hiltViewModel<HomeViewModel>()
        val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
        val cameraController = remember { LifecycleCameraController(context) }
        val transcription by homeViewModel.userSpeechTranscriptionStream.collectAsStateWithLifecycle()
        val onEndOfSpeech = remember {
            { transcription: String, associatedBitmapUri: Uri ->
                navController.navigate(
                    PerceiveNavigationDestinations
                        .ChatScreen
                        .buildRoute(transcription, associatedBitmapUri)
                )
            }
        }
        HomeScreen(
            cameraController = cameraController,
            transcriptionText = transcription,
            homeScreenUiState = uiState,
            onStartListening = {
                cameraController.takePicture(
                    context = context,
                    onSuccess = {
                        homeViewModel.startTranscription(
                            currentCameraImageProxy = it,
                            onEndOfSpeech = onEndOfSpeech
                        )
                    },
                    onError = { /*TODO*/ }
                )
            }
        )
    }
}

private fun NavGraphBuilder.chatScreen(route: String, navController: NavController) {
    composable(route = route) {
        val viewModel = hiltViewModel<ChatViewModel>()
        val currentUserTranscription by viewModel.userSpeechTranscriptionStream.collectAsStateWithLifecycle()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ChatScreen(
            isAssistantResponseLoading = uiState.isLoading,
            chatMessages = uiState.messages,
            currentTranscription = currentUserTranscription,
            isListening = uiState.isListening,
            onStartListening = viewModel::startTranscription,
            isAssistantMuted = uiState.isAssistantMuted,
            onAssistantMutedChange = viewModel::onAssistantMutedStateChange,
            onBackButtonClick = navController::popBackStack // todo(fix this):this not recommended way to navigate back
        )
    }
}