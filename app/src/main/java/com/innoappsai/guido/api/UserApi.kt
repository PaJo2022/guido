package com.innoappsai.guido.api

import com.innoappsai.guido.model.User
import com.innoappsai.guido.model.UserPlacePreferenceRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserApi {

    @GET("user")
    suspend fun getUserById(
        @Query("userId") userId: String
    ): Response<User?>

    @POST("add-user")
    suspend fun registerUser(
        @Body user: User
    ): Response<User?>

    @PUT("update-profile-picture")
    suspend fun setProfilePicture(
        @Query("userId") userId: String,
        @Query("newProfilePicture") newProfilePicture: String
    ): Response<User?>

    @POST("update-user")
    suspend fun updateUserData(
        @Body user: User
    ): Response<User?>

    @POST("update-user-preferences")
    suspend fun updateUserPlacePreference(
        @Query("userId") userId: String,
        @Body userPlacePreferenceRequestDTO: UserPlacePreferenceRequestDTO
    ): Response<User?>
}