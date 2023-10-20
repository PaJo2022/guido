package com.innoappsai.guido.api

import retrofit2.http.GET
import retrofit2.http.Query

interface AuthenticatorApi {
    @GET("refresh-token")
    fun refreshToken(
        @Query("userId") userId: String?
    ): retrofit2.Call<Unit?>
}