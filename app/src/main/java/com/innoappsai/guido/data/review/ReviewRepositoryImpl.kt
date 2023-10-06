package com.innoappsai.guido.data.review

import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.model.review.Review
import com.innoappsai.guido.model.review.ReviewRequestDTO
import com.innoappsai.guido.model.review.toUiModel
import javax.inject.Inject


class ReviewRepositoryImpl @Inject constructor(
    private val guidoApi: GuidoApi
) : ReviewRepository {
    override suspend fun addReview(reviewRequestDTO: ReviewRequestDTO): Review? {
        val response = guidoApi.addReview(reviewRequestDTO)
        return response.body()?.toUiModel()
    }

    override suspend fun getReviewByPlaceId(placeId: String): List<Review> {
        val response = guidoApi.fetchReviewByPlaceId(placeId)
        return response.body()?.map { it.toUiModel() } ?: emptyList()
    }

}