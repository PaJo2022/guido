package com.innoappsai.guido.model

import java.util.UUID

data class PlaceTimings(
    val id: String = UUID.randomUUID().toString(),
    val dayOfTheWeek: String,
    val from: String,
    val to: String
)
