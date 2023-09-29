package com.innoappsai.guido.auth.repo.user


import com.dhandadekho.mobile.utils.Resource
import com.innoappsai.guido.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserDetails(userId: String): User?

    suspend fun addUser(user: User)

    suspend fun updateProfilePicInLocalDb(userId: String, profilePic: String)
    suspend fun updateProfileData(userId: String,profileData: Map<String, String>): Resource<String>
    fun getUserDetailsFlow(userId: String): Flow<User?>
    suspend fun getUserDetailsFromServer(userId: String): User?
    suspend fun setProfilePicture(pictureUrl: String): Resource<String>
    fun addNewBusiness(businessId: String): Flow<Resource<String>>
}