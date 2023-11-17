package com.innoappsai.guido.placeFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.PlaceFeature
import com.innoappsai.guido.model.PlaceTypeContainer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterActivityVideModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val userRepository: UserRepository,
    val appPrefs: AppPrefs
) : ViewModel() {


    private var distanceProgress: Int = 5

    private val _userInterestes: MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()
    val userInterestes: LiveData<List<PlaceTypeContainer>> = _userInterestes

    private val _isPlaceInterestesSaved: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isPlaceInterestesSaved: SharedFlow<Unit> = _isPlaceInterestesSaved

    private val _newInterestsSelected: MutableLiveData<Boolean> = MutableLiveData()
    val newInterestsSelected: LiveData<Boolean> get() = _newInterestsSelected

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: SharedFlow<Boolean> get() = _isLoading

    private var savedPlaceInterestsId = emptyList<String>()

    private val placeTypesList = ArrayList(Constants.placeTypes)

    private var isDistanceChange = false
    private var isPreferenceChanged = false


    //Place Features
    private val _placeFeaturesList = ArrayList(Constants.placeFeaturesList)

    private val _placeFeatures: MutableLiveData<List<PlaceFeature>> = MutableLiveData()
    val placeFeatures: LiveData<List<PlaceFeature>> = _placeFeatures

    init {
        _placeFeatures.value = _placeFeaturesList
        getSavedPlaceTypePreferences()
    }

    fun onDistanceChanged(distance: Int) {
        _newInterestsSelected.value =
            appPrefs.prefDistance != distance * 1000 || isPreferenceChanged
        isDistanceChange = appPrefs.prefDistance != distance * 1000
        distanceProgress = distance
    }


    fun getSavedPlaceTypePreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val preferences = placesRepository.getAllSavedPlaceTypePreferences()
            savedPlaceInterestsId = preferences.map { it.id }
            val job = async {
                placeTypesList.forEach { placetype ->
                    placetype.isSelected = preferences.find { it.id == placetype.id } != null
                }
            }
            job.await()
            val groupedPlaceTypes = placeTypesList.groupBy { it.type }

            val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                PlaceTypeContainer(
                    type,
                    placeTypeList,
                    placeTypeList.find { it.isSelected } != null)
            }
            _userInterestes.postValue(placeTypeContainers)
            _newInterestsSelected.postValue(false)
            _isLoading.emit(false)
        }
    }

    fun onPlaceInterestClicked(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSelected = placeTypesList.find { it.id == id }?.isSelected ?: false
            placeTypesList.find { it.id == id }?.isSelected = !isSelected
            val selectedInterestsId = placeTypesList.filter { it.isSelected }.map { it.id }
            val set1 = selectedInterestsId.toSet()
            val set2 = savedPlaceInterestsId.toSet()
            val isNewPlaceInterestesSelected =
                set1.subtract(set2).isNotEmpty() || set2.subtract(set1).isNotEmpty()
            val groupedPlaceTypes = placeTypesList.groupBy { it.type }

            val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                PlaceTypeContainer(
                    type,
                    placeTypeList,
                    placeTypeList.find { it.isSelected } != null)
            }
            _userInterestes.postValue(placeTypeContainers)
            isPreferenceChanged = isNewPlaceInterestesSelected
            _newInterestsSelected.postValue((isNewPlaceInterestesSelected || isDistanceChange))
        }
    }

    fun savePlaceTypePreferences(isSavedEnabled: Boolean) {
        viewModelScope.launch {
            val allSelectedPlaceInterestes = placeTypesList.filter { it.isSelected }
            if (isSavedEnabled) {
                placesRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
                appPrefs.prefDistance = distanceProgress
                MyApp.tempPlaceDistance = null
                MyApp.tempPlaceDistance = null
            } else {
                MyApp.tempPlaceInterestes = allSelectedPlaceInterestes
                MyApp.tempPlaceDistance = distanceProgress * 1000
            }

            _isPlaceInterestesSaved.emit(Unit)
        }
    }


    //Place Features

    fun onPlaceFeatureClicked(placeFeature: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSelected =
                _placeFeaturesList.find { it.featureName == placeFeature }?.isSelected ?: false
            _placeFeaturesList.find { it.featureName == placeFeature }?.isSelected = !isSelected
            _placeFeatures.postValue(_placeFeaturesList)
        }
    }
}