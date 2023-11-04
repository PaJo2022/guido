package com.innoappsai.guido.generateItinerary.model.itinerary

import com.google.android.libraries.places.api.model.Place
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO

data class TripData(
    @SerializedName("day")
    val day: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("landMarks")
    val travelPlaces: List<Landmark>?,
    var isSelected : Boolean = false
)

data class Landmark(
    val travelTiming : String?,
    val landMarkId : String?,
    val landMarkName : String?,
    val landMarkDescription : String?,
    val details:PlaceDTO?
)