package com.guido.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guido.app.Colors

@Entity(tableName = "PLACE_TYPE")
data class PlaceType(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val displayName: String,
    val type: String,
    var isSelected: Boolean = false,
    var selectedColor: Int = Colors.getColorsBasedOnIndex()
)
