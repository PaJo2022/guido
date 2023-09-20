package com.guido.app.api

import com.guido.app.model.places.PlacesApiDTO
import com.guido.app.model.places.geoCoding.ReverseGeoCodingResponse
import com.guido.app.model.singlePlaceDetails.SinglePlaceDetailsDTO
import retrofit2.Response
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


    @GET("place/details/json")
    suspend fun fetchPlacesDetails(
        @Query("place_id", encoded = true) placeId : String,
        @Query("key") key : String
    ) : Response<SinglePlaceDetailsDTO>


    @GET("geocode/json")
    suspend fun fetchAddressFromLatLng(
        @Query("latlng") latlng : String,
        @Query("key") key : String
    ) : Response<ReverseGeoCodingResponse>
}