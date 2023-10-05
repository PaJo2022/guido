package com.innoappsai.guido.model.places_backend_dto

import com.innoappsai.guido.model.PlaceTimings

data class PlaceRequestDTO(
    val contactNumber: String? = null,
    val createdBy: String? = null,
    val location: PlaceRequestLocation? = null,
    var photos: List<String>? = null,
    var videos: List<String>? = null,
    var placeMapImage: String? = null,
    val placeAddress: String? = null,
    val placeCity: String? = null,
    val placeState: String? = null,
    val placeCountry: String? = null,
    val placeDescription: String? = null,
    val pricingType: String? = null,
    val placeId: String? = null,
    val rating: Double? = null,
    val placeName: String? = null,
    val types: List<String>? = null,
    val website: String? = null,
    val instagram: String? = null,
    val facebook: String? = null,
    val businessEmail: String? = null,
    val businessOwner: String? = null,
    val businessSpecialNotes: String? = null,
    val placeFeatures: List<String>? = null,
    val placeTimings: List<PlaceTimings>? = null,

    )

data class PlaceRequestLocation(
    val type: String = "Point",
    val coordinates: List<Double>?
)

