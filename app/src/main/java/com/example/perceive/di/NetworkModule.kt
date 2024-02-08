package com.example.perceive.di

import com.example.perceive.data.remote.languagemodel.GeminiLanguageModelClient
import com.example.perceive.data.remote.languagemodel.MultiModalLanguageModelClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindMultiModalLanguageModelClient(impl: GeminiLanguageModelClient): MultiModalLanguageModelClient
}