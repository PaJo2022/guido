package com.guido.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guido.app.model.PlaceAutocomplete
import com.guido.app.model.PlaceType
import com.guido.app.model.User
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