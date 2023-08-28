package com.guido.app.model

import android.os.Parcelable
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailsUiModel(
    var placeUiModel: PlaceUiModel?=null,
    var locationStory : String ?= null,
    var locationVideos : List<VideoUiModel> ?= null
) : Parcelable

