package com.innoappsai.guido.data.review

import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.review.Review
import com.innoappsai.guido.model.review.ReviewRequestDTO

interface ReviewRepository {
    suspend fun addReview(reviewRequestDTO: ReviewRequestDTO):  PlaceDTO?
    suspend fun getReviewByPlaceId(placeId: String): List<Review>
}