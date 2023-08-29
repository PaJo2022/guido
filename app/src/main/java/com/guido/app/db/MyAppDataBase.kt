package com.guido.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guido.app.model.PlaceType

@Database(
    entities = [
        PlaceType::class
    ],
    version = 1,
    exportSchema = false
)

abstract class MyAppDataBase : RoomDatabase() {
    abstract fun placeTypeDao() : PlaceTypeDao
}