package com.guido.app.di


import com.guido.app.api.GuidoApi
import com.guido.app.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideGuidoApi(okHttpClient: OkHttpClient) : GuidoApi {

        return Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/").addConverterFactory(
            GsonConverterFactory.create()
        ).client(okHttpClient).build().create(GuidoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVideoApi(okHttpClient: OkHttpClient) : VideoApi {

        return Retrofit.Builder().baseUrl("https://www.googleapis.com/").addConverterFactory(
            GsonConverterFactory.create()
        ).client(okHttpClient).build().create(VideoApi::class.java)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(logging)

        return client.build()
    }

}