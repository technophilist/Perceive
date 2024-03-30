package com.example.perceive.di

import com.example.perceive.data.local.bitmapstore.AndroidBitmapStore
import com.example.perceive.data.local.bitmapstore.BitmapStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class LocalDataModule {

    @Binds
    abstract fun bindBitmapStore(impl: AndroidBitmapStore): BitmapStore
}