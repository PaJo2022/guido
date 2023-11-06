package com.innoappsai.guido.generateItinerary.model.generateItinerary

import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection

data class TravelItinerary(
    val placeName: String,
    val placeLatitude: Double?,
    val placeLongitude: Double?,
    val travelingAs: String?,
    val countryName: String,
    val tripLength: Int,
    val tripStartDate: String,
    val tripBudget: String?,
    val dailySchedule: List<DayWiseTimeSelection>,
    val landmarks: List<Landmark>
)

data class Landmark(
    val id: String?,
    val name: String?,
    val landMarkType: String?,
    val timeSpent: String = "1 Hour"
)



fun generateTravelItineraryString(itinerary: TravelItinerary): String {
    val sb = StringBuilder()

    sb.append("I want you to create me a travel itinerary for my next travel to ${itinerary.placeName},${itinerary.countryName} whose coordinates are ${itinerary.placeLatitude},${itinerary.placeLongitude} and traveling as ${itinerary.travelingAs} and my budget is ${itinerary.tripBudget}")
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