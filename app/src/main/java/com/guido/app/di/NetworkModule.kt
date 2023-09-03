package com.guido.app.di


import com.guido.app.api.ChatGptApi
import com.guido.app.api.GuidoApi
import com.guido.app.api.VideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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

        return Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/").addConverterFactory(
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
    fun provideChatGptApi(okHttpClient: OkHttpClient) : ChatGptApi {

        val baseUrl = "https://api.openai.com/v1/chat/"
        val token = "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" // Replace with your actual Bearer token

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().addInterceptor(AuthInterceptor(token)).build())
            .build()

        return retrofit.create(ChatGptApi::class.java)
    }

    class AuthInterceptor(private val token: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(request)
        }
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