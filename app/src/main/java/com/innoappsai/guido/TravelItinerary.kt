package com.innoappsai.guido

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel

@Entity(tableName = "TRAVEL_ITINERARY_TABLE")
data class TravelItinerary(
    @SerializedName("_id")
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val userId: String?,
    val itineraryModel : ItineraryModel?
)
