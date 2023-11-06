package com.innoappsai.guido.generateItinerary.model

data class TripDataForNotification(
    val placeId: String,
    val placeName: String,
    val placeImage: String?,
    val notificationDateAndTime: String
)
