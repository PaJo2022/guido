package com.guido.app.data.videos

import com.guido.app.model.videosUiModel.VideoUiModel

interface VideoRepository {

    suspend fun fetchPlacesVideos(
        query: String,apiKey : String
    ) : List<VideoUiModel>
}