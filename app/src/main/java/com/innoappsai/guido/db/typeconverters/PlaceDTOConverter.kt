package com.innoappsai.guido.db.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.innoappsai.guido.model.PlaceTimings
import com.innoappsai.guido.model.places_backend_dto.Location


class PlaceDTOConverter {


    private val gson = Gson()

    @TypeConverter
    fun toLocation(json: String?): Location? {
        val type = object : TypeToken<Location>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromLocation(string: Location?): String {
        return gson.toJson(string)
    }

    @TypeConverter
    fun toPlaceTimings(json: String?):List<PlaceTimings>? {
        val type = object : TypeToken<List<PlaceTimings>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromPlaceTimings(string: List<PlaceTimings>?): String {
        return gson.toJson(string)
    }

    @TypeConverter
    fun toListOfString(json: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromListOfString(string: List<String>?): String {
        return gson.toJson(string)
    }
}

