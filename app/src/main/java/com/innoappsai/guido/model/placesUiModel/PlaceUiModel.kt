package com.innoappsai.guido.model.placesUiModel

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.model.DataType
import com.innoappsai.guido.model.PlaceType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val placeId: String? = null,
    val name: String? = null,
    val latLng: LatLng? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val createdBy: String? = null,
    val icon: String? = null,
    val iconDrawable: Int? = null,
    val type: String? = null,
    val placeType: PlaceType? = null,
    val placeDescription: String? = null,
    @SerializedName("icon_background_color")
    val iconBackGroundColor: String? = null,
    val photos: List<String>? = null,
    val videos: List<String>? = null,
    val rating: Double? = null,
    val callNumber: String? = null,
    val website: String? = null,
    val isOpenNow: Boolean = false,
    val openTill: String? = null,
    val reviews: List<ReviewUiModel>? = null,
    var isSelected: Boolean = false,
    var placeUiType: PlaceUiType = PlaceUiType.SMALL
) : Parcelable


fun List<PlaceUiModel>.addUiType(iconDrawable: Int?,placeUiType: PlaceUiType) = map {
    PlaceUiModel(
        it.placeId,
        it.name,
        it.latLng,
        it.address,
        it.icon,
        it.createdBy,
        it.city,
        it.state,
        it.country,
        iconDrawable,
        it.type,
        it.placeType,
        it.iconBackGroundColor,
        it.placeDescription,
        it.photos,
        it.videos,
        it.rating,
        it.callNumber,
        it.website,
        it.isOpenNow,
        it.openTill,
        it.reviews,
        it.isSelected,
        placeUiType
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
): Parcelable



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