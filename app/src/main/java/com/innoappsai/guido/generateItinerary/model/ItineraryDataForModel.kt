package com.innoappsai.guido.generateItinerary.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ITINERARY_DATA_FOR_MODEL")
data class ItineraryDataForModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itineraryId: String
)