package com.innoappsai.guido.auth.repo.user


import com.dhandadekho.mobile.utils.Resource
import com.innoappsai.guido.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserDetails(userId: String): User?

    suspend fun addUser(user: User)


    suspend fun updateProfileData(user: User): User?
    fun getUserDetailsFlow(): Flow<User?>
    suspend fun getUserDetailsFromServer(userId: String): User?
    suspend fun setProfilePicture(userId: String,pictureUrl: String): User?

}