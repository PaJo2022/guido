package com.innoappsai.guido.model.PlaceIdWithName

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PLACE_ID_WITH_TIME")
data class PlaceWithIdAndTime(
    @PrimaryKey(autoGenerate = false)
    var id : String,
    var lashPushNotificationShown : Long
)
