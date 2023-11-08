package com.innoappsai.guido.data.travel_itinerary

import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.db.MyAppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ItineraryRepositoryImpl @Inject constructor(
    private val db: MyAppDataBase,
    private val api: GuidoApi
) : ItineraryRepository {
    override suspend fun addItinerary(itinerary: TravelItinerary) {
        withContext(Dispatchers.IO) {
            db.itineraryDao().deleteAllTravelItinerary()
            db.itineraryDao().insertItinerary(itinerary)
        }
    }

    override suspend fun getItineraryById(itineraryId: String) = api.getItineraryDetails(itineraryId).body()
    override suspend fun getAllTravelItineraryList(userId: String): List<TravelItinerary> {
        val response = api.getAllGenerateTravelItineraryListByUserId(userId)
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            emptyList()
        }
    }

}