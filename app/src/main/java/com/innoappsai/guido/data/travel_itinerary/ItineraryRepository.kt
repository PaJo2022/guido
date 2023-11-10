package com.innoappsai.guido.data.travel_itinerary

import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.generateItinerary.model.ItineraryDataForModel
import kotlinx.coroutines.flow.Flow

interface ItineraryRepository {
    suspend fun addItinerary(itinerary: TravelItinerary)
    suspend fun getItineraryById(itineraryId: String) : TravelItinerary?

    suspend fun getAllTravelItineraryList(userId: String): List<TravelItinerary>

    suspend fun addItineraryIdWhenAlarmIsSet(itineraryDataForModel: ItineraryDataForModel)
    suspend fun isAlarmSetForTheItineraryId(itineraryId: String) : ItineraryDataForModel?
    fun isAlarmSetForTheItineraryIdUsingFlow(itineraryId: String) : Flow<ItineraryDataForModel?>
    suspend fun deleteItineraryAlarmForTheItineraryId(itineraryId: String)
}