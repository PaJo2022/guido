package com.guido.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guido.app.model.PlaceAutocomplete
import com.guido.app.model.PlaceType
import com.guido.app.model.User

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