package com.guido.app.fragments

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

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
            )
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
        val location = placeUiModel.address
        return videoRepository.fetchPlacesVideos(
            query = "${placeName},${location}",
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