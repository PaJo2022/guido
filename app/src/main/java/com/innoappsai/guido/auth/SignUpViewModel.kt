package com.innoappsai.guido.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants.placeTypes
import com.innoappsai.guido.auth.model.UserLoginState
import com.innoappsai.guido.auth.repo.auth.AuthRepository
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.PlaceTypeContainer
import com.innoappsai.guido.model.User
import com.innoappsai.guido.model.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appPrefs: AppPrefs,
    private val placeRepository: PlacesRepository
) : ViewModel() {


    private val _userLoginState: MutableLiveData<UserLoginState> = MutableLiveData()
    val userLoginState: MutableLiveData<UserLoginState> get() = _userLoginState

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: MutableSharedFlow<String> get() = _error

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: MutableSharedFlow<Boolean> get() = _isLoading

    private var _tempUser: User? = null




    val userInterestes : MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()


    init {
        getSavedPlaceTypePreferences()
    }


    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val firebaseUser = authRepository.signUpWithEmail(email, password)
            if (firebaseUser == null) {
                _isLoading.emit(false)
                _error.emit("Not Able To Sign You Up!")
                return@launch
            }
            val isUserAllReadySignedUp =
                userRepository.getUserDetailsFromServer(firebaseUser.uid) != null
            if (isUserAllReadySignedUp) {
                _isLoading.emit(false)
                _error.emit("This Email Is Allready Registered!")
                return@launch
            }
            _isLoading.emit(false)
            _tempUser = firebaseUser.toUserModel()
            _userLoginState.postValue(UserLoginState.UserCreateAccount(firebaseUser.toUserModel()))
        }
    }

    fun createUser(userName: String, location: String) {
        if (_tempUser == null) return
        val user = _tempUser!!

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val allSelectedPlaceInterestes = placeTypes.filter { it.isSelected }
            if (allSelectedPlaceInterestes.isEmpty()) {
                _isLoading.emit(false)
                _error.emit("Please select Atleast 1 Interest!")
                return@launch
            }
            val newUser = User(
                id = user.id,
                email = user.email,
                displayName = userName,
                location = location
            )
            val isUserRegistered = authRepository.onRegister(newUser)
            if (!isUserRegistered) {
                _isLoading.emit(false)
                _error.emit("Something Went Wrong While Registering You!")
                return@launch
            }
            _isLoading.emit(false)
            userRepository.addUser(newUser)
            appPrefs.isUserLoggedIn = true
            savePlaceTypePreferences()
            _userLoginState.postValue(UserLoginState.UserSignedUp(newUser))
        }
    }



    private fun getSavedPlaceTypePreferences(){
        viewModelScope.launch(Dispatchers.IO) {
            val preferences = placeRepository.getAllSavedPlaceTypePreferences()
            val job = async {
                placeTypes.forEach { placetype->
                    placetype.isSelected = preferences.find { it.id == placetype.id} != null
                }
            }
            job.await()
            val groupedPlaceTypes = placeTypes.groupBy { it.type }

            // Map the grouped results into PlaceTypeContainer objects
            val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                PlaceTypeContainer(type, placeTypeList)
            }
            userInterestes.postValue(placeTypeContainers)
        }
    }

    fun onPlaceInterestClicked(id : String){
        viewModelScope.launch {
            val isSelected = placeTypes.find { it.id == id }?.isSelected ?: false
            placeTypes.find { it.id == id }?.isSelected = !isSelected
            savePlaceTypePreferences()
        }
    }

    private fun savePlaceTypePreferences() {
        viewModelScope.launch {
            val allSelectedPlaceInterestes = placeTypes.filter { it.isSelected }
            placeRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
            appPrefs.prefDistance = 5
        }
    }




}