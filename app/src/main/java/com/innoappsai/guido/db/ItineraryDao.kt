package com.innoappsai.guido.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.innoappsai.guido.TravelItinerary
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItinerary(itinerary: TravelItinerary?)

    @Query("SELECT * FROM TRAVEL_ITINERARY_TABLE WHERE id =:id")
    fun getTravelItineraryById(id : String) : Flow<TravelItinerary>

    @Query("SELECT * FROM TRAVEL_ITINERARY_TABLE")
    fun getAllTravelItinerary() : Flow<TravelItinerary?>


    @Query("DELETE  FROM TRAVEL_ITINERARY_TABLE")
    suspend fun deleteAllTravelItinerary()

}