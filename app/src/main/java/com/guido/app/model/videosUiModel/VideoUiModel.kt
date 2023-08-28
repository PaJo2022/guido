package com.guido.app.model.videosUiModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.guido.app.model.places.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUiModel(
    val id : String?,
    val title : String?,
    val description : String?,
    val thumbnail : String?,
    val videoUrl : String?
): Parcelable

