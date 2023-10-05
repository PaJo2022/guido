package com.innoappsai.guido.model

enum class VideoType{
    OWN_VIDEO,YOUTUBE_VIDEO
}

data class VideoItem(
    val videoType : VideoType,
    val videoLink : String
)