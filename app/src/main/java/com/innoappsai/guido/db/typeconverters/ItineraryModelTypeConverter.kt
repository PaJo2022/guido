package com.innoappsai.guido.db.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData
import com.innoappsai.guido.model.places_backend_dto.Location

class ItineraryModelTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun toTripData(json: String?): TripData? {
        val type = object : TypeToken<TripData>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromTripData(string: TripData?): String {
        return gson.toJson(string)
    }


    @TypeConverter
    fun toTravelPlaces(json: String?): List<TravelPlace>? {
        val type = object : TypeToken<List<TravelPlace>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromTravelPlaces(string: TravelPlace?): String {
        return gson.toJson(string)
    }

    @TypeConverter
    fun toTravelItineraryData(json: String?): ItineraryModel? {
        val type = object : TypeToken<ItineraryModel>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromTravelItineraryData(string: ItineraryModel?): String {
        return gson.toJson(string)
    }


}