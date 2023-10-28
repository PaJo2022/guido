package com.innoappsai.guido.generateItinerary.model

import java.util.UUID

data class DayWiseTimeSelection(
    val id : String = UUID.randomUUID().toString(),
    val dayName : String,
    var startValue : Float,
    var endValue : Float
)
