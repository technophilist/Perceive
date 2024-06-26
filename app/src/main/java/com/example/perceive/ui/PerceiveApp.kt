package com.example.perceive.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
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
import com.example.perceive.ui.permission.PermissionsDeniedScreen
import com.example.perceive.utils.takePicture

@Composable
fun PerceiveApp(
    navHostController: NavHostController = rememberNavController(),
    shouldShowWelcomeScreen: Boolean = true,
    onNavigateToHomeScreen: () -> Unit = {}
) {
    var isRequiredPermissionsGranted by remember { mutableStateOf<Boolean?>(null) }
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val isCameraPermissionGranted = it[Manifest.permission.CAMERA] ?: false
        val isRecordAudioPermissionGranted = it[Manifest.permission.RECORD_AUDIO] ?: false
        isRequiredPermissionsGranted =
            isCameraPermissionGranted && isRecordAudioPermissionGranted
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    if (isRequiredPermissionsGranted == true) {
        NavHost(
            navController = navHostController,
            startDestination = if (shouldShowWelcomeScreen) PerceiveNavigationDestinations.WelcomeScreen.route
            else PerceiveNavigationDestinations.HomeScreen.route
        ) {
            composable(route = PerceiveNavigationDestinations.WelcomeScreen.route) {
                // no need to use popUpTo because shouldShowWelcomeScreen boolean will take
                // care of it.
                WelcomeScreen(
                    onNavigateToHomeScreenButtonClick = {
                        navHostController.navigate(PerceiveNavigationDestinations.HomeScreen.route)
                        onNavigateToHomeScreen()
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
    } else if (isRequiredPermissionsGranted == false) {
        PermissionsDeniedScreen()
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
                // If the app is already listening and a request to start listening is made,
                // it indicates that the current transcription needs to be stopped.
                // Hence, stop the current transcription.
                if (uiState.isListening) {
                    homeViewModel.stopTranscription()
                    return@HomeScreen
                }
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
        LifecycleEventEffect(
            event = Lifecycle.Event.ON_PAUSE,
            onEvent = viewModel::stopAssistantIfSpeaking
        )
        DisposableEffect(Unit) { onDispose(viewModel::clearCache) }
        ChatScreen(
            isAssistantResponseLoading = uiState.isLoading,
            chatMessages = uiState.messages,
            currentTranscription = currentUserTranscription,
            isListening = uiState.isListening,
            onStartListening = {
                // If the app is already listening and a request to start listening is made,
                // it indicates that the current transcription needs to be stopped.
                // Hence, stop the current transcription.
                if (uiState.isListening) {
                    viewModel.stopTranscription()
                    return@ChatScreen
                }
                viewModel.startTranscription()
            },
            isAssistantMuted = uiState.isAssistantMuted,
            onAssistantMutedChange = viewModel::onAssistantMutedStateChange,
            isAssistantSpeaking = uiState.isAssistantSpeaking,
            onStopAssistantSpeechButtonClick = viewModel::stopAssistantIfSpeaking,
            onBackButtonClick = {
                // This check is needed to ensure that the back button is not clicked
                // while the navigation transition is playing. If this is not checked, the
                // user could possibly pop the back stack twice resulting in an invalid
                // app state. - https://www.youtube.com/watch?v=aV-Yai4zDc0
                val isNavigationBackNotInProgress =
                    navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED
                if (isNavigationBackNotInProgress) navController.popBackStack()
            }
        )
    }
}