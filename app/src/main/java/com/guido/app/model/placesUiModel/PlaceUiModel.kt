package com.guido.app.model.placesUiModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.guido.app.model.places.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val name : String?,
    val address : String?,
    val icon : String?,
    @SerializedName("icon_background_color")
    val iconBackGroundColor: String?,
    val photos: List<Photo>?,
    val rating: Double?,
): Parcelable

