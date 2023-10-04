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
    val id: String? = null,
    val contactNumber: String? = null,
    val createdBy: String? = null,
    @SerializedName("geoLocation")
    val location: Location? = null,
    var photos: List<String>? = null,
    var videos: List<String>? = null,
    val placeMapImage: String? = null,
    val placeAddress: String? = null,
    val placeCity: String? = null,
    val placeState: String? = null,
    val placeCountry: String? = null,
    val placeDescription: String? = null,
    val placeId: String? = null,
    val placeName: String? = null,
    val pricingType: String?,
    val rating: Double? = null,
    val types: List<String>? = null,
    val website: String? = null,
    val instagram: String? = null,
    val facebook: String? = null,
    val businessEmail: String? = null,
    val businessOwner: String? = null,
    val businessSpecialNotes: String? = null,
    val placeFeatures: List<String>? = null,
    val placeTimings: List<String>? = null,
) : Parcelable

fun List<PlaceDTO>.toPlaceUiModel() = map{
    PlaceUiModel(
        placeId = it.placeId,
        name = it.placeName,
        createdBy = it.createdBy,
        latLng = LatLng(it.location?.latitude ?: 0.0, it.location?.longitude ?: 0.0),
        placeMapImage = it.placeMapImage,
        address = it.placeAddress,
        city = it.placeCity,
        state = it.placeState,
        country = it.placeCountry,
        placeDescription = it.placeDescription,
        icon = null,
        iconBackGroundColor = null,
        type = getTypesOfPlaces(it.types?.firstOrNull() ?: ""),
        photos = it.photos,
        videos = it.videos,
        rating = it.rating,
        callNumber = it.contactNumber,
        website = it.website,
        instagram = it.instagram,
        facebook = it.facebook,
        businessEmail = it.businessEmail,
        businessOwner = it.businessOwner,
        businessSpecialNotes = it.businessSpecialNotes,
        placeFeatures = it.placeFeatures,
        placeTimings = it.placeTimings,
    )
}

fun PlaceDTO.toPlaceUiModel() =  PlaceUiModel(
    placeId = placeId,
    name = placeName,
    createdBy = createdBy,
    latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
    placeMapImage = placeMapImage,
    address = placeAddress,
    city = placeCity,
    state = placeState,
    country = placeCountry,
    placeDescription =placeDescription ,
    icon = null,
    iconBackGroundColor = null,
    type = getTypesOfPlaces(types?.firstOrNull() ?: ""),
    photos = photos,
    videos = videos,
    rating = rating,
    callNumber = contactNumber,
    website = website,
    instagram = instagram,
    facebook = facebook,
    businessEmail = businessEmail,
    businessOwner = businessOwner,
    businessSpecialNotes = businessSpecialNotes,
    placeFeatures = placeFeatures,
    placeTimings = placeTimings,
)