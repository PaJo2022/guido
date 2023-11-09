package com.innoappsai.guido

object DeepLinkHelper {
    fun generateItineraryGenerationDeeplink(id: String): String {
        return "https://www.guidoai.com/itinerary?id=${id}"
    }
}