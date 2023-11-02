package com.innoappsai.guido.generateItinerary.model

import com.innoappsai.guido.generateItinerary.model.generateItinerary.PlaceToVisit

data class Trip(
    val placeName: String,
    val placeCountry: String,
    val tripLength: String,
    val tripPartners: String,
    val tripStartDate: String,
    val tripBudget: String,
    val tripEachDayTimings: List<TripDayTiming>,
    val tripPlacesWannaVisit: List<PlaceToVisit>
)

data class TripDayTiming(
    val day: String,
    val tripStartTime: String,
    val tripEndTime: String
)

