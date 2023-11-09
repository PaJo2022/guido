package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.gson.annotations.SerializedName

data class ItineraryModel(
    @SerializedName("countryName")
    val countryName: String?,
    @SerializedName("tripEndDate")
    val endData: String?,
    @SerializedName("placeName")
    val placeName: String?,
    @SerializedName("placeLatitude")
    val placeLatitude: Double?,
    @SerializedName("placeLongitude")
    val placeLongitude: Double?,
    @SerializedName("tripStartDate")
    val startDate: String?,
    @SerializedName("tripLength")
    var tripLength: String?,
    @SerializedName("tripPartners")
    val tripPartners: String?,
    @SerializedName("placeMapUrl")
    val placeMapUrl: String?,
    @SerializedName("tripData")
    val tripData: List<TripData>?
)