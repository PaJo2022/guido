package com.guido.app.model.places

import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel


data class Result(
    val business_status: String?,
    val geometry: Geometry?,
    val icon: String?,
    val icon_background_color: String?,
    val icon_mask_base_uri: String?,
    val name: String?,
    val opening_hours: OpeningHours?,
    val photos: List<Photo>?,
    val place_id: String?,
    val plus_code: PlusCode?,
    val rating: Double?,
    val reference: String?,
    val scope: String?,
    val types: List<String>?,
    val user_ratings_total: Int?,
    val vicinity: String?
)

fun List<Result>.toPlaceUiModel() = map{
    PlaceUiModel(
        name = it.name,
        address = it.vicinity,
        icon = it.icon,
        iconBackGroundColor = it.icon_background_color,
        photos = it.photos,
        rating = it.rating
    )
}