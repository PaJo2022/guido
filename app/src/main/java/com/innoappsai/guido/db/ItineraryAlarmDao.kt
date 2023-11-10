package com.innoappsai.guido.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.innoappsai.guido.generateItinerary.model.ItineraryDataForModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryAlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItinerary(itineraryModel: ItineraryDataForModel?)

    @Query("SELECT * FROM ITINERARY_DATA_FOR_MODEL WHERE itineraryId =:itineraryId")
    suspend fun getTravelItineraryById(itineraryId: String): ItineraryDataForModel?

    @Query("SELECT * FROM ITINERARY_DATA_FOR_MODEL WHERE itineraryId =:itineraryId")
    fun getTravelItineraryByIdUsingFlow(itineraryId: String): Flow<ItineraryDataForModel?>

    @Query("DELETE FROM ITINERARY_DATA_FOR_MODEL WHERE itineraryId = :itineraryId")
    suspend fun deleteTravelItineraryById(itineraryId: String)

}