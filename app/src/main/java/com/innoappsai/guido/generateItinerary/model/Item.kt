package com.innoappsai.guido.generateItinerary.model

import com.innoappsai.guido.R
import java.util.UUID

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: Int,
    var isSelected: Boolean = false
)


val travelCompanionItemList = arrayListOf(
    Item(
        name = "Solo",
        icon = R.drawable.guido_icon,
    ), Item(
        name = "Couple",
        icon = R.drawable.guido_icon,
    ), Item(
        name = "Family",
        icon = R.drawable.guido_icon,
    ), Item(
        name = "Friends",
        icon = R.drawable.guido_icon,
    )
)

val travelBudgeItemList = arrayListOf(
    Item(
        name = "Economy",
        icon = R.drawable.guido_icon,
    ), Item(
        name = "Normal",
        icon = R.drawable.guido_icon,
    ), Item(
        name = "Luxury",
        icon = R.drawable.guido_icon,
    )
)
