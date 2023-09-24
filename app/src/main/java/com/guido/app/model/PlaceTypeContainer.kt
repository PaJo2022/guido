package com.guido.app.model

data class PlaceTypeContainer(
    val type: String,
    val placeTypes: List<PlaceType>,
    var isOpened: Boolean = false
)
