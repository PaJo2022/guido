package com.innoappsai.guido.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.review.ReviewRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.review.ReviewRequestDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val appPrefs: AppPrefs,
    private val userRepository: UserRepository
) : ViewModel() {

    var placeId: String? = null

    private val _isReviewAdded: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isReviewAdded: SharedFlow<Boolean> = _isReviewAdded.asSharedFlow()

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: SharedFlow<Boolean> = _isLoading.asSharedFlow()

    fun addNewTitle(reviewTitle: String, reviewDescription: String, rating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = appPrefs.userId
            if (placeId == null || userId == null) {
                _isReviewAdded.emit(false)
                return@launch
            }

            val dbId = userRepository.getUserDetails(userId)?.dbId
            _isLoading.emit(true)
            val reviewRequest = ReviewRequestDTO(
                placeId = placeId,
                userId = dbId,
                rating = rating.toDouble(),
                reviewTitle = reviewTitle,
                reviewDescription = reviewDescription
            )
            val updatedPlace = reviewRepository.addReview(reviewRequest)

            _isReviewAdded.emit(updatedPlace != null)
            _isLoading.emit(false)
        }
    }

}