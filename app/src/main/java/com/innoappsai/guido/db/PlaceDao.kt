package com.innoappsai.guido.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<PlaceDTO>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceDTO?)

    @Query("SELECT * FROM PLACE_DTO WHERE placeId =:dbId")
    fun getPlaceById(dbId : String) : Flow<PlaceDTO>

    @Query("SELECT * FROM PLACE_DTO")
    fun getAllPlaces() : Flow<List<PlaceDTO>>

    @Query("DELETE FROM PLACE_DTO WHERE placeId = :dbId")
    suspend fun deletePlaceById(dbId: String)

    @Query("DELETE FROM PLACE_DTO ")
    suspend fun deleteAllPlaces()

}