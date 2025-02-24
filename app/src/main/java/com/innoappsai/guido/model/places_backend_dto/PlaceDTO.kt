package com.innoappsai.guido.model.places_backend_dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.Constants.getTypesOfPlaces
import com.innoappsai.guido.model.PlaceTimings
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "PLACE_DTO")
data class PlaceDTO(
    @PrimaryKey(autoGenerate = false)
    val placeId: String,
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
    val placeName: String? = null,
    val placeOpenStatus: String? = null,
    val pricingType: String?,
    val rating: Double? = null,
    val reviewsCount: Int? = null,
    val types: List<String>? = null,
    val website: String? = null,
    val instagram: String? = null,
    val facebook: String? = null,
    val businessEmail: String? = null,
    val businessOwner: String? = null,
    val businessSpecialNotes: String? = null,
    val openTill: String? = null,
    val icon: String? = null,
    val typeName: String? = null,
    val superType: String? = null,
    val placeFeatures: List<String>? = null,
    val placeTimings: List<PlaceTimings>? = null,
    var isChecked: Boolean = false,
    var shouldShowCheckBox: Boolean = false,
) : Parcelable

fun List<PlaceDTO>.toPlaceUiModel() = map{
    PlaceUiModel(
        serverDbId = it.id,
        placeId = it.placeId,
        name = it.placeName,
        createdBy = it.createdBy,
        latLng = LatLng(it.location?.latitude ?: 0.0, it.location?.longitude ?: 0.0),
        placeMapImage = it.placeMapImage,
        address = it.placeAddress,
        city = it.placeCity,
        state = it.placeState,
        pricingType = it.pricingType,
        country = it.placeCountry,
        placeDescription = it.placeDescription,
        icon = it.icon,
        typeName = it.typeName,
        superType = it.superType,
        iconBackGroundColor = null,
        type = getTypesOfPlaces(it.types?.firstOrNull() ?: ""),
        photos = it.photos,
        videos = it.videos,
        rating = it.rating,
        reviewsCount = it.reviewsCount,
        callNumber = it.contactNumber,
        website = it.website,
        instagram = it.instagram,
        facebook = it.facebook,
        businessEmail = it.businessEmail,
        businessOwner = it.businessOwner,
        businessSpecialNotes = it.businessSpecialNotes,
        placeFeatures = it.placeFeatures,
        placeTimings = it.placeTimings,
        placeOpenStatus = it.placeOpenStatus,
        openTill = it.openTill,
        isChecked = it.isChecked,
        shouldShowCheckBox = it.shouldShowCheckBox
    )
}

fun PlaceDTO.toPlaceUiModel() =  PlaceUiModel(
    serverDbId = id,
    placeId = placeId,
    name = placeName,
    createdBy = createdBy,
    pricingType = pricingType,
    latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
    placeMapImage = placeMapImage,
    address = placeAddress,
    icon = icon,
    typeName = typeName,
    superType = superType,
    city = placeCity,
    state = placeState,
    country = placeCountry,
    placeDescription =placeDescription,
    iconBackGroundColor = null,
    type = getTypesOfPlaces(types?.firstOrNull() ?: ""),
    photos = photos,
    videos = videos,
    rating = rating,
    reviewsCount = reviewsCount,
    callNumber = contactNumber,
    website = website,
    instagram = instagram,
    facebook = facebook,
    businessEmail = businessEmail,
    businessOwner = businessOwner,
    businessSpecialNotes = businessSpecialNotes,
    placeFeatures = placeFeatures,
    placeTimings = placeTimings,
    placeOpenStatus = placeOpenStatus,
    openTill = openTill,
    isChecked = isChecked,
    shouldShowCheckBox = shouldShowCheckBox
)