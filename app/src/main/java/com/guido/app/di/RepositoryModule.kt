package com.guido.app.di

import com.guido.app.api.GuidoApi
import com.guido.app.api.VideoApi
import com.guido.app.data.videos.VideosRepositoryImpl
import com.guido.app.data.places.PlacesRepository
import com.guido.app.data.places.PlacesRepositoryImpl
import com.guido.app.data.videos.VideoRepository
import com.guido.app.db.MyAppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providePlacesRepository(api : GuidoApi,db : MyAppDataBase) : PlacesRepository = PlacesRepositoryImpl(api,db)

    @Provides
    @Singleton
    fun provideVideosRepository(api : VideoApi) : VideoRepository = VideosRepositoryImpl(api)
}