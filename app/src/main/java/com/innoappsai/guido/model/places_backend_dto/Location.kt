package com.innoappsai.guido.model.places_backend_dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Location(
    val latitude: Double?,
    val longitude: Double?
): Parcelable