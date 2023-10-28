package com.innoappsai.guido.generateItinerary.model


enum class TravelItineraryGenerationOption {
    TRAVEL_DURATION, TRAVEL_TIME_ALLOCATION, TRAVEL_DATE, TRAVEL_COMPANION, TRAVEL_INTERESTS, TRAVEL_BUDGET
}

val itineraryGenerationOptionList = arrayListOf(
    TravelItineraryGenerationOption.TRAVEL_DURATION,
    TravelItineraryGenerationOption.TRAVEL_TIME_ALLOCATION,
    TravelItineraryGenerationOption.TRAVEL_DATE,
    TravelItineraryGenerationOption.TRAVEL_COMPANION,
    TravelItineraryGenerationOption.TRAVEL_INTERESTS,
    TravelItineraryGenerationOption.TRAVEL_BUDGET,
)