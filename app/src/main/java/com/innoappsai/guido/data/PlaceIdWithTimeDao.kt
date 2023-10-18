package com.innoappsai.guido.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.innoappsai.guido.model.PlaceIdWithName.PlaceWithIdAndTime
import retrofit2.http.DELETE

@Dao
interface PlaceIdWithTimeDao {

    @Insert
    suspend fun onPlacePushNotificationSend(placeWithIdAndTime: PlaceWithIdAndTime)

    @Query("SELECT * FROM place_id_with_time WHERE id = :placeId")
    suspend fun getPlaceIdWithTime(placeId : String) : PlaceWithIdAndTime?

    @Query("UPDATE place_id_with_time SET lashPushNotificationShown = :lashPushNotificationShown WHERE id = :placeId")
    suspend fun updateAllPlacesIsCheckedAndCheckBoxFor(placeId : String,lashPushNotificationShown : Long)

//    @DELETE
//    suspend fun deleteAllData()
}