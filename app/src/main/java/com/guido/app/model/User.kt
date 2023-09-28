package com.guido.app.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize

@Keep
@Entity(tableName = "USER_TABLE")
@Parcelize
data class User@JvmOverloads constructor(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null,
    val location: String? = null,
    val profilePicture: String? = null,
    val isUserPremium: Boolean = false,
    val email: String? = null
) : Parcelable

fun FirebaseUser.toUserModel() = User(
    id = uid,
    email = email
)