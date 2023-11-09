package com.innoappsai.guido.di


import android.content.Context
import android.content.Intent
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.api.AuthenticatorApi
import com.innoappsai.guido.api.ChatGptApi
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.api.UserApi
import com.innoappsai.guido.api.VideoApi
import com.innoappsai.guido.auth.AuthActivity
import com.innoappsai.guido.data.TokenAuthenticator
import com.innoappsai.guido.db.AppPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    companion object {
        private const val BASE_URL = "http://64.227.157.189:7000/"
        //private const val BASE_URL = "https://api.guidoai.com/"
    }

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideGuidoApi(okHttpClient: OkHttpClient): GuidoApi {
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
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
            .client(okHttpClient.newBuilder().addInterceptor(ChatGptAuthInterceptor(token)).build())
            .build()

        return retrofit.create(ChatGptApi::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient) : UserApi {


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().build())
            .build()

        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticatorApi(): AuthenticatorApi {


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AuthenticatorApi::class.java)
    }

    class ChatGptAuthInterceptor(private val token: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(request)
        }
    }

    class AuthInterceptor(private val context : Context,private val token: String?, private val fcmKey: String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return try {
                val request = chain.request().newBuilder()
                    .addHeader("authorization", token.toString())
                    .addHeader("fcmKey", fcmKey.toString())
                    .build()
                chain.proceed(request)
            }catch (e : Exception){
                val intent = Intent(context, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                chain.proceed(chain.request())
            }
        }
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context : Context, authenticatorApi: AuthenticatorApi, appPrefs: AppPrefs): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(logging)
        client.addInterceptor(AuthInterceptor(context,appPrefs.authToken, appPrefs.fcmKey))
        client.authenticator(
            TokenAuthenticator(
                api = authenticatorApi,
                appPrefs = appPrefs
            )
        )

        return client.build()
    }

}