package com.innoappsai.guido.di


import com.innoappsai.guido.api.UserApi
import com.innoappsai.guido.api.ChatGptApi
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.api.VideoApi
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

        return Retrofit.Builder().baseUrl("http://64.227.157.189:7000/").addConverterFactory(
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


    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient) : UserApi {

        val baseUrl = "http://64.227.157.189:7000/"


        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().build())
            .build()

        return retrofit.create(UserApi::class.java)
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
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(logging)

        return client.build()
    }

}