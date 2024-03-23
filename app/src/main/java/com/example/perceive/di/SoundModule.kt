package com.example.perceive.di

import com.example.perceive.domain.sound.AndroidUISoundPlayer
import com.example.perceive.domain.sound.UISoundPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SoundModule {

    @Binds
    @Singleton
    abstract fun bindUISoundPlayer(impl: AndroidUISoundPlayer): UISoundPlayer
}