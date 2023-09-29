package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhandadekho.mobile.utils.Resource
import com.innoappsai.guido.Constants.USER_FILE_FOLDER
import com.innoappsai.guido.auth.repo.auth.AuthRepository
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.file.FileRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository,
    private val appPrefs: AppPrefs
) :
    ViewModel() {

    private val _user: MutableLiveData<User?> = MutableLiveData()
    val user: LiveData<User?> get() = _user


    var isFromSignUpFlow = false
    private var _tempUser: User? = null

    private val _profileEditState: MutableLiveData<ProfileEditingState> =
        MutableLiveData(ProfileEditingState.IDLE)
    val profileEditState: MutableLiveData<ProfileEditingState> get() = _profileEditState

    private val _isProfilePicUpdating: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isProfilePicUpdating: SharedFlow<Boolean> get() = _isProfilePicUpdating


    private val _isProfileUpdating: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isProfileUpdating: SharedFlow<Boolean> get() = _isProfileUpdating

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> get() = _error


    private val _profilePicUrl: MutableLiveData<String?> = MutableLiveData()
    val profilePicUrl: LiveData<String?> = _profilePicUrl


    fun getUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserDetailsFlow().collect {
                _tempUser = it
                _user.postValue(it)
            }
        }
    }

    fun getTempUser(tempUser: User?) {
        tempUser.let {
            _user.value = it
            _tempUser = it
        }
    }


    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.onLogOut()
        }
    }

    fun addFile(fileArray: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            _isProfilePicUpdating.emit(true)
            val state = fileRepository.storeImageToServer(fileArray,USER_FILE_FOLDER)
            if (state is Resource.Success) {
                val url = state.data.toString()
                updateProfilePicture(url)
            }
            _isProfilePicUpdating.emit(false)
        }
    }

    private fun updateProfilePicture(picUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = appPrefs.userId ?: return@launch
            _isProfileUpdating.emit(true)
            val updatedUserData = userRepository.setProfilePicture(userId, picUrl)
            if (updatedUserData != null) {
                _error.emit("Profile Updated")
                userRepository.addUser(updatedUserData)
                _profilePicUrl.postValue(picUrl)
            } else {
                _profilePicUrl.postValue(picUrl)
                _error.emit("Something Went Wrong")
            }
            _isProfileUpdating.emit(false)
        }
    }

    fun updateUserData(updatedUserName: String, updatedUserLocation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedUser = _tempUser?.let {
                User(
                    id = it.id,
                    email = it.email,
                    displayName = updatedUserName,
                    location = updatedUserLocation
                )
            } ?: kotlin.run {
                _error.emit("Something Went Wrong")
                return@launch
            }

            _isProfileUpdating.emit(true)
            val updatedUserData = userRepository.updateProfileData(updatedUser)
            if (updatedUserData != null) {
                userRepository.addUser(updatedUser)
                _error.emit("Profile Updated")
            } else {
                _error.emit("Something Went Wrong")
            }
            onProfileEditCanceled()
            _isProfileUpdating.emit(false)
        }
    }


    fun onProfileEditClicked() {
        _profileEditState.postValue(ProfileEditingState.EDITING)
    }

    fun onProfileEditCanceled() {
        _profileEditState.postValue(ProfileEditingState.IDLE)
    }

    enum class ProfileEditingState {
        IDLE, EDITING
    }

}