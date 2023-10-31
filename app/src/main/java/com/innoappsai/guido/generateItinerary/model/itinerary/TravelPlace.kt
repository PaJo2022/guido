package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.gson.annotations.SerializedName

data class TravelPlace(
    val placeId: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("placeImages")
    val placePhotos: List<String>,
    @SerializedName("travelTiming")
    val timeSlots: String?
)