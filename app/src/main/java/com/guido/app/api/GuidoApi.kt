package com.guido.app.api

import com.guido.app.model.places.PlacesApiDTO
import com.guido.app.model.places.geoCoding.ReverseGeoCodingResponse
import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface GuidoApi {

    @GET("place/nearbysearch/json")
    suspend fun fetchPlacesNearMe(
        @Query("location", encoded = true) location : String,
        @Query("radius") radius : Int,
        @Query("types", encoded = true) keyword : String,
        @Query("key") key : String
    ) : Response<PlacesApiDTO>

    @GET("geocode/json")
    suspend fun fetchAddressFromLatLng(
        @Query("latlng") latlng : String,
        @Query("key") key : String
    ) : Response<ReverseGeoCodingResponse>
}