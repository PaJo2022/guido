package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.gson.annotations.SerializedName

data class TripData(
    @SerializedName("Day")
    val day: String?,
    val isSelected : Boolean = false,
    @SerializedName("Places")
    val travelPlaces: List<TravelPlace>?
)