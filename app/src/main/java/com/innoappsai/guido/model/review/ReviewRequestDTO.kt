package com.innoappsai.guido.model.review

data class ReviewRequestDTO(
    val placeId: String? = null,
    val rating: Double? = null,
    val reviewDescription: String? = null,
    val reviewTitle: String? = null,
    val userId: String? = null,
    val authorName: String? = null,
    val authorProfileUrl: String? = null
)