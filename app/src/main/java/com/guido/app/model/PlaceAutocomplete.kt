package com.guido.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Place_Auto_Complete")
data class PlaceAutocomplete(
    @PrimaryKey(autoGenerate = false)
    val placeId: String,
    val area: String,
    val address: String
)
