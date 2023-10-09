package com.innoappsai.guido.model.review

import android.os.Parcelable
import com.innoappsai.guido.model.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Review(
    val title: String?,
    val description: String?,
    val rating: Double?,
    val user: User?,
    val mediaFiles: List<PlaceMediaItem> ?= emptyList()
) : Parcelable

enum class MediaType {
    Image, Video
}
@Parcelize
data class PlaceMediaItem(
    val mediaType: MediaType,
    val url: String
) : Parcelable
