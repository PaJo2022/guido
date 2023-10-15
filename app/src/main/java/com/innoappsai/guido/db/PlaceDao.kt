package com.innoappsai.guido.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<PlaceDTO>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceDTO?)

    @Query("SELECT * FROM PLACE_DTO WHERE placeId =:dbId")
    fun getPlaceById(dbId: String): Flow<PlaceDTO>

    @Query("SELECT * FROM PLACE_DTO")
    fun getAllPlaces(): Flow<List<PlaceDTO>>

    @Update
    suspend fun updatePlace(place: PlaceDTO?)

    @Query("DELETE FROM PLACE_DTO WHERE placeId = :dbId")
    suspend fun deletePlaceById(dbId: String)

    @Query("DELETE FROM PLACE_DTO ")
    suspend fun deleteAllPlaces()

    @Query("UPDATE PLACE_DTO SET isChecked = :isChecked,shouldShowCheckBox = :shouldShowCheckBox")
    suspend fun updateAllPlacesIsCheckedAndCheckBoxFor(isChecked: Boolean,shouldShowCheckBox : Boolean)


    @Query("UPDATE PLACE_DTO SET isChecked = :isChecked,shouldShowCheckBox = 1 WHERE placeId = :placeId")
    suspend fun updatePlaceIsChecked(placeId: String, isChecked: Boolean)


    @Query("SELECT * FROM PLACE_DTO WHERE isChecked = 1 AND shouldShowCheckBox = 1")
    suspend fun getAllSelectedPlaces(): List<PlaceDTO>
}