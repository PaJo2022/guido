package com.innoappsai.guido.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.data.PlaceIdWithTimeDao
import com.innoappsai.guido.db.typeconverters.ItineraryModelTypeConverter
import com.innoappsai.guido.db.typeconverters.PlaceDTOConverter
import com.innoappsai.guido.generateItinerary.model.ItineraryDataForModel
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceIdWithName.PlaceWithIdAndTime
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.User
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO

@Database(
    entities = [
        PlaceType::class,
        User::class,
        PlaceAutocomplete::class,
        PlaceDTO::class,
        TravelItinerary::class,
        PlaceWithIdAndTime::class,
        ItineraryDataForModel::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(PlaceDTOConverter::class, ItineraryModelTypeConverter::class)
abstract class MyAppDataBase : RoomDatabase() {
    abstract fun placeTypeDao(): PlaceTypeDao
    abstract fun locationSearchDao() : LocationSearchDao
    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun itineraryAlarmDao(): ItineraryAlarmDao
    abstract fun placeIdWithTimeDao(): PlaceIdWithTimeDao
}