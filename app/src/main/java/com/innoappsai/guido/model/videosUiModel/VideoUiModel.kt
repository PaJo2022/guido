package com.innoappsai.guido.model.videosUiModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUiModel(
    val id : String?,
    val title : String?,
    val description : String?,
    val thumbnail : String?,
    val videoUrl : String?
): Parcelable

