package com.innoappsai.guido.model.PlaceFilter

import com.innoappsai.guido.R

data class PlaceFilter(
    val leftIcon: Int? = null,
    val title: String? = null,
    val rightIcon: Int? = null,
    val placeFilterType: PlaceFilterType,
    var isSelected : Boolean = false
)

enum class PlaceFilterType {
    FULL_FILTER, UNLOCK_FILTERS, SORT, OPEN_NOW, MORE_FILTERS,TRAVEL_ITINERARY,HYPER_LOCAL_PLACE_SEARCH
}


val placeFiltersList = listOf(
    PlaceFilter(leftIcon = R.drawable.ic_filter, placeFilterType = PlaceFilterType.FULL_FILTER),
    PlaceFilter(leftIcon = R.drawable.ic_generate, title = "Generate Travel Itinerary", placeFilterType = PlaceFilterType.TRAVEL_ITINERARY),
    PlaceFilter(leftIcon = R.drawable.ic_generate, title = "Hyper Local Search", placeFilterType = PlaceFilterType.HYPER_LOCAL_PLACE_SEARCH),
    PlaceFilter(
        title = "Sort",
        rightIcon = R.drawable.ic_sort,
        placeFilterType = PlaceFilterType.SORT
    ),
    PlaceFilter(
        title = "Open Now",
        placeFilterType = PlaceFilterType.OPEN_NOW
    )
)
