package com.guido.app.auth.model

import com.guido.app.model.User

sealed class UserLoginState{
    data class UserLoggedIn(val user: User) : UserLoginState()
    data class UserRegisterAccount(val user: User) : UserLoginState()
    data class UserCreateAccount(val user: User) : UserLoginState()
    data class UserSignedUp(val user: User) : UserLoginState()



}
