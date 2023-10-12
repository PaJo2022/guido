package com.innoappsai.guido.data.travel_itinerary

import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.db.MyAppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ItineraryRepositoryImpl @Inject constructor(
    private val db: MyAppDataBase
) : ItineraryRepository {
    override suspend fun addItinerary(itinerary: TravelItinerary) {
        withContext(Dispatchers.IO) {
            db.itineraryDao().insertItinerary(itinerary)
        }
    }

    override fun getItineraryById(id: String) = db.itineraryDao().getTravelItineraryById(id)

}