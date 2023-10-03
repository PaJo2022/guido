package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants.GCP_API_KEY
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.calculateDistance
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.data.tourData.TourDataRepository
import com.innoappsai.guido.data.videos.VideoRepository
import com.innoappsai.guido.formatDouble
import com.innoappsai.guido.model.PlaceDetailsUiModel
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.ChatGptResponse
import com.innoappsai.guido.model.chatGptModel.Message
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.videosUiModel.VideoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    var callNumber : String ?= null

    private val _singlePlaceData: MutableLiveData<PlaceUiModel?> = MutableLiveData()
    val singlePlaceData: LiveData<PlaceUiModel?> = _singlePlaceData

    private val _landMarkData: MutableLiveData<PlaceDetailsUiModel?> = MutableLiveData()
    val landMarkData: LiveData<PlaceDetailsUiModel?> = _landMarkData

    private val _landMarkTourDataData: MutableLiveData<String> = MutableLiveData()
    val landMarkTourDataData: LiveData<String> = _landMarkTourDataData

    private val _placeDistance: MutableLiveData<String> = MutableLiveData()
    val placeDistance: LiveData<String> = _placeDistance



    private val _isPlaceDataFetching: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isPlaceDataFetching: SharedFlow<Boolean> = _isPlaceDataFetching

    private val _isPlaceVideoFetching: MutableSharedFlow<Boolean> = MutableSharedFlow()


    private val _isPlaceAIDataFetching: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isPlaceAIDataFetching: SharedFlow<Boolean> = _isPlaceAIDataFetching

    init {
        placesDetailsUiModel = PlaceDetailsUiModel()


    }


    fun getSinglePlaceDetails(placeUiModel: PlaceUiModel?) {
        val placeId = placeUiModel?.placeId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _isPlaceDataFetching.emit(true)
            val placeData = placeRepository.fetchSinglePlacesDetails(
                placeId = placeId
            ) ?: return@launch
            fetchAllDataForTheLocation(placeData)
            callNumber = placeData?.callNumber
            _isPlaceDataFetching.emit(false)
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


    private suspend fun fetchVideosForTheLandMarkName(placeUiModel: PlaceUiModel): List<VideoUiModel> {
        val placeName = placeUiModel.name
        val city = placeUiModel.city
        val country = placeUiModel.country
        return videoRepository.fetchPlacesVideos(
            query = "${placeName},${city},${country}",
            apiKey = GCP_API_KEY
        )
    }

    private suspend fun fetchTourDataForLandMark(placeUiModel: PlaceUiModel): ChatGptResponse? {
        val placeName = placeUiModel.name
        val location = placeUiModel.address
        val message = "As a tour guide tell me about the place ${placeName} at ${location} with all relevant details and interesting and useful facts"
        return tourDataRepository.getTourDataAboutTheLandMark(
           ChatGptRequest(
               listOf(Message(message,"user"))
           )
        )
    }



    fun fetchAllDataForTheLocation(placeUiModel: PlaceUiModel){
        viewModelScope.launch(Dispatchers.IO) {
            _isPlaceAIDataFetching.emit(true)
            val tourDataJob = async { fetchTourDataForLandMark(placeUiModel) }
            val tourData = tourDataJob.await()
            _isPlaceAIDataFetching.emit(false)
            setTourDataData(tourData?.choices?.firstOrNull()?.message?.content.toString())
        }
        viewModelScope.launch(Dispatchers.IO) {
            _isPlaceVideoFetching.emit(true)
            val landMarkVideoJob = async { fetchVideosForTheLandMarkName(placeUiModel) }
            val landMarkVideo = landMarkVideoJob.await()
            _isPlaceVideoFetching.emit(false)
            setPlaceVideoData(landMarkVideo)
            _landMarkData.postValue(placesDetailsUiModel)
        }
    }

}