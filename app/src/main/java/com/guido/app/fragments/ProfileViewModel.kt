package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.Constants.placeTypes
import com.guido.app.R
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.PlaceType
import com.guido.app.model.PlaceTypeContainer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val userRepository: UserRepository,
    private val appPrefs: AppPrefs
) :
    ViewModel() {

    var distanceProgress: Int = 5

    private var savedPlaceInterestsId = emptyList<String>()
    private val _formattedAddress: MutableLiveData<String> = MutableLiveData()
    val formattedAddress: LiveData<String> get() = _formattedAddress

    private val _newInterestsSelected: MutableLiveData<Boolean> = MutableLiveData()
    val newInterestsSelected: LiveData<Boolean> get() = _newInterestsSelected

    fun fetchCurrentAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(
                latLng,
                key
            )?.results?.firstOrNull()?.formatted_address.toString()
            _formattedAddress.postValue(address)
        }
    }





    val userInterestes : MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()

    private val _isPlaceInterestesSaved: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isPlaceInterestesSaved: SharedFlow<Unit> = _isPlaceInterestesSaved


    init {
        getSavedPlaceTypePreferences()
    }

        private fun getSavedPlaceTypePreferences(){
            viewModelScope.launch(Dispatchers.IO) {
                val preferences = placesRepository.getAllSavedPlaceTypePreferences()
                savedPlaceInterestsId = preferences.map { it.id }
                val job = async {
                    placeTypes.forEach { placetype->
                        placetype.isSelected = preferences.find { it.id == placetype.id} != null
                    }
                }
                job.await()
                val groupedPlaceTypes = placeTypes.groupBy { it.type }

                val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                    PlaceTypeContainer(
                        type,
                        placeTypeList,
                        placeTypeList.find { it.isSelected } != null)
                }
                userInterestes.postValue(placeTypeContainers)
            }
        }

    fun onPlaceInterestClicked(id : String){
        viewModelScope.launch(Dispatchers.IO) {
            val isSelected = placeTypes.find { it.id == id }?.isSelected ?: false
            placeTypes.find { it.id == id }?.isSelected = !isSelected
            val selectedInterestsId = placeTypes.filter { it.isSelected }.map { it.id }
            val set1 = selectedInterestsId.toSet()
            val set2 = savedPlaceInterestsId.toSet()
            val isNewPlaceInterestesSelected =
                set1.subtract(set2).isNotEmpty() || set2.subtract(set1).isNotEmpty()
            _newInterestsSelected.postValue((isNewPlaceInterestesSelected))
        }
    }

   fun savePlaceTypePreferences() {
        viewModelScope.launch {
            val allSelectedPlaceInterestes = placeTypes.filter { it.isSelected }
            placesRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
            appPrefs.prefDistance = distanceProgress
            _isPlaceInterestesSaved.emit(Unit)
        }
    }


    fun getUserData() = userRepository.getUserDetailsFlow(appPrefs.userId.toString())

    fun getSavedPreferences() = placesRepository.getAllSavedPlaceTypePreferencesFlow()
}