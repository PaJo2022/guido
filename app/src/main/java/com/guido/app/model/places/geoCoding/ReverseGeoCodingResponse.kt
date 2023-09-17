package com.guido.app.model.places.geoCoding

data class ReverseGeoCodingResponse(
    val plus_code: PlusCode,
    val results: List<Result>,
    val status: String
)