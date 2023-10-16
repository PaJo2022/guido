package com.innoappsai.guido.model

enum class SortType {
    DISTANCE,
    MOST_POPULAR,
    HIGHEST_RATING,
    A_TO_Z,
    COST_LOW_TO_HIGH,
    COST_HIGH_TO_LOW,
    OPEN_NOW,
}

data class PlaceSortOption(val name: String, val type: SortType, var isChecked: Boolean = false)

val placeSortOptions = listOf(
    PlaceSortOption("Distance", SortType.DISTANCE),
    PlaceSortOption("Most popular", SortType.MOST_POPULAR),
    PlaceSortOption("Highest rating", SortType.HIGHEST_RATING),
    PlaceSortOption("A -> Z", SortType.A_TO_Z),
    PlaceSortOption("$ - $$$", SortType.COST_LOW_TO_HIGH),
    PlaceSortOption("$$$ - $", SortType.COST_HIGH_TO_LOW)
)
