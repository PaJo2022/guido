package com.innoappsai.guido.fragments

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.review.ReviewRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.VideoItem
import com.innoappsai.guido.model.VideoType
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
    private val appPrefs: AppPrefs,
    private val userRepository: UserRepository
) : ViewModel() {

    var placeId: String? = null

    private val _startAddReviewProcess: MutableSharedFlow<Pair<Array<String>,Array<String>>> = MutableSharedFlow()
    val startAddReviewProcess: SharedFlow<Pair<Array<String>,Array<String>>> = _startAddReviewProcess.asSharedFlow()

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: SharedFlow<Boolean> = _isLoading.asSharedFlow()

    private val _placeImages: MutableLiveData<ArrayList<Uri>> = MutableLiveData()
    val placeImages: LiveData<ArrayList<Uri>> = _placeImages

    private val _placeVideos: MutableLiveData<ArrayList<VideoItem>> = MutableLiveData()
    val placeVideos: LiveData<ArrayList<VideoItem>> = _placeVideos


    private val imageFileArrayList: ArrayList<Uri> = ArrayList()
    private val videoFileArrayList: ArrayList<Uri> = ArrayList()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun addNewTitle(reviewTitle: String, reviewDescription: String, rating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = appPrefs.userId
            if (placeId == null || userId == null) {

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
            MyApp.reviewRequestDTO = reviewRequest
            _startAddReviewProcess.emit(Pair(
                imageFileArrayList.map { it.toString() }.toTypedArray(),videoFileArrayList.map { it.toString() }.toTypedArray()
            ))
        }
    }

    fun addSingleImageFileToList(fileUri : Uri) {
        viewModelScope.launch {
            if (imageFileArrayList.size < 5) {
                imageFileArrayList.add(fileUri)
                _placeImages.postValue(imageFileArrayList)
            } else {
                _error.emit("Max 10 Images Can Be Uploaded")
            }
        }
    }

    fun addVideoUris(fileUri: List<Uri>) {
        viewModelScope.launch {
            if (videoFileArrayList.size <= 3 && fileUri.size <= 3) {
                videoFileArrayList.addAll(fileUri)
                _placeVideos.postValue(java.util.ArrayList(videoFileArrayList.map {
                    VideoItem(VideoType.OWN_VIDEO, it.toString())
                }))
            } else {
                _error.emit("Max 3 Videos Can Be Uploaded")
            }
        }
    }

    fun addImageFilesToList(fileUri: List<Uri>) {
        viewModelScope.launch {
            if (imageFileArrayList.size <= 5 && fileUri.size <= 5) {
                imageFileArrayList.addAll(fileUri)
                _placeImages.postValue(imageFileArrayList)
            } else {
                _error.emit("Max 5 Images Can Be Uploaded")
            }
        }
    }
}