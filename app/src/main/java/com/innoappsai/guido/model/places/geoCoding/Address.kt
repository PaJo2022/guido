package com.innoappsai.guido.model.places.geoCoding

data class Address(
    val city: String?,
    val village: String?,
    val country: String?,
    val country_code: String?,
    val county: String?,
    val postcode: String?,
    val residential: String?,
    val state: String?,
    val state_district: String?,
    val suburb: String?
)