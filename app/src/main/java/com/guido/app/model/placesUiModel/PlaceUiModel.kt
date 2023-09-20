package com.guido.app.model.placesUiModel

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.guido.app.model.DataType
import com.guido.app.model.places.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val placeId: String? = null,
    val name: String? = null,
    val latLng: LatLng? = null,
    val address: String? = null,
    val icon: String? = null,
    @SerializedName("icon_background_color")
    val iconBackGroundColor: String? = null,
    val photos: List<Photo>? = null,
    val rating: Double? = null,
    val callNumber: String? = null,
    val website: String? = null,
    val isOpenNow: Boolean = false,
    val openTill: String? = null,
    val reviews: List<ReviewUiModel>? = null,
    var placeUiType: PlaceUiType = PlaceUiType.SMALL
) : Parcelable


fun List<PlaceUiModel>.addUiType(placeUiType: PlaceUiType) = map {
    PlaceUiModel(
        it.placeId,
        it.name,
        it.latLng,
        it.address,
        it.icon,
        it.iconBackGroundColor,
        it.photos,
        it.rating,
        it.callNumber,
        it.website,
        it.isOpenNow,
        it.openTill,
        it.reviews,
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
    val icon: String? = null,
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