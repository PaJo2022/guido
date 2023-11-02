package com.innoappsai.guido.generateItinerary.model.generateItinerary

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class PlaceToVisit(
    val placeId: String?,
    val latitude: Double?,
    val longitude: Double?,
    val placeName: String?,
    val placePhotos: List<String>?
)


fun createTripData(
    placeName: String,
    placeCountry: String,
    tripLength: String,
    tripPartners: String,
    tripStartDate: String,
    tripBudget: String,
    tripEachDayTimings: List<Triple<String, String, String>>,
    tripPlacesWannaVisit: List<PlaceToVisit>
): JsonObject {
    val tripData = JsonObject()
    tripData.addProperty("placeName", placeName)
    tripData.addProperty("placeCountry", placeCountry)
    tripData.addProperty("tripLength", tripLength)
    tripData.addProperty("tripPartners", tripPartners)
    tripData.addProperty("tripStartDate", tripStartDate)
    tripData.addProperty("tripBudget", tripBudget)

    val tripEachDayTimingsArray = JsonArray()
    tripEachDayTimings.forEach { (day, tripStartTime, tripEndTime) ->
        val dayTiming = JsonObject()
        dayTiming.addProperty("day", day)
        dayTiming.addProperty("tripStartTime", tripStartTime)
        dayTiming.addProperty("tripEndTime", tripEndTime)
        tripEachDayTimingsArray.add(dayTiming)
    }
    tripData.add("tripEachDayTimings", tripEachDayTimingsArray)

    val tripPlacesWannaVisitArray = JsonArray()
    tripPlacesWannaVisit.forEach { place ->
        val placeObject = JsonObject()
        placeObject.addProperty("landMarkId", place.placeId)
        placeObject.addProperty("landMarkLatitude", place.latitude)
        placeObject.addProperty("landMarkLongitude", place.longitude)
        placeObject.addProperty("landMarkName", place.placeName)

        val placePhotosArray = JsonArray()
        place.placePhotos?.forEach { photo ->
            placePhotosArray.add(photo)
        }
        placeObject.add("landMarkPhotos", placePhotosArray)

        tripPlacesWannaVisitArray.add(placeObject)
    }
    tripData.add("landMarks", tripPlacesWannaVisitArray)

    return tripData
}
