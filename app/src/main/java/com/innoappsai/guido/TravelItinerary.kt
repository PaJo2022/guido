package com.innoappsai.guido

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel

@Entity(tableName = "TRAVEL_ITINERARY_TABLE")
data class TravelItinerary(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val travelItineraryData : ItineraryModel
)
