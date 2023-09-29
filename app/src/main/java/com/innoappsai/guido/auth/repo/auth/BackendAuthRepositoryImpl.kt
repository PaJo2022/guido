package com.innoappsai.guido.auth.repo.auth


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.innoappsai.guido.api.UserApi
import com.innoappsai.guido.auth.repo.user.safeResume
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.User
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class BackendAuthRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val firebaseAuth: FirebaseAuth,
    private val db: MyAppDataBase,
    private val appPrefs: AppPrefs
) : AuthRepository {
    override suspend fun onRegister(user: User): User? {
        val apiResponse = userApi.registerUser(user)
        return if (apiResponse.isSuccessful && apiResponse.body() != null) {
            apiResponse.body()
        } else {
            null
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUser? {
        return suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    appPrefs.userId = it.user?.uid
                    continuation.safeResume(it.user)
                }.addOnFailureListener { e ->
                    continuation.safeResume(null)
                }

        }
    }

    override suspend fun loginWithEmail(email: String, password: String): FirebaseUser? {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    appPrefs.userId = it.user?.uid
                    continuation.safeResume(it.user)
                }.addOnFailureListener { e ->
                    continuation.safeResume(null)
                }

        }
    }

    override suspend fun onLogin(fbUserId: String): User? {
        val apiResponse = userApi.getUserById(fbUserId.trim())
        return if (apiResponse.isSuccessful && apiResponse.body() != null) {
            apiResponse.body()
        } else {
            null
        }
    }

    override suspend fun onLogOut() {
        db.clearAllTables()
        appPrefs.clear()
    }

}