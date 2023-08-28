package com.guido.app.model.videos

import com.guido.app.model.videosUiModel.VideoUiModel

data class YoutubeVideosDTO(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val regionCode: String
)

fun List<Item>.toUiModel() = map{
    VideoUiModel(
        id = it.id.videoId,
        title = it.snippet.title,
        description = it.snippet.description,
        thumbnail = it.snippet.thumbnails.high.url,
        videoUrl = "https://www.youtube.com/embed/${it.id.videoId}"
    )
}