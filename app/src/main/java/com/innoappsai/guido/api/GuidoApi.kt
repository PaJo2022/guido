package com.innoappsai.guido.api

import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.places.geoCoding.ReverseGeoCodingDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.model.review.ReviewRequestDTO
import com.innoappsai.guido.model.review.ReviewResponseDTO
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface GuidoApi {


    @POST("near-by-places")
    suspend fun fetchPlacesNearMe(
        @Query("latitude", encoded = true) latitude: Double,
        @Query("longitude", encoded = true) longitude: Double,
        @Query("distance") radius: Int,
        @Body types: List<String>
    ): Response<List<PlaceDTO>>


    @POST("add-place")
    suspend fun addPlace(
        @Body placeRequestDTO: PlaceRequestDTO
    ): Response<PlaceDTO?>


    @GET("place")
    suspend fun fetchPlacesDetails(
        @Query("placeId", encoded = true) placeId: String
    ): Response<PlaceDTO>


    @GET("reverse-geo-coding")
    suspend fun fetchAddressFromLatLng(
        @Query("latitude", encoded = true) latitude : Double,
        @Query("longitude", encoded = true) longitude : Double,
    ) : Response<ReverseGeoCodingDTO?>

    @GET("auto-complete")
    suspend fun fetchPlaceAutoCompleteSuggestion(
        @Query("query", encoded = true) query : String
    ) : Response<List<PlaceAutoCompleteDTO>>

    @GET("places-by-user-id")
    suspend fun fetchPlacesByUserId(
        @Query("userId", encoded = true) userId : String
    ): Response<List<PlaceDTO>>

    @DELETE("delete-place-by-id")
    suspend fun deletePlaceById(
        @Query("userId", encoded = true) userId : String,
        @Query("placeId", encoded = true) placeId: String
    ): Response<PlaceDTO>

    @PUT("update-static-map-url-by-place-id")
    suspend fun updatePlaceStaticMapUrlPlaceById(
        @Query("placeId", encoded = true) placeId: String,
        @Query("newStaticMapUrl") staticMapUrl: String,
    ): Response<PlaceDTO>


    // Reviews

    @POST("add-review")
    suspend fun addReview(
        @Body reviewRequestDTO: ReviewRequestDTO
    ): Response<PlaceDTO>

    @GET("reviews-by-place-id")
    suspend fun fetchReviewByPlaceId(
        @Query("placeId", encoded = true) placeId: String
    ): Response<List<ReviewResponseDTO>>


    @POST("ask-ai")
    suspend fun askAI(
        @Query("userId") userId: String?,
        @Query("shouldSendEmail") shouldSendEmail: Boolean,
        @Body chatGptRequest: ChatGptRequest,
    ): Response<String?>

    @POST("generate-travel-itinerary")
    suspend fun generateTravelItinerary(
        @Query("userId") userId: String,
        @Body body : RequestBody,
    ): Response<ItineraryModel?>

    @GET("user-generated-travel-itinerary")
    suspend fun getAllGenerateTravelItineraryListByUserId(
        @Query("userId") userId: String
    ): Response<List<TravelItinerary>>

    @GET("travel-itinerary-details")
    suspend fun getItineraryDetails(
        @Query("itineraryId") itineraryid: String
    ): Response<TravelItinerary?>
}