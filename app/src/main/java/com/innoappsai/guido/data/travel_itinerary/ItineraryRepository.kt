package com.innoappsai.guido.data.travel_itinerary

import com.innoappsai.guido.TravelItinerary
import kotlinx.coroutines.flow.Flow

interface ItineraryRepository {
    suspend fun addItinerary(itinerary: TravelItinerary)
    fun getItineraryById(id: String): Flow<TravelItinerary?>

    suspend fun getAllTravelItineraryList(userId: String): List<TravelItinerary>
}