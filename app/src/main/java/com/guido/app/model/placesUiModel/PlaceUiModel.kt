package com.guido.app.model.placesUiModel

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.guido.app.model.places.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val name : String?,
    val latLng: LatLng? = null,
    val address: String?,
    val icon: String?,
    @SerializedName("icon_background_color")
    val iconBackGroundColor: String?,
    val photos: List<Photo>?,
    val rating: Double?,
    var placeUiType: PlaceUiType = PlaceUiType.SMALL
) : Parcelable

fun List<PlaceUiModel>.addUiType(placeUiType: PlaceUiType) = map {
    PlaceUiModel(
        it.name,
        it.latLng,
        it.address,
        it.icon,
        it.iconBackGroundColor,
        it.photos,
        it.rating,
        placeUiType
    )
}

enum class PlaceUiType {
    SMALL, LARGE
}


@Parcelize
data class PlaceTypeUiModel(
    val type: String?,
    val icon: String?,
    val places: List<PlaceUiModel>?
) : Parcelable

