package com.innoappsai.guido.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.innoappsai.guido.api.ChatGptApi
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.api.VideoApi
import com.innoappsai.guido.auth.repo.auth.AuthRepository
import com.innoappsai.guido.auth.repo.auth.AuthRepositoryImpl
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.auth.repo.user.UserRepositoryImpl
import com.innoappsai.guido.data.file.FileRepository
import com.innoappsai.guido.data.file.FileRepositoryImpl
import com.innoappsai.guido.data.places.BackendPlacesRepositoryImpl
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.data.tourData.TourDataRepository
import com.innoappsai.guido.data.tourData.TourDataRepositoryImpl
import com.innoappsai.guido.data.videos.VideoRepository
import com.innoappsai.guido.data.videos.VideosRepositoryImpl
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
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
        BackendPlacesRepositoryImpl(api, db)

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

    @Provides
    @Singleton
    fun provideFileRepositoryImpl(firebaseStorage: FirebaseStorage): FileRepository =
        FileRepositoryImpl(firebaseStorage)



}