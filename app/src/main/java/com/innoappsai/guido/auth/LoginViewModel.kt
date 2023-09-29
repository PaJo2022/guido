package com.innoappsai.guido.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants
import com.innoappsai.guido.auth.model.UserLoginState
import com.innoappsai.guido.auth.repo.auth.AuthRepository
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.PlaceTypeContainer
import com.innoappsai.guido.model.User
import com.innoappsai.guido.model.toUserModel
import com.innoappsai.guido.sign_in.SignInResult
import com.innoappsai.guido.sign_in.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val placesRepository: PlacesRepository,
    private val appPrefs: AppPrefs,
    private val db: MyAppDataBase
) : ViewModel() {


    private val _userLoginState: MutableLiveData<UserLoginState> = MutableLiveData()
    val userLoginState: LiveData<UserLoginState> get() = _userLoginState

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: MutableSharedFlow<String> get() = _error

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: MutableSharedFlow<Boolean> get() = _isLoading


    val userInterestes : MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()

    private var _tempUser : User ?= null

    init {
        getSavedPlaceTypePreferences()
    }


    fun searchUserElseAddUser(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val firebaseUser = authRepository.loginWithEmail(email, password)
            if (firebaseUser == null) {
                _error.emit("Something Went Wrong")
                return@launch
            }
            val userId = firebaseUser.uid
            val userData = userRepository.getUserDetailsFromServer(userId)
            if (userData == null) {
                _userLoginState.postValue(UserLoginState.UserCreateAccount(firebaseUser.toUserModel()))
            } else {
                db.userDao().apply {
                    deleteUser()
                    insertUser(userData)
                }
                appPrefs.isUserLoggedIn = true
                _userLoginState.postValue(UserLoginState.UserLoggedIn(userData))
            }
        }
    }

    fun onSignInResult(result: SignInResult) {
        viewModelScope.launch(Dispatchers.IO) {
            _tempUser = result.data?.toUserModel() ?: return@launch
            checkIfNewUserOrElseLogin(_tempUser!!)
        }
    }

    private fun checkIfNewUserOrElseLogin(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = user.id ?: return@launch
            val userData = authRepository.onLogin(userId)
            if (userData != null) {
                _isLoading.emit(false)
                userRepository.addUser(userData)
                _userLoginState.postValue(UserLoginState.UserLoggedIn(user))
                return@launch
            }
            _userLoginState.postValue(UserLoginState.UserCreateAccount(user))

        }
    }

    fun createUser(userName: String, location: String) {
        if (_tempUser == null) return
        val user = _tempUser!!

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val allSelectedPlaceInterestes = Constants.placeTypes.filter { it.isSelected }
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
            val preferences = placesRepository.getAllSavedPlaceTypePreferences()
            val job = async {
                Constants.placeTypes.forEach { placetype->
                    placetype.isSelected = preferences.find { it.id == placetype.id} != null
                }
            }
            job.await()
            val groupedPlaceTypes = Constants.placeTypes.groupBy { it.type }

            // Map the grouped results into PlaceTypeContainer objects
            val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                PlaceTypeContainer(type, placeTypeList)
            }
            userInterestes.postValue(placeTypeContainers)
        }
    }

    fun onPlaceInterestClicked(id : String){
        viewModelScope.launch {
            val isSelected = Constants.placeTypes.find { it.id == id }?.isSelected ?: false
            Constants.placeTypes.find { it.id == id }?.isSelected = !isSelected
            savePlaceTypePreferences()
        }
    }

    private fun savePlaceTypePreferences() {
        viewModelScope.launch {
            val allSelectedPlaceInterestes = Constants.placeTypes.filter { it.isSelected }
            placesRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
            appPrefs.prefDistance = 5
        }
    }

    fun setLoading(isLoading: Boolean) {
        viewModelScope.launch {
            _isLoading.emit(isLoading)
        }
    }


}