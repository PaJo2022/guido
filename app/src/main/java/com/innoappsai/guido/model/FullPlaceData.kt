package com.innoappsai.guido.model

import com.innoappsai.guido.model.places.geoCoding.ReverseGeoCodingDTO

data class FullPlaceData(
    val address: String?,
    val cityOrVillage: String?,
    val state: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?
)

fun ReverseGeoCodingDTO.toUiModel() = FullPlaceData(
    address = display_name,
    latitude = lat,
    longitude = lon,
    cityOrVillage = address.city ?: address.village,
    country = address.country,
    state = address.state
)