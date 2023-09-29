package com.innoappsai.guido.model

import android.os.Parcelable
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.videosUiModel.VideoUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailsUiModel(
    var placeUiModel: PlaceUiModel?=null,
    var locationStory : String ?= null,
    var locationVideos : List<VideoUiModel> ?= null
) : Parcelable

