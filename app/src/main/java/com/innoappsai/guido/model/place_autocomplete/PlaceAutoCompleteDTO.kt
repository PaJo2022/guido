package com.innoappsai.guido.model.place_autocomplete

import com.google.gson.annotations.SerializedName
import com.innoappsai.guido.model.PlaceAutocomplete

data class PlaceAutoCompleteDTO(
    @SerializedName("description")
    val name: String? = null,
    @SerializedName("place_id")
    val placeId: String? = null,
)

data class PlaceAutoCompleteLatLngDTO(
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun List<PlaceAutoCompleteDTO>.toUiModel() = map {
    PlaceAutocomplete(
        placeId = it.placeId.orEmpty(),
        area = it.name.orEmpty(),
        address = it.name.orEmpty(),
        latitude = 0.0,
        longitude = 0.0
    )
}