package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class TravelPlace(
    @SerializedName("landMarkId")
    val placeId: String,
    @SerializedName("landMarkName")
    val placeName: String,
    @SerializedName("landMarkLatitude")
    val latitude: Double,
    @SerializedName("landMarkLongitude")
    val longitude: Double,
    @SerializedName("landMarkImage")
    val placePhoto: String?,
    @SerializedName("travelTiming")
    val timeSlots: String?,
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