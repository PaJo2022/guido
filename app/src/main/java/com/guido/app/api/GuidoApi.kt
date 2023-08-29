package com.guido.app.api

import com.guido.app.model.places.PlacesApiDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GuidoApi {

    @GET("json")
    suspend fun fetchPlacesNearMe(
        @Query("location") location : String,
        @Query("radius") radius : Int,
        @Query("keyword") keyword : String,
        @Query("key") key : String
    ) : Response<PlacesApiDTO>
}