package com.innoappsai.guido.generateItinerary.model.timelineTravelSpots

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TimeLineModel(
    var message: String,
    var date: String,
    var status: OrderStatus
) : Parcelable