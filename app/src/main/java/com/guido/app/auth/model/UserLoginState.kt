package com.guido.app.auth.model

import com.guido.app.model.User

sealed class UserLoginState{
    data class UserLoggedIn(val user: User) : UserLoginState()
    data class UserCreateAccount(val user: User) : UserLoginState()
    data class UserSignedUp(val user: User) : UserLoginState()
    data class ProfileNotComplete(val message : String) : UserLoginState()
    data class Error(val message : String) : UserLoginState()
    object Loading : UserLoginState()
    object AccountCreateLoading : UserLoginState()
}
