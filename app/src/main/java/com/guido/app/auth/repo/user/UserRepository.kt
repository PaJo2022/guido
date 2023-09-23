package com.guido.app.auth.repo.user


import com.dhandadekho.mobile.utils.Resource
import com.guido.app.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserDetails(userId : String): User?

    suspend fun addUser(user: User)

    suspend fun updateProfilePicInLocalDb(userId: String,profilePic: String)
    fun getUserDetailsFlow(userId : String): Flow<User?>
    suspend fun getUserDetailsFromServer(userId: String): User?
    suspend fun setProfilePicture(pictureUrl: String): Resource<String>
    fun addNewBusiness(businessId: String): Flow<Resource<String>>
}