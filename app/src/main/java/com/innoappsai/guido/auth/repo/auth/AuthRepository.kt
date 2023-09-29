package com.innoappsai.guido.auth.repo.auth


import com.google.firebase.auth.FirebaseUser
import com.innoappsai.guido.model.User

interface AuthRepository {
    suspend fun onRegister(user: User) : User?
    suspend fun signUpWithEmail(email : String,password : String) : FirebaseUser?
    suspend fun loginWithEmail(email : String,password : String) : FirebaseUser?
    suspend fun onLogin(fbUserId : String): User?
    suspend fun onLogOut()
}