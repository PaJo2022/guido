package com.innoappsai.guido.model

data class PlaceTypeContainer(
    val type: String,
    val placeTypes: List<PlaceType>,
    var isOpened: Boolean = false
)
