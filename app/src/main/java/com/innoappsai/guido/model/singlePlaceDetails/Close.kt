package com.innoappsai.guido.model.singlePlaceDetails

data class Close(
    val date: String,
    val day: Int,
    val time: String,
    val truncated: Boolean
)