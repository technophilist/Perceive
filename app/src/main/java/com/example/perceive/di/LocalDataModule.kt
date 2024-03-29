package com.example.perceive.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.perceive.data.local.bitmapstore.AndroidBitmapStore
import com.example.perceive.data.local.bitmapstore.BitmapStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
abstract class LocalDataModule {

    @Binds
    abstract fun bindBitmapStore(impl: AndroidBitmapStore): BitmapStore

    companion object {
        @Provides
        @Singleton
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile(PERCEIVE_USER_PREFERENCES_STORE_NAME)
            }
        }
    }
}

private const val PERCEIVE_USER_PREFERENCES_STORE_NAME = "perceive_user_preferences"