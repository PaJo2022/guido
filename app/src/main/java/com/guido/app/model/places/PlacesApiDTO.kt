package com.guido.app.model.places

data class PlacesApiDTO(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<Result>?,
    val status: String
)