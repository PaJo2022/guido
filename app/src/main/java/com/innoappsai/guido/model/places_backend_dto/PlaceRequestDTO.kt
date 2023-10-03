package com.innoappsai.guido.model.places_backend_dto

data class PlaceRequestDTO(
    val contactNumber: String?,
    val createdBy: String?,
    val location: PlaceRequestLocation?,
    var photos: List<String>?,
    var videos: List<String>?,
    val placeAddress: String?,
    val placeCity: String? = null,
    val placeState: String? = null,
    val placeCountry: String? = null,
    val placeDescription: String?,
    val pricingType: String?,
    val placeId: String?,
    val rating : Double?,
    val placeName: String?,
    val types: List<String>?,
    val website: String?
)

data class PlaceRequestLocation(
    val type: String = "Point",
    val coordinates: List<Double>?
)

