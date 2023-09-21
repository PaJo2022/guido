package com.guido.app.auth.repo.user

import android.util.Log
import androidx.room.withTransaction
import com.dhandadekho.mobile.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.guido.app.db.AppPrefs
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        db.withTransaction {
            db.userDao().apply {
                deleteUser()
                insertUser(user)
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

    override fun setProfilePicture(pictureUrl: String): Flow<Resource<String>> {
        return callbackFlow {
            fireStoreCollection.collection("users").document(appPrefs.userId.toString())
                .update("user_profile_picture", pictureUrl)
                .addOnSuccessListener { documentReference ->
                    trySend(Resource.Success("Success"))
                }
                .addOnFailureListener { e ->
                    trySend(Resource.Error(Exception(e.message), null))
                }
            awaitClose {
                close()
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