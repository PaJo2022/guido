package com.guido.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
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


    private val _userLoginState: MutableSharedFlow<UserLoginState> = MutableSharedFlow()
    val userLoginState: SharedFlow<UserLoginState> get() = _userLoginState


    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseUser = authRepository.signUpWithEmail(email, password)
            if (firebaseUser == null) {
                _userLoginState.emit(UserLoginState.Error("Something Went Wrong!"))
                return@launch
            }
            val isUserAllReadySignedUp =
                userRepository.getUserDetailsFromServer(firebaseUser.uid) != null
            if (isUserAllReadySignedUp) {
                _userLoginState.emit(UserLoginState.Error("This Email Is Allready Registered!"))
                return@launch
            }
            _userLoginState.emit(UserLoginState.UserCreateAccount(firebaseUser.toUserModel()))
        }
    }


}