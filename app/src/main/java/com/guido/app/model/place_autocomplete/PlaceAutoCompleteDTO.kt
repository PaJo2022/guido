package com.guido.app.model.place_autocomplete

import com.guido.app.model.PlaceAutocomplete

data class PlaceAutoCompleteDTO(
    val address: Address?=null,
    val display_name: String?=null,
    val lat: Double?=null,
    val lon: Double?=null,
    val name: String?=null,
    val place_id: Int?=null,
)

fun List<PlaceAutoCompleteDTO>.toUiModel() = map {
    PlaceAutocomplete(
        placeId = it.place_id.toString(),
        area = it.name.toString(),
        address = it.display_name.toString(),
        latitude = it.lat ?: 0.0,
        longitude = it.lon ?: 0.0
    )
}