package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants.GCP_API_KEY
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.calculateDistance
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.data.review.ReviewRepository
import com.innoappsai.guido.data.tourData.ChatGptRepository
import com.innoappsai.guido.data.videos.VideoRepository
import com.innoappsai.guido.formatDouble
import com.innoappsai.guido.model.PlaceDetailsUiModel
import com.innoappsai.guido.model.VideoItem
import com.innoappsai.guido.model.VideoType
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.Message
import com.innoappsai.guido.model.placesUiModel.ExtraInfoWithIcon
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.review.Review
import com.innoappsai.guido.model.videosUiModel.VideoUiModel
import com.innoappsai.guido.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandMarkDetailsViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val tourDataRepository: ChatGptRepository,
    private val placeRepository: PlacesRepository,
    private val reviewRepository: ReviewRepository
) :
    ViewModel() {

    private var placesDetailsUiModel: PlaceDetailsUiModel? = null

    var callNumber: String? = null

    private val _videoLinkList: ArrayList<VideoItem> = ArrayList()

    private val _singlePlaceData: MutableLiveData<PlaceUiModel?> = MutableLiveData()
    val singlePlaceData: LiveData<PlaceUiModel?> = _singlePlaceData

    private val _placeMoreData: MutableLiveData<ArrayList<ExtraInfoWithIcon>> = MutableLiveData()
    val placeMoreData: LiveData<ArrayList<ExtraInfoWithIcon>> = _placeMoreData

    private val _landMarkVideoData: MutableLiveData<List<VideoItem>> = MutableLiveData()
    val landMarkVideoData: LiveData<List<VideoItem>> = _landMarkVideoData

    private val _landMarkTourDataData: MutableLiveData<String> = MutableLiveData()
    val landMarkTourDataData: LiveData<String> = _landMarkTourDataData

    private val _placeDistance: MutableLiveData<String> = MutableLiveData()
    val placeDistance: LiveData<String> = _placeDistance

    private val _placeReviews: MutableLiveData<List<Review>> = MutableLiveData()
    val placeReviews: LiveData<List<Review>> = _placeReviews


    private val _isPlaceDataFetching: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isPlaceDataFetching: SharedFlow<Boolean> = _isPlaceDataFetching.asSharedFlow()

    private val _isPlaceVideoFetching: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isPlaceVideoFetching: SharedFlow<Boolean> = _isPlaceVideoFetching.asSharedFlow()


    private val _isPlaceAIDataFetching: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isPlaceAIDataFetching: SharedFlow<Boolean> = _isPlaceAIDataFetching.asSharedFlow()

    init {
        placesDetailsUiModel = PlaceDetailsUiModel()
    }


    fun fetchDetailsById(placeId: String?) {
        if (placeId == null) {
            viewModelScope.launch {

            }
            return
        }
        placeRepository.fetchSinglePlacesDetails(
            placeId = placeId
        ).onEach {
           if(it is Resource.Success){
               it.data?.let {placeData->
                   getSinglePlaceDetails(placeData)
               }
           }
        }.launchIn(viewModelScope)
    }

    private suspend fun getSinglePlaceDetails(placeData: PlaceUiModel) {
        _isPlaceDataFetching.emit(true)
        _isPlaceAIDataFetching.emit(true)
        _isPlaceVideoFetching.emit(true)
        if (placeData.placeDescription.isNullOrEmpty()) {
            fetchTourDataForLandMark(placeData)
        }else{
            _isPlaceAIDataFetching.emit(false)
        }
        fetchVideosForTheLocation(placeData)
        _isPlaceVideoFetching.emit(false)



        getDistanceBetweenMyPlaceAndTheCurrentPlace(placeData)
        callNumber = placeData.callNumber
        _isPlaceDataFetching.emit(false)
        _singlePlaceData.postValue(placeData)

        // For Videos
        val videos = placeData.videos?.map {
            VideoItem(videoType = VideoType.OWN_VIDEO, it)
        } ?: emptyList()
        _videoLinkList.addAll(videos)
        _landMarkVideoData.postValue(_videoLinkList)


        // For Extra Information
        val extraInfoList = ArrayList<ExtraInfoWithIcon>()
        val hours = placeData.openTill
        val website = placeData.website
        val facebook = placeData.facebook
        val instagram = placeData.instagram
        val businessOwner = placeData.businessOwner
        val businessEmail = placeData.businessEmail
        if (!hours.isNullOrEmpty()) {
            extraInfoList.add(ExtraInfoWithIcon(R.drawable.ic_clock, "Hours", hours))
        }
        if (!website.isNullOrEmpty()) {
            extraInfoList.add(ExtraInfoWithIcon(R.drawable.ic_website_new, "Website", website))
        }
        if (!facebook.isNullOrEmpty()) {
            extraInfoList.add(ExtraInfoWithIcon(R.drawable.ic_facebook_new, "Facebook", facebook))
        }
        if (!instagram.isNullOrEmpty()) {
            extraInfoList.add(ExtraInfoWithIcon(R.drawable.ic_insta, "Instagram", instagram))
        }
        if (!businessOwner.isNullOrEmpty()) {
            extraInfoList.add(ExtraInfoWithIcon(R.drawable.ic_owner_new, "Owner/Manager", businessOwner))
        }
        if (!businessEmail.isNullOrEmpty()) {
            extraInfoList.add(ExtraInfoWithIcon(R.drawable.ic_email_new, "Email", businessEmail))
        }
        placeData.serverDbId?.let { fetchReviewsForThePlace(it) }
        _placeMoreData.postValue(extraInfoList)

    }

    private fun getDistanceBetweenMyPlaceAndTheCurrentPlace(placeUiModel: PlaceUiModel?){
        val placeLatLng = placeUiModel?.latLng ?: return

        val myPlaceLatLng = MyApp.userCurrentLatLng ?: return

        val totalDistance = calculateDistance(myPlaceLatLng.latitude,myPlaceLatLng.longitude,placeLatLng.latitude,placeLatLng.longitude) / 1000
        _placeDistance.postValue("${formatDouble(totalDistance)} Km")
    }





    private suspend fun fetchVideosForTheLandMarkName(placeUiModel: PlaceUiModel): List<VideoUiModel> {
        val placeName = placeUiModel.name
        val city = placeUiModel.city
        val state = placeUiModel.state
        val country = placeUiModel.country
        return videoRepository.fetchPlacesVideos(
            query = "${placeName},${city},${state},${country}",
            apiKey = GCP_API_KEY
        )
    }

    private fun fetchTourDataForLandMark(placeUiModel: PlaceUiModel) {
       viewModelScope.launch(Dispatchers.IO) {
           val message =
               "I want you to act as a place owner and generate a compelling description for my business. Here are the details you can use:\n" +
                       "\n" +
                       "Place Name: ${placeUiModel.name}\n" +
                       "Street Address: ${placeUiModel.address}\n" +
                       "City: ${placeUiModel.city}\n" +
                       "State: ${placeUiModel.state}\n" +
                       "Country: ${placeUiModel.country}\n" +
                       "Contact Number: ${placeUiModel.callNumber}\n" +
                       "Website: ${placeUiModel.website}\n" +
                       "Instagram: ${placeUiModel.instagram}\n" +
                       "Facebook: ${placeUiModel.facebook}\n" +
                       "Business Email: ${placeUiModel.businessEmail}\n" +
                       "Business Owner: ${placeUiModel.name}\n" +
                       "Timings: ${placeUiModel.placeTimings}"
           val data = tourDataRepository.getTourDataAboutTheLandMark(
               chatGptRequest =  ChatGptRequest(
                   listOf(Message(message, "user"))
               )
           )
           _landMarkTourDataData.postValue(
               data ?: "No Details Found"
           )
           _isPlaceAIDataFetching.emit(false)
       }
    }




    private fun fetchVideosForTheLocation(placeUiModel: PlaceUiModel) {

        viewModelScope.launch(Dispatchers.IO) {
            val landMarkVideoJob = async { fetchVideosForTheLandMarkName(placeUiModel) }
            val landMarkVideo = landMarkVideoJob.await()
            _videoLinkList.addAll(landMarkVideo.map {
                VideoItem(videoType = VideoType.YOUTUBE_VIDEO, it.videoUrl.toString())
            })
            _landMarkVideoData.postValue(_videoLinkList)
        }
    }

    private fun fetchReviewsForThePlace(placeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val reviews = reviewRepository.getReviewByPlaceId(placeId)
            _placeReviews.postValue(reviews)
        }
    }


}