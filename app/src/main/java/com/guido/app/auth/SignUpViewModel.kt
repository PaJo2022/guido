package com.guido.app.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.R
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.PlaceType
import com.guido.app.model.PlaceTypeContainer
import com.guido.app.model.User
import com.guido.app.model.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private var _tempUser: User? = null

    var isUserRegistered : Boolean = false

    private val placeTypes = listOf(
        PlaceType("1", "Airport", "Service", iconDrawable = R.drawable.ic_airport),
        PlaceType("6", "Bank", "Shop", iconDrawable = R.drawable.ic_bank),
        PlaceType("7", "Bar", "Shop", iconDrawable = R.drawable.icon_bar),
        PlaceType("8", "Beauty Salon", "Shop", iconDrawable = R.drawable.ic_beauty_saloon),
        PlaceType("11", "Cafe", "Shop", iconDrawable = R.drawable.ic_cafe_marker),
        PlaceType("18", "Clothing Store", "Shop", iconDrawable = R.drawable.icon_clothing_store),
        PlaceType("28", "Florist", "Shop", iconDrawable = R.drawable.icon_florist),
        PlaceType("31", "Gym", "Shop", iconDrawable = R.drawable.icon_gym),
        PlaceType("32", "Hair Care", "Shop", iconDrawable = R.drawable.icon_hair_care),
        PlaceType("38", "Jewelry Store", "Shop", iconDrawable = R.drawable.icon_jewelry),
        PlaceType("41", "Liquor Store", "Shop", iconDrawable = R.drawable.icon_liquor_store),
        PlaceType(
            "22",
            "Department Store",
            "Shop",
            iconDrawable = R.drawable.icon_department_store
        ),
        PlaceType("52", "Pharmacy", "Shop", iconDrawable = R.drawable.icon_pharmacy),
        PlaceType("57", "Restaurant", "Shop", iconDrawable = R.drawable.icon_restaurant),
        PlaceType("63", "Spa", "Shop", iconDrawable = R.drawable.ic_spa),
        PlaceType("67", "Supermarket", "Shop", iconDrawable = R.drawable.ic_super_market),
        PlaceType("61", "Shoe Store", "Shop", iconDrawable = R.drawable.ic_shoe_store),
        PlaceType("62", "Shopping Mall", "Shop", iconDrawable = R.drawable.ic_shopping_mall),


        PlaceType("16", "Church", "Service", iconDrawable = R.drawable.icon_church),
        PlaceType("17", "City Hall", "Service", iconDrawable = R.drawable.icon_city_hall),
        PlaceType("21", "Dentist", "Service", iconDrawable = R.drawable.icon_dentist),
        PlaceType("26", "Embassy", "Service", iconDrawable = R.drawable.icon_embassy),
        PlaceType("27", "Fire Station", "Service", iconDrawable = R.drawable.icon_fire_station),
        PlaceType("36", "Hospital", "Service", iconDrawable = R.drawable.icon_hospital),
        PlaceType("51", "Parking", "Service", iconDrawable = R.drawable.icon_parking),
        PlaceType("56", "Post Office", "Service", iconDrawable = R.drawable.icon_post_ofc),
        PlaceType(
            "58",
            "Roofing Contractor",
            "Service",
            iconDrawable = R.drawable.icon_roofing_contractor
        ),
        PlaceType("66", "Subway Station", "Service", iconDrawable = R.drawable.ic_subway),

        PlaceType("37", "Hotel", "Lodging", iconDrawable = R.drawable.icon_hotel),
        PlaceType("42", "Lodging", "Lodging", iconDrawable = R.drawable.icon_hotel),

        PlaceType(
            "46",
            "Movie Theater",
            "Entertainment",
            iconDrawable = R.drawable.icon_movie_theater
        ),
        PlaceType("47", "Museum", "Entertainment", iconDrawable = R.drawable.icon_museum),
        PlaceType("48", "Night Club", "Entertainment", iconDrawable = R.drawable.icon_night_club),
        PlaceType("12", "Campground", "Entertainment", iconDrawable = R.drawable.icon_campground),
        PlaceType(
            "2",
            "Amusement Park",
            "Entertainment",
            iconDrawable = R.drawable.ic_amusment_park
        )
    )

    val userInterestes : MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()


    init {
        getSavedPlaceTypePreferences()
    }


    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _userLoginState.postValue(UserLoginState.Loading)
            val firebaseUser = authRepository.signUpWithEmail(email, password)
            if (firebaseUser == null) {
                _userLoginState.postValue(UserLoginState.Error("Not Able To Sign You Up!"))
                return@launch
            }
            val isUserAllReadySignedUp =
                userRepository.getUserDetailsFromServer(firebaseUser.uid) != null
            if (isUserAllReadySignedUp) {
                _userLoginState.postValue(UserLoginState.Error("This Email Is Allready Registered!"))
                return@launch
            }
            _tempUser = firebaseUser.toUserModel()
            isUserRegistered = true
            _userLoginState.postValue(UserLoginState.UserCreateAccount(firebaseUser.toUserModel()))
        }
    }

    fun createUser(userName: String, location: String) {
        val allSelectedPlaceInterestes = placeTypes.find { it.isSelected }
        if(allSelectedPlaceInterestes == null){
            _userLoginState.value = UserLoginState.ProfileNotComplete("Please select Atleast 1 Interest")
            return
        }
        if (_tempUser == null) return
        val user = _tempUser!!
        val newUser = User(
            id = user.id,
            email = user.email,
            displayName = userName,
            location = location
        )
        viewModelScope.launch(Dispatchers.IO) {
            _userLoginState.postValue(UserLoginState.AccountCreateLoading)
            val isUserRegistered = authRepository.onRegister(newUser)
            if (!isUserRegistered) {
                _userLoginState.postValue(UserLoginState.Error("Something Went Wrong While Registering You"))
                return@launch
            }
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