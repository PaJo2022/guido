package com.innoappsai.guido.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class PlaceTimings(
    val dayOfTheWeek: String,
    val openingHour: String,
    val closingHour: String
) : Parcelable
