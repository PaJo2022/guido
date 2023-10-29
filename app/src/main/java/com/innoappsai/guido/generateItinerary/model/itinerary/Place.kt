package com.innoappsai.guido.generateItinerary.model.itinerary

data class Place(
    val placeId: String,
    val placeName: String,
    val placePhotos: List<String>,
    val timeSlots: String
)