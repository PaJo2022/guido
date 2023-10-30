package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.gson.annotations.SerializedName

data class ItineraryModel(
    @SerializedName("Address")
    val address: String?,
    @SerializedName("End Date")
    val endData: String?,
    @SerializedName("Place Name")
    val placeName: String?,
    @SerializedName("Start Date")
    val startDate: String?,
    @SerializedName("Trip Length")
    val tripLength : String?,
    @SerializedName("Trip Partners")
    val tripPartners : String?,
    val tripData: List<TripData>?
)