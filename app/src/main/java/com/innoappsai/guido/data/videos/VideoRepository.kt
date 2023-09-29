package com.innoappsai.guido.data.videos

import com.innoappsai.guido.model.videosUiModel.VideoUiModel

interface VideoRepository {

    suspend fun fetchPlacesVideos(
        query: String,apiKey : String
    ) : List<VideoUiModel>
}