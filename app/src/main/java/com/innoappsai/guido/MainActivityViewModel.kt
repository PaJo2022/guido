package com.innoappsai.guido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val placesRepository: PlacesRepository,private val appPrefs: AppPrefs,private val userRepository: UserRepository) :
    ViewModel() {

    fun setFcmKey(fcmKey : String){
        appPrefs.fcmKey = fcmKey
    }
    fun removeAllSavedPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.deleteAllPlacesFromDB()
        }
    }

    fun getUserData() = userRepository.getUserDetailsFlow()
}