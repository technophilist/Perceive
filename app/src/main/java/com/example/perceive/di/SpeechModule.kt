package com.example.perceive.di

import com.example.perceive.domain.speech.AndroidTranscriptionService
import com.example.perceive.domain.speech.TranscriptionService
import com.example.perceive.domain.speech.tts.AndroidTextToSpeechService
import com.example.perceive.domain.speech.tts.TextToSpeechService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpeechModule {

    @Binds
    @Singleton
    abstract fun bindTranscriptionService(impl: AndroidTranscriptionService): TranscriptionService

    @Binds
    @Singleton
    abstract fun bindTextToSpeechService(impl: AndroidTextToSpeechService): TextToSpeechService
}