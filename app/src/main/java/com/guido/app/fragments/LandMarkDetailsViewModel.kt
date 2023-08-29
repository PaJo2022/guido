package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.Constants.GCP_API_KEY
import com.guido.app.data.videos.VideoRepository
import com.guido.app.log
import com.guido.app.model.PlaceDetailsUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandMarkDetailsViewModel @Inject constructor(private val videoRepository: VideoRepository) :
    ViewModel() {

    private var placesDetailsUiModel: PlaceDetailsUiModel? = null
    private val _landMarkData : MutableLiveData<PlaceDetailsUiModel?> = MutableLiveData()
    val landMarkData : LiveData<PlaceDetailsUiModel?> = _landMarkData

    init {
        placesDetailsUiModel = PlaceDetailsUiModel()
    }

    fun setPlaceData(placeUiModel: PlaceUiModel?) {
        placesDetailsUiModel?.placeUiModel = placeUiModel
    }

    private fun setPlaceVideoData(locationVideos: List<VideoUiModel>) {
        placesDetailsUiModel?.locationVideos = locationVideos
    }

    private suspend fun fetchVideosForTheLandMarkName(query: String): List<VideoUiModel> {
        return videoRepository.fetchPlacesVideos(
            query = query,
            apiKey = GCP_API_KEY
        )
    }

    fun fetchAllDataForTheLocation(query: String){
        viewModelScope.launch(Dispatchers.IO) {
            val landMarkVideoJob = async { fetchVideosForTheLandMarkName("${query} Tour Guide") }
            val landMarkVideo = landMarkVideoJob.await()
            setPlaceVideoData(landMarkVideo)
            placesDetailsUiModel?.log()
            _landMarkData.postValue(placesDetailsUiModel)
        }
    }

}