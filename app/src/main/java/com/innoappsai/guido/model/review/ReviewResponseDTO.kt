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
    val author: User? = null
)

fun ReviewResponseDTO.toUiModel() = Review(
    title = reviewTitle,
    description = reviewDescription,
    rating = rating,
    user = author
)