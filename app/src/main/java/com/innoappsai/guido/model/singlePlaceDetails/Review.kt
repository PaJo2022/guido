package com.innoappsai.guido.model.singlePlaceDetails

import com.innoappsai.guido.model.placesUiModel.ReviewUiModel

data class Review(
    val author_name: String,
    val author_url: String,
    val language: String,
    val original_language: String,
    val profile_photo_url: String,
    val rating: Int,
    val relative_time_description: String,
    val text: String,
    val time: Int,
    val translated: Boolean
)

fun List<Review>.toUiModel() = map {
    ReviewUiModel(
        authorName = it.author_name,
        authorProfilePic = it.profile_photo_url,
        authorRating = it.rating,
        reviewedDateInMillis = it.time.toLong(),
        reviewText = it.text,
        reviewDone = it.relative_time_description
    )
}