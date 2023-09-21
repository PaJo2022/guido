package com.guido.app.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.model.User
import com.guido.app.model.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository, private val userRepository: UserRepository
) : ViewModel() {


    private val _userLoginState: MutableLiveData<UserLoginState> = MutableLiveData()
    val userLoginState: MutableLiveData<UserLoginState> get() = _userLoginState

    private var _tempUser: User? = null


    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseUser = authRepository.signUpWithEmail(email, password)
            if (firebaseUser == null) {
                _userLoginState.postValue(UserLoginState.Error("Something Went Wrong!"))
                return@launch
            }
            val isUserAllReadySignedUp =
                userRepository.getUserDetailsFromServer(firebaseUser.uid) != null
            if (isUserAllReadySignedUp) {
                _userLoginState.postValue(UserLoginState.Error("This Email Is Allready Registered!"))
                return@launch
            }
            _tempUser = firebaseUser.toUserModel()
            _userLoginState.postValue(UserLoginState.UserCreateAccount(firebaseUser.toUserModel()))
        }
    }

    fun createUser(userName: String, location: String) {
        if (_tempUser == null) return
        val user = _tempUser!!
        val newUser = User(
            id = user.id,
            email = user.email,
            displayName = userName,
            location = location
        )
        viewModelScope.launch(Dispatchers.IO) {
            val isUserRegistered = authRepository.onRegister(newUser)
            if (!isUserRegistered) {
                _userLoginState.postValue(UserLoginState.Error("Something Went Wrong"))
                return@launch
            }
            userRepository.addUser(newUser)
            _userLoginState.postValue(UserLoginState.UserSignedUp(newUser))
        }
    }


}