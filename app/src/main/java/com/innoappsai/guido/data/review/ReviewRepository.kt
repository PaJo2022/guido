package com.innoappsai.guido.data.review

import com.innoappsai.guido.model.review.Review
import com.innoappsai.guido.model.review.ReviewRequestDTO

interface ReviewRepository {
    suspend fun addReview(reviewRequestDTO: ReviewRequestDTO): Review?
    suspend fun getReviewByPlaceId(placeId: String): List<Review>
}