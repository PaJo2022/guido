package com.innoappsai.guido.auth.repo.user

import androidx.room.withTransaction
import com.dhandadekho.mobile.utils.Resource
import com.innoappsai.guido.api.UserApi
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class UserRepositoryImpl @Inject constructor(
    private val appPrefs: AppPrefs,
    private val userApi: UserApi,
    private val db: MyAppDataBase
) : UserRepository {
    override suspend fun getUserDetails(userId: String) = db.userDao().getUser(userId)
    override suspend fun addUser(user: User) {
        db.apply {
            withTransaction {
                userDao().deleteUser()
                userDao().insertUser(user)
            }
        }
    }


    override suspend fun updateProfileData(
        user: User
    ): User? {
        val response = userApi.updateUserData(user)
        return response.body()
    }

    override fun getUserDetailsFlow() = db.userDao().getUserFlow()
    override suspend fun getUserDetailsFromServer(userId: String): User? = userApi.getUserById(userId).body()
    override suspend fun setProfilePicture(userId: String, pictureUrl: String): User? {
        val response = userApi.setProfilePicture(userId,pictureUrl)
        return response.body()
    }


}

fun <T> Continuation<T>.safeResume(value: T) {
    try {
        this.resume(value)
    } catch (e: Exception) {
        null
    }
}