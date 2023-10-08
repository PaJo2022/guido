package com.innoappsai.guido.data.review

import androidx.room.withTransaction
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.review.Review
import com.innoappsai.guido.model.review.ReviewRequestDTO
import com.innoappsai.guido.model.review.toUiModel
import javax.inject.Inject


class ReviewRepositoryImpl @Inject constructor(
    private val guidoApi: GuidoApi,
    private val db: MyAppDataBase
) : ReviewRepository {
    override suspend fun addReview(reviewRequestDTO: ReviewRequestDTO): PlaceDTO? {
        val response = guidoApi.addReview(reviewRequestDTO)
        if (response.isSuccessful && response.body() != null) {
            val placeDTO = response.body()!!
            val placeId = reviewRequestDTO.placeId!!
            db.withTransaction {
                db.placeDao().apply {
                    deletePlaceById(placeId)
                    insertPlace(placeDTO)
                }
            }
        }
        return response.body()
    }

    override suspend fun getReviewByPlaceId(placeId: String): List<Review> {
        val response = guidoApi.fetchReviewByPlaceId(placeId)
        return response.body()?.map { it.toUiModel() } ?: emptyList()
    }

}