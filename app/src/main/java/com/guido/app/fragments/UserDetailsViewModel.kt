package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhandadekho.mobile.utils.Resource
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.data.file.FileRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _userLoginState: MutableSharedFlow<UserLoginState> = MutableSharedFlow()
    val userLoginState: SharedFlow<UserLoginState> get() = _userLoginState

    private val _profilePicUrl: MutableLiveData<String?> = MutableLiveData()
    val profilePicUrl: LiveData<String?> = _profilePicUrl


    fun getUserDetailsByUserId(userId: String?) {
        if (userId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserDetailsFlow(userId).collect {
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
        fileRepository.addImagesForBusiness(fileArray).onEach { state ->
            when (state) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    val url = state.data.toString()
                    val isProfilePicSet = userRepository.setProfilePicture(url)
                    if (isProfilePicSet is Resource.Success) {
                        userRepository.updateProfilePicInLocalDb(appPrefs.userId.toString(), url)
                        _profilePicUrl.postValue(url)
                    } else {
                        _profilePicUrl.postValue(url)
                    }

                }

                else -> Unit
            }

        }.launchIn(viewModelScope)
    }

}