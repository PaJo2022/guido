package com.innoappsai.guido.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.User

@Database(
    entities = [
        PlaceType::class,
        User::class,
        PlaceAutocomplete::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class MyAppDataBase : RoomDatabase() {
    abstract fun placeTypeDao(): PlaceTypeDao
    abstract fun locationSearchDao() : LocationSearchDao
    abstract fun userDao(): UserDao
}