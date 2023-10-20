package com.innoappsai.guido.data

import android.util.Log
import com.innoappsai.guido.api.AuthenticatorApi
import com.innoappsai.guido.db.AppPrefs
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val api: AuthenticatorApi, private val appPrefs: AppPrefs) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val newAccessToken = refreshToken()


            // If the refresh is successful, return a new request with the updated token
            return response.request.newBuilder()
                .header("authorization", "$newAccessToken")
                .build()

        }
        return null

    }

    private fun refreshToken(): String? {
        val userId = appPrefs.userId
        val token = api.refreshToken(userId).execute().headers()["authorization"]
        appPrefs.authToken = token
        return token
    }
}
