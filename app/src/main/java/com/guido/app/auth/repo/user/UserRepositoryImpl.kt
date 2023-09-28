package com.guido.app.auth.repo.user

import android.util.Log
import androidx.room.withTransaction
import com.dhandadekho.mobile.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.guido.app.db.AppPrefs
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val appPrefs: AppPrefs,
    private val fireStoreCollection: FirebaseFirestore,
    private val db: MyAppDataBase
) : UserRepository {
    override suspend fun getUserDetails(userId: String) = db.userDao().getUser(userId)
    override suspend fun addUser(user: User) {
        db.userDao().insertUser(user)
    }

    override suspend fun updateProfilePicInLocalDb(userId: String, profilePic: String) {
        withContext(Dispatchers.IO) {
            db.userDao().updateProfilePic(userId, profilePic)
        }
    }

    override suspend fun updateProfileData(
        userId : String,
        profileData: Map<String, String>
    ): Resource<String> {
        return suspendCoroutine { continuation ->
            fireStoreCollection.collection("users").document(userId)
                .update(profileData) // Pass the map of updates here
                .addOnSuccessListener { documentReference ->
                    continuation.resume(Resource.Success("Updated"))
                }
                .addOnFailureListener { e ->
                    continuation.resume((Resource.Error(Exception(e.message), null)))
                }

        }
    }

    override fun getUserDetailsFlow(userId: String) = db.userDao().getUserFlow(userId)
    override suspend fun getUserDetailsFromServer(userId: String): User? {
        return suspendCoroutine { continuation ->
            val locationsRef = fireStoreCollection.collection("users").document(userId)
            locationsRef.addSnapshotListener { value, error ->
                if (error != null) {
                    continuation.safeResume(null)
                } else if (value != null) {
                    val user = value.toObject(User::class.java)
                    continuation.safeResume(user)
                } else {
                    continuation.safeResume(null)
                }

            }

        }
    }

    override suspend fun setProfilePicture(pictureUrl: String): Resource<String> {
        return suspendCoroutine {continuation->
            fireStoreCollection.collection("users").document(appPrefs.userId.toString())
                .update("profilePicture", pictureUrl)
                .addOnSuccessListener { documentReference ->
                    continuation.resume(Resource.Success("Success"))
                }
                .addOnFailureListener { e ->
                    continuation.resume(Resource.Error(Exception(e.message), null))
                }

        }
    }

    override fun addNewBusiness(businessId: String): Flow<Resource<String>> {
        return callbackFlow {
            fireStoreCollection.collection("user_business").document(appPrefs.userId.toString())
                .set(businessId)
                .addOnSuccessListener { documentReference ->

                    trySend(Resource.Success("Registered"))
                }
                .addOnFailureListener { e ->
                    Log.i("JAPAN", "${e.message}")
                    trySend(Resource.Error(Exception(e.message), null))
                }
            awaitClose {
                close()
            }
        }
    }
}

fun <T> Continuation<T>.safeResume(value: T) {
    try {
        this.resume(value)
    } catch (e: Exception) {
        null
    }
}