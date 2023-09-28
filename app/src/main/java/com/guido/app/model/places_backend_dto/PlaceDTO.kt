package com.guido.app.model.places_backend_dto

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.guido.app.Constants.getTypesOfPlaces
import com.guido.app.model.placesUiModel.PlaceUiModel

data class PlaceDTO(
    @SerializedName("_id")
    val id: String,
    val contactNumber: String?,
    val createdAt: String?,
    @SerializedName("geoLocation")
    val location: Location?,
    val photos: List<String>?,
    val placeAddress: String?,
    val placeId: String?,
    val placeName: String?,
    val rating: Double?,
    val reviews: Any?,
    val types: List<String>?,
    val website: String?
)

fun List<PlaceDTO>.toPlaceUiModel() = map{
    PlaceUiModel(
        placeId = it.placeId,
        name = it.placeName,
        latLng = LatLng(it.location?.latitude ?: 0.0, it.location?.longitude ?: 0.0),
        address = it.placeAddress,
        icon = null,
        iconBackGroundColor = null,
        type = getTypesOfPlaces(it.types?.firstOrNull() ?: ""),
        photos = it.photos,
        rating = it.rating,
        callNumber = it.contactNumber,
        website = it.website,
    )
}

fun PlaceDTO.toPlaceUiModel() =  PlaceUiModel(
    placeId = placeId,
    name = placeName,
    latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
    address = placeAddress,
    icon = null,
    iconBackGroundColor = null,
    type = getTypesOfPlaces(types?.firstOrNull() ?: ""),
    photos = photos,
    rating = rating,
    callNumber = contactNumber,
    website = website,
)