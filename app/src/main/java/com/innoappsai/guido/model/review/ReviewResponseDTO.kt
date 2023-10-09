package com.innoappsai.guido.model.review

import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.model.User

data class ReviewResponseDTO(
    @SerializedName("_id")
    val id: String? = null,
    val placeId: String? = null,
    val rating: Double? = null,
    val reviewDescription: String? = null,
    val reviewTitle: String? = null,
    val userId: String? = null,
    val author: User? = null,
    var reviewImageUrls: List<String>? = null,
    var reviewVideoUrls: List<String>? = null
)

fun ReviewResponseDTO.toUiModel() = Review(
    title = reviewTitle,
    description = reviewDescription,
    rating = rating,
    user = author,
    mediaFiles = if (reviewImageUrls?.isNotEmpty() == true) reviewImageUrls?.map {
        PlaceMediaItem(
            mediaType = MediaType.Image,
            it
        )
    } else if (reviewVideoUrls?.isNotEmpty() == true) reviewVideoUrls?.map {
        PlaceMediaItem(
            mediaType = MediaType.Video,
            it
        )
    } else emptyList()
)