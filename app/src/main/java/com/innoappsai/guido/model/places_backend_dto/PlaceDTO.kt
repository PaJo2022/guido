package com.innoappsai.guido.model.places_backend_dto

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.Constants.getTypesOfPlaces
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceDTO(
    @SerializedName("_id")
    val id: String?=null,
    val contactNumber: String?=null,
    val createdBy: String?=null,
    @SerializedName("geoLocation")
    val location: Location?=null,
    var photos: List<String>?=null,
    var videos: List<String>?=null,
    val placeAddress: String?=null,
    val placeDescription: String?=null,
    val placeId: String?=null,
    val placeName: String?=null,
    val pricingType: String?,
    val rating: Double?=null,
    val types: List<String>?=null,
    val website: String?=null
) : Parcelable

fun List<PlaceDTO>.toPlaceUiModel() = map{
    PlaceUiModel(
        placeId = it.placeId,
        name = it.placeName,
        createdBy = it.createdBy,
        latLng = LatLng(it.location?.latitude ?: 0.0, it.location?.longitude ?: 0.0),
        address = it.placeAddress,
        placeDescription = it.placeDescription,
        icon = null,
        iconBackGroundColor = null,
        type = getTypesOfPlaces(it.types?.firstOrNull() ?: ""),
        photos = it.photos,
        videos = it.videos,
        rating = it.rating,
        callNumber = it.contactNumber,
        website = it.website,
    )
}

fun PlaceDTO.toPlaceUiModel() =  PlaceUiModel(
    placeId = placeId,
    name = placeName,
    createdBy = createdBy,
    latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
    address = placeAddress,
    placeDescription =placeDescription ,
    icon = null,
    iconBackGroundColor = null,
    type = getTypesOfPlaces(types?.firstOrNull() ?: ""),
    photos = photos,
    videos = videos,
    rating = rating,
    callNumber = contactNumber,
    website = website,
)