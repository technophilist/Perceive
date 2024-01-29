package com.example.perceive.ui

import android.content.Intent
import android.graphics.Bitmap
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.perceive.ui.home.HomeScreen
import com.example.perceive.ui.home.HomeViewModel
import com.example.perceive.utils.createRecognitionListener
import com.example.perceive.utils.takePicture

@Composable
fun PerceiveApp() {
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val cameraController = remember { LifecycleCameraController(context) }
    val transcription by homeViewModel.userSpeechTranscriptionStream.collectAsStateWithLifecycle()
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }
    // An image of what the user is currently pointing the camera at.
    var currentCameraImage by remember { mutableStateOf<Bitmap?>(null) }
    val listener = remember {
        createRecognitionListener(
            onReadyForSpeech = {
                homeViewModel.onStartListening(
                    currentCameraImage ?: return@createRecognitionListener
                )
            },
            onEndOfSpeech = {
                speechRecognizer.stopListening()
                speechRecognizer.destroy()
                homeViewModel.onStopListening()
            },
            onError = { homeViewModel.onErrorOccurred() },
            onPartialResults = { partialResultsBundle ->
                val transcript = partialResultsBundle
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.first() ?: return@createRecognitionListener
                homeViewModel.onUpdateTranscription(transcript)
            }
        )
    }

    HomeScreen(
        cameraController = cameraController,
        transcriptionText = transcription,
        homeScreenUiState = uiState,
        onStartListening = {
            cameraController.takePicture(
                context,
                onSuccess = {
                    currentCameraImage = it.toBitmap()
                    speechRecognizer.setRecognitionListener(listener)
                    speechRecognizer.startListening(speechRecognizerIntent)
                },
                onError = { homeViewModel.onErrorOccurred() }
            )
        }
    )

}