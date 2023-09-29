package com.innoappsai.guido.api

import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.places.geoCoding.ReverseGeoCodingDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GuidoApi {


    @POST("nearByPlaces")
    suspend fun fetchPlacesNearMe(
        @Query("latitude", encoded = true) latitude : Double,
        @Query("longitude", encoded = true) longitude : Double,
        @Query("distance") radius : Int,
        @Body types : List<String>
    ) : Response<List<PlaceDTO>>


    @GET("place")
    suspend fun fetchPlacesDetails(
        @Query("placeId", encoded = true) placeId : String
    ) : Response<PlaceDTO>


    @GET("reverse-geo-coding")
    suspend fun fetchAddressFromLatLng(
        @Query("latitude", encoded = true) latitude : Double,
        @Query("longitude", encoded = true) longitude : Double,
    ) : Response<ReverseGeoCodingDTO?>

    @GET("auto-complete")
    suspend fun fetchPlaceAutoCompleteSuggestion(
        @Query("query", encoded = true) query : String
    ) : Response<List<PlaceAutoCompleteDTO>>
}