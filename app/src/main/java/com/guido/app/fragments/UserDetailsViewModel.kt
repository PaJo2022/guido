package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.MyApp
import com.guido.app.auth.model.UserLoginState
import com.guido.app.auth.repo.auth.AuthRepository
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val db : MyAppDataBase
) :
    ViewModel() {

    private val _user: MutableLiveData<User?> = MutableLiveData()
    val user: LiveData<User?> get() = _user


    var isFromSignUpFlow = false
    private var _tempUser: User? = null

    private val _userLoginState: MutableSharedFlow<UserLoginState> = MutableSharedFlow()
    val userLoginState: SharedFlow<UserLoginState> get() = _userLoginState


    fun getUserDetailsByUserId(userId: String?) {
        if (userId == null) return
        viewModelScope.launch(Dispatchers.IO) {
             userRepository.getUserDetailsFlow(userId).collect{
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

    }

    fun signOut() {
       viewModelScope.launch(Dispatchers.IO) {
           authRepository.onLogOut()
       }
    }

}