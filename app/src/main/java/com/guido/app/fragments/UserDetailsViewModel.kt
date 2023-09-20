package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user


    var isFromSignUpFlow = false
    private var _tempUser: User? = null

    private val _userLoginState: MutableSharedFlow<UserLoginState> = MutableSharedFlow()
    val userLoginState: SharedFlow<UserLoginState> get() = _userLoginState


    fun getUserDetailsByUserId(userId: String?) {
        if (userId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserDetails(userId)
            user?.let {
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

    fun createUser(userName: String, location: String) {
        if (_tempUser == null) return
        _tempUser!!.apply {
            this.location = location
            this.displayName = userName
        }
        viewModelScope.launch(Dispatchers.IO) {
            val isUserRegistered = authRepository.onRegister(_tempUser!!)
            if (!isUserRegistered) {
                _userLoginState.emit(UserLoginState.Error("Something Went Wrong"))
                return@launch
            }

            _userLoginState.emit(UserLoginState.UserSignedUp(_tempUser!!))
        }
    }

}