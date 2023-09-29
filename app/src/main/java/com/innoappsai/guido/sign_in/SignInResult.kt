package com.innoappsai.guido.sign_in

import com.innoappsai.guido.model.User


data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val email : String?,
    val profilePictureUrl: String?
)

fun UserData.toUserModel() = User(
    id = userId,
    email = email,
    displayName = username,
    profilePicture = profilePictureUrl
)
