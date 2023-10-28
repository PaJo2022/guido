package com.innoappsai.guido.generateItinerary.model

import java.util.UUID

data class InterestsSelection(
    val id : String = UUID.randomUUID().toString(),
    val interestsName : String,
    val interestsIcon : Int,
    var interestsLevel : Float,
)
