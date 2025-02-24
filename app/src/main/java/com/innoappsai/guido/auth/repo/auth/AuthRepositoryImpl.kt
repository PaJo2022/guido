package com.innoappsai.guido.auth.repo.auth


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.innoappsai.guido.auth.repo.user.safeResume
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.User
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val fireStoreCollection: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val db : MyAppDataBase,
    private val appPrefs: AppPrefs
) : AuthRepository {
    override suspend fun onRegister(user: User): User? {
        return suspendCoroutine {
            fireStoreCollection.collection("users").document(user.id)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    it.safeResume(null)
                }
                .addOnFailureListener { e ->
                    it.safeResume(null)
                }
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
        appPrefs.userId = fbUserId
        return suspendCoroutine {continuation->
            fireStoreCollection.collection("users").document(fbUserId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        continuation.safeResume(null)
                    }else if (value != null && value.exists()) {
                        val user = value.toObject<User>()
                        continuation.safeResume(user)
                    } else {
                        continuation.safeResume(null)
                    }
                }
        }
    }

    override suspend fun onLogOut() {
        db.clearAllTables()
        appPrefs.clear()
    }


}