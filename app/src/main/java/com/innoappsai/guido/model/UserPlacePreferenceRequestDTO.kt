package com.innoappsai.guido.model

data class UserPlacePreferenceRequestDTO(
    val placePreferences : List<String> ?= null,
    val placePreferenceDistance : Int ?= null,
)
