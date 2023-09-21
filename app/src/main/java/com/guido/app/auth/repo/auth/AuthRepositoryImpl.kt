package com.guido.app.auth.repo.auth


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.guido.app.db.AppPrefs
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val fireStoreCollection: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val db : MyAppDataBase,
    private val appPrefs: AppPrefs
) : AuthRepository {
    override suspend fun onRegister(user: User): Boolean {
        return suspendCoroutine {
            fireStoreCollection.collection("users").document(user.id)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    it.resume(true)
                }
                .addOnFailureListener { e ->
                    it.resume(false)
                }
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUser? {
        return suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    appPrefs.userId = it.user?.uid
                    continuation.resume(it.user)
                }.addOnFailureListener { e ->
                    continuation.resume(null)
                }

        }
    }

    override suspend fun loginWithEmail(email: String, password: String): FirebaseUser? {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    appPrefs.userId = it.user?.uid
                    continuation.resume(it.user)
                }.addOnFailureListener { e ->
                    continuation.resume(null)
                }

        }
    }

    override fun onLogin(fbUserId: String): Flow<User?> {
        return callbackFlow {
            fireStoreCollection.collection("users").document(fbUserId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(null)
                        cancel()
                    }

                    if (value != null && value.exists()) {
                        val user = value.toObject<User>()
                        user?.let {
                            trySend(it)
                        } ?: kotlin.run {
                            trySend(null)
                        }
                    } else {
                        trySend(null)
                    }
                    close()
                }
            awaitClose {
                close()
            }
        }
    }

    override suspend fun onLogOut() {
        db.userDao().deleteUser()
        appPrefs.clear()
    }


}