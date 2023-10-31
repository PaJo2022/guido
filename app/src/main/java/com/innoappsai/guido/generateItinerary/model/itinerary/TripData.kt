package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.gson.annotations.SerializedName

data class TripData(
    @SerializedName("day")
    val day: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("landMarks")
    val travelPlaces: List<TravelPlace>?,
    var isSelected : Boolean = false
)