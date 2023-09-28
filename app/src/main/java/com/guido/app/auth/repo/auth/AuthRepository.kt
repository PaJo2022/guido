package com.guido.app.auth.repo.auth


import com.dhandadekho.mobile.utils.Resource
import com.google.firebase.auth.FirebaseUser
import com.guido.app.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun onRegister(user: User) : Boolean
    suspend fun signUpWithEmail(email : String,password : String) : FirebaseUser?
    suspend fun loginWithEmail(email : String,password : String) : FirebaseUser?
    suspend fun onLogin(fbUserId : String): User?
    suspend fun onLogOut()
}