package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.gson.annotations.SerializedName

data class ItineraryModel(
    @SerializedName("countryName")
    val countryName: String?,
    @SerializedName("tripEndDate")
    val endData: String?,
    @SerializedName("placeName")
    val placeName: String?,
    @SerializedName("tripStartDate")
    val startDate: String?,
    @SerializedName("tripLength")
    val tripLength : String?,
    @SerializedName("tripPartners")
    val tripPartners : String?,
    val tripData: List<TripData>?
)