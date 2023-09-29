package com.innoappsai.guido.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.innoappsai.guido.model.PlaceAutocomplete
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchedLocation(placeAutoComplete : PlaceAutocomplete?)

    @Query("SELECT * FROM PLACE_AUTO_COMPLETE LIMIT 5")
    fun getRecentSearchLocations() : Flow<List<PlaceAutocomplete>>




    @Query("DELETE FROM PLACE_AUTO_COMPLETE")
    fun deleteAllSearchedLocations()

}