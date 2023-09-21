package com.guido.app.auth.repo.user


import com.dhandadekho.mobile.utils.Resource
import com.guido.app.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserDetails(userId : String): User?

    suspend fun addUser(user: User)
    fun getUserDetailsFlow(userId : String): Flow<User?>
    suspend fun getUserDetailsFromServer(userId : String): User?
    fun setProfilePicture(pictureUrl: String) : Flow<Resource<String>>
    fun addNewBusiness(businessId : String): Flow<Resource<String>>
}