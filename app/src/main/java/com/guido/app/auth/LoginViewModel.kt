package com.guido.app.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.db.AppPrefs
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.User
import com.guido.app.model.toUserModel
import com.guido.app.sign_in.SignInResult
import com.guido.app.sign_in.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appPrefs: AppPrefs,
    private val db: MyAppDataBase
) : ViewModel() {


    private val _userLoginState: MutableLiveData<UserLoginState> = MutableLiveData()
    val userLoginState: LiveData<UserLoginState> get() = _userLoginState

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: MutableSharedFlow<String> get() = _error

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: MutableSharedFlow<Boolean> get() = _isLoading


    fun searchUserElseAddUser(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val firebaseUser = authRepository.loginWithEmail(email, password)
            if (firebaseUser == null) {

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
            val userData = result.data?.toUserModel() ?: return@launch
            getUserData(userData)
        }
    }

    private fun getUserData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = user.id ?: return@launch
            val userData = authRepository.onLogin(userId)
            if (userData != null) {
                _isLoading.emit(false)
                userRepository.addUser(user)
                _userLoginState.postValue(UserLoginState.UserLoggedIn(user))
                return@launch
            }
            createUser(user)
        }
    }

    private fun createUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val isUserRegistered = authRepository.onRegister(user)
            if (!isUserRegistered) {
                _isLoading.emit(false)
                _error.emit("Something Went Wrong While Registering You!")
                return@launch
            }
            _isLoading.emit(false)
            userRepository.addUser(user)
            appPrefs.isUserLoggedIn = true
            _userLoginState.postValue(UserLoginState.UserSignedUp(user))
        }
    }


}