package com.guido.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guido.app.R

@Entity(tableName = "PLACE_TYPE")
data class PlaceType(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val displayName: String,
    val type : String,
    var isSelected: Boolean = false,
    var selectedColor : Int = R.color.color_sound_compose_selected_color1)
