package com.guido.app.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.Constants.GCP_API_KEY
import com.guido.app.MyApp
import com.guido.app.calculateDistance
import com.guido.app.data.places.PlacesRepository
import com.guido.app.data.tourData.TourDataRepository
import com.guido.app.data.videos.VideoRepository
import com.guido.app.formatDouble
import com.guido.app.model.PlaceDetailsUiModel
import com.guido.app.model.chatGptModel.ChatGptRequest
import com.guido.app.model.chatGptModel.ChatGptResponse
import com.guido.app.model.chatGptModel.Message
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandMarkDetailsViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val tourDataRepository: TourDataRepository,
    private val placeRepository: PlacesRepository
) :
    ViewModel() {

    private var placesDetailsUiModel: PlaceDetailsUiModel? = null

    private val _singlePlaceData: MutableLiveData<PlaceUiModel?> = MutableLiveData()
    val singlePlaceData: LiveData<PlaceUiModel?> = _singlePlaceData

    private val _landMarkData: MutableLiveData<PlaceDetailsUiModel?> = MutableLiveData()
    val landMarkData: LiveData<PlaceDetailsUiModel?> = _landMarkData

    private val _landMarkTourDataData: MutableLiveData<String> = MutableLiveData()
    val landMarkTourDataData: LiveData<String> = _landMarkTourDataData

    private val _placeDistance: MutableLiveData<String> = MutableLiveData()
    val placeDistance: LiveData<String> = _placeDistance

    init {
        placesDetailsUiModel = PlaceDetailsUiModel()
    }

    fun getSinglePlaceDetails(placeUiModel: PlaceUiModel?) {
        val placeId = placeUiModel?.placeId ?: return
        viewModelScope.launch {
            val placeData = placeRepository.fetchSinglePlacesDetails(
                placeId = placeId,
                key = GCP_API_KEY
            )
            _singlePlaceData.postValue(placeData)
        }

    }

    fun getDistanceBetweenMyPlaceAndTheCurrentPlace(placeUiModel: PlaceUiModel?){
        val placeLatLng = placeUiModel?.latLng ?: return

        val myPlaceLatLng = MyApp.userCurrentLatLng ?: return

        val totalDistance = calculateDistance(myPlaceLatLng.latitude,myPlaceLatLng.longitude,placeLatLng.latitude,placeLatLng.longitude) / 1000
        _placeDistance.value = "${formatDouble(totalDistance)} Km"
    }

    private fun setPlaceVideoData(locationVideos: List<VideoUiModel>) {
        placesDetailsUiModel?.locationVideos = locationVideos
    }

    private fun setTourDataData(tourDataInHtml: String) {
        _landMarkTourDataData.postValue(tourDataInHtml)
    }


    private suspend fun fetchVideosForTheLandMarkName(query: String): List<VideoUiModel> {
        return videoRepository.fetchPlacesVideos(
            query = query,
            apiKey = GCP_API_KEY
        )
    }

    private suspend fun fetchTourDataForLandMark(landMarkName: String): ChatGptResponse? {
        val message = "as a Tour guide tell me how can i plan my visit at ${landMarkName}"
        return tourDataRepository.getTourDataAboutTheLandMark(
           ChatGptRequest(
               listOf(Message(message,"user"))
           )
        )
    }



    fun fetchAllDataForTheLocation(query: String){
        viewModelScope.launch(Dispatchers.IO) {
            val tourDataJob = async { fetchTourDataForLandMark(query) }
            val tourData = tourDataJob.await()
            Log.i("KOREA", "fetchAllDataForTheLocation: ${tourData}")
            setTourDataData(tourData?.choices?.firstOrNull()?.message?.content.toString())
        }
        viewModelScope.launch(Dispatchers.IO) {
            val landMarkVideoJob = async { fetchVideosForTheLandMarkName("${query} Tour Guide") }
            val landMarkVideo = landMarkVideoJob.await()
            setPlaceVideoData(landMarkVideo)
            _landMarkData.postValue(placesDetailsUiModel)
        }
    }

}