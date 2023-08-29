package com.guido.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guido.app.model.PlaceType
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewPlaceType(placeType: PlaceType?)

    @Query("SELECT * FROM PLACE_TYPE")
    fun getAllPlaceTypes() : Flow<List<PlaceType>>




    @Query("DELETE FROM PLACE_TYPE")
    fun deletePlaceTypes()
}