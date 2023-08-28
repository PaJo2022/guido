package com.guido.app.data.places

import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel

interface PlacesRepository {

    suspend fun fetchPlacesNearMe(
        location : String,
        radius : Int,
        type : String,
        keyword : String?=null,
        key : String,
    ) : List<PlaceUiModel>
}