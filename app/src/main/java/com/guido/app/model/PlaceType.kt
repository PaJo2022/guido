package com.guido.app.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guido.app.Colors
import com.guido.app.R
import kotlinx.parcelize.Parcelize

@Entity(tableName = "PLACE_TYPE")
@Parcelize
data class PlaceType(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val displayName: String,
    val type: String,
    var iconDrawable: Int = R.drawable.hospital_marker,
    var isSelected: Boolean = false,
    var selectedColor: Int = Colors.getColorsBasedOnIndex()
) : Parcelable
