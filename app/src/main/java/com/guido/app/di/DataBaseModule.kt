package com.guido.app.di

import android.content.Context
import androidx.room.Room
import com.guido.app.db.MyAppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Provides
    @Singleton
    fun provideRoomDataBase(@ApplicationContext context: Context) : MyAppDataBase = Room.databaseBuilder(
        context.applicationContext,
        MyAppDataBase::class.java, "database-name"
    ).fallbackToDestructiveMigration().build()



}