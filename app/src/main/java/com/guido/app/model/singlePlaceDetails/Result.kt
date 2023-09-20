package com.guido.app.model.singlePlaceDetails

import com.google.android.gms.maps.model.LatLng
import com.guido.app.model.places.Photo
import com.guido.app.model.placesUiModel.PlaceUiModel

data class SinglePlaceResult(
    val address_components: List<AddressComponent>?,
    val adr_address: String?,
    val business_status: String?,
    val current_opening_hours: CurrentOpeningHours?,
    val formatted_address: String?,
    val formatted_phone_number: String?,
    val geometry: Geometry?,
    val icon: String?,
    val icon_background_color: String?,
    val icon_mask_base_uri: String?,
    val international_phone_number: String?,
    val name: String?,
    val opening_hours: OpeningHours?,
    val photos: List<Photo>?,
    val place_id: String?,
    val plus_code: PlusCode?,
    val rating: Double?,
    val reference: String?,
    val reviews: List<Review>?,
    val types: List<String>?,
    val url: String?,
    val user_ratings_total: Int?,
    val utc_offset: Int?,
    val vicinity: String?,
    val website: String?,
    val wheelchair_accessible_entrance: Boolean?
)


fun SinglePlaceResult.toPlaceUiModel() =  PlaceUiModel(
    placeId = place_id,
    name = name,
    latLng = LatLng(geometry?.location?.lat ?: 0.0,geometry?.location?.lng ?: 0.0),
    address = formatted_address,
    icon = icon,
    iconBackGroundColor = icon_background_color,
    photos = photos,
    rating = rating,
    callNumber = international_phone_number,
    website = website,
    isOpenNow = current_opening_hours?.open_now == true,
    reviews = reviews?.toUiModel()
)