package com.example.perceive.di

import com.example.perceive.domain.speech.AndroidTranscriptionService
import com.example.perceive.domain.speech.TranscriptionService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranscriptionModule {

    @Binds
    @Singleton
    abstract fun bindTranscriptionService(impl: AndroidTranscriptionService): TranscriptionService
}