package com.guido.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.db.AppPrefs
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appPrefs: AppPrefs,
    private val db: MyAppDataBase
) : ViewModel() {


    private val _userLoginState: MutableSharedFlow<UserLoginState> = MutableSharedFlow()
    val userLoginState: SharedFlow<UserLoginState> get() = _userLoginState



    fun searchUserElseAddUser(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val firebaseUser = authRepository.loginWithEmail(email, password)
            if (firebaseUser == null) {

                return@launch
            }
            val userId = firebaseUser.uid
            val userData = userRepository.getUserDetailsFromServer(userId)
            if (userData == null) {
                _userLoginState.emit(UserLoginState.UserCreateAccount(firebaseUser.toUserModel()))
            } else {
                db.userDao().apply {
                    deleteUser()
                    insertUser(userData)
                }
                appPrefs.isUserLoggedIn = true
                _userLoginState.emit(UserLoginState.UserLoggedIn(userData))
            }
        }
    }


}