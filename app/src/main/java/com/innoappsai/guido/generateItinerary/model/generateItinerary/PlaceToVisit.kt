package com.innoappsai.guido.generateItinerary.model.generateItinerary

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class TravelItinerary(
    val placeName: String,
    val countryName: String,
    val tripLength: Int,
    val tripStartDate: String,
    val landmarks: List<Landmark>
)

data class Landmark(
    val id: String?,
    val name: String?
)

fun generateTravelItineraryString(itinerary: TravelItinerary): String {
    val sb = StringBuilder()

    sb.append("I want you to create me a travel itinerary for my next travel.")
    sb.append(" I will be going for a tour for ${itinerary.tripLength} days.")
    sb.append(" Each day I will start my travel at 10 AM and will travel for 6 hours.")
    sb.append("\n\n")
    sb.append("I will start my travel on ${itinerary.tripStartDate}\n\n")
    sb.append("These are the list of landmarks I wanna visit there and willing to spend a minimum of 1 hour at each place:\n\n")

    for ((index, landmark) in itinerary.landmarks.withIndex()) {
        sb.append("${index + 1}. ${landmark.name}(id: ${landmark.id})\n")
    }

    return sb.toString()
}