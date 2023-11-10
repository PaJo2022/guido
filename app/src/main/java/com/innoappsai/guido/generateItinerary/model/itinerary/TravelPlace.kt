package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel

data class TravelPlace(
    @SerializedName("travelTiming")
    val travelTiming: String?,
    @SerializedName("travelDateAndTiming")
    val travelDateAndTiming: String?,
    @SerializedName("landMarkId")
    val placeId: String?,
    @SerializedName("landMarkName")
    val placeName: String?,
    @SerializedName("landMarkDescription")
    val landMarkDescription: String?,
    @SerializedName("details")
    val placeDetails: PlaceUiModel?,
)

data class TravelDirection(
    var currentLocation: LatLng? = null,
    var nextLocation: LatLng? = null,
    var currentLocationName : String?=null,
    var nextLocationName : String?=null
)



data class TravelPlaceWithTravelDirection(
    val travelPlace: TravelPlace?=null,
    val travelDirection: TravelDirection?=null
)