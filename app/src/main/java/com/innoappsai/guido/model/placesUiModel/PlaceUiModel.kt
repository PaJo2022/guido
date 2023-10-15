package com.innoappsai.guido.model.placesUiModel

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.model.DataType
import com.innoappsai.guido.model.PlaceTimings
import com.innoappsai.guido.model.PlaceType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val serverDbId: String? = null,
    val placeId: String? = null,
    val name: String? = null,
    val latLng: LatLng? = null,
    val placeMapImage: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val createdBy: String? = null,
    val icon: String? = null,
    val typeName: String? = null,
    val superType: String? = null,
    val type: String? = null,
    val placeType: PlaceType? = null,
    val pricingType: String? = null,
    val placeDescription: String? = null,
    @SerializedName("icon_background_color")
    val iconBackGroundColor: String? = null,
    val photos: List<String>? = null,
    val videos: List<String>? = null,
    val rating: Double? = null,
    val reviewsCount: Int? = null,
    val callNumber: String? = null,
    val website: String? = null,
    val instagram: String? = null,
    val facebook: String? = null,
    val businessEmail: String? = null,
    val businessOwner: String? = null,
    val businessSpecialNotes: String? = null,
    val placeFeatures: List<String>? = null,
    val placeTimings: List<PlaceTimings>? = null,
    val isOpenNow: Boolean = false,
    val placeOpenStatus: String? = null,
    val openTill: String? = null,
    val reviews: List<ReviewUiModel>? = null,
    var isSelected: Boolean = false,
    var isChecked: Boolean = false,
    var shouldShowCheckBox: Boolean = false,
    var placeUiType: PlaceUiType = PlaceUiType.SMALL
) : Parcelable


@Parcelize
data class ExtraInfoWithIcon(
    val icon: Int?,
    val title: String?,
    val extraInfo: String?
) : Parcelable


fun List<PlaceUiModel>.addUiType(placeUiType: PlaceUiType) = map {
    PlaceUiModel(
        isChecked = it.isChecked,
        shouldShowCheckBox = it.shouldShowCheckBox,
        serverDbId  = it.serverDbId,
        icon = it.icon,
        typeName = it.typeName,
        superType = it.superType,
        placeUiType = placeUiType,
        placeId = it.placeId,
        pricingType = it.pricingType,
        name = it.name,
        createdBy = it.createdBy,
        latLng = it.latLng,
        placeMapImage = it.placeMapImage,
        address = it.address,
        city = it.city,
        state = it.state,
        country = it.country,
        placeDescription = it.placeDescription,
        iconBackGroundColor = null,
        type = it.type,
        photos = it.photos,
        videos = it.videos,
        rating = it.rating,
        reviewsCount = it.reviewsCount,
        callNumber = it.callNumber,
        website = it.website,
        instagram = it.instagram,
        facebook = it.facebook,
        businessEmail = it.businessEmail,
        businessOwner = it.businessOwner,
        businessSpecialNotes = it.businessSpecialNotes,
        placeFeatures = it.placeFeatures,
        placeTimings = it.placeTimings,
        placeOpenStatus = it.placeOpenStatus,
        openTill = it.openTill
    )
}
@Parcelize
data class ReviewUiModel(
    val authorName: String,
    val authorProfilePic: String,
    val authorRating: Int,
    val reviewedDateInMillis: Long,
    val reviewText: String,
    val reviewDone: String,
) : Parcelable



enum class PlaceUiType {
    SMALL, LARGE, SMALL_SHIMMER, LARGE_SHIMMER
}


@Parcelize
data class PlaceTypeUiModel(
    val type: String? = null,
    val icon: Int? = null,
    val places: List<PlaceUiModel>? = null,
    var dataType: DataType = DataType.SHIMMER
) : Parcelable


val DUMMY_PLACE_TYPE_UI_MODEL = arrayListOf(
    PlaceTypeUiModel(
        type = "",
        icon = null,
        places = listOf(
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            ),
            PlaceUiModel(
                placeUiType = PlaceUiType.LARGE_SHIMMER
            )
        )
    )
)