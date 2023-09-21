package com.guido.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.guido.app.api.ChatGptApi
import com.guido.app.api.GuidoApi
import com.guido.app.api.VideoApi
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.auth.AuthRepositoryImpl
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.auth.repo.user.UserRepositoryImpl
import com.guido.app.data.places.PlacesRepository
import com.guido.app.data.places.PlacesRepositoryImpl
import com.guido.app.data.tourData.TourDataRepository
import com.guido.app.data.tourData.TourDataRepositoryImpl
import com.guido.app.data.videos.VideoRepository
import com.guido.app.data.videos.VideosRepositoryImpl
import com.guido.app.db.AppPrefs
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
    fun providePlacesRepository(api: GuidoApi, db: MyAppDataBase): PlacesRepository =
        PlacesRepositoryImpl(api, db)

    @Provides
    @Singleton
    fun provideVideosRepository(api: VideoApi): VideoRepository = VideosRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideTourDataRepository(api: ChatGptApi): TourDataRepository = TourDataRepositoryImpl(api)


    @Provides
    @Singleton
    fun provideAuthRepository(
        fireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        appPrefs: AppPrefs,
        db: MyAppDataBase
    ): AuthRepository = AuthRepositoryImpl(
        fireStoreCollection = fireStore, firebaseAuth = firebaseAuth,appPrefs = appPrefs,db=db
    )


    @Provides
    @Singleton
    fun provideUserRepository(appPrefs: AppPrefs, fireStore: FirebaseFirestore,db: MyAppDataBase): UserRepository =
        UserRepositoryImpl(appPrefs = appPrefs, fireStoreCollection = fireStore,db = db)

}