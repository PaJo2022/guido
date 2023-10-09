package com.innoappsai.guido.model.review

import com.innoappsai.guido.model.User

data class Review(
    val title: String?,
    val description: String?,
    val rating: Double?,
    val user: User?,
    val mediaFiles: List<PlaceMediaItem> ?= emptyList()
)

enum class MediaType {
    Image, Video
}

data class PlaceMediaItem(
    val mediaType: MediaType,
    val url: String
)
