package com.guido.app.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.guido.app.DefaultLocationClient
import com.guido.app.LocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePlacesApi(@ApplicationContext context: Context) = Places.createClient(context)


    @Provides
    @Singleton
    fun provideFuseLocationProvide(@ApplicationContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideLocationClient(@ApplicationContext context: Context) : LocationClient = DefaultLocationClient(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
}