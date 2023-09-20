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
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    var firstName: String? = null,
    var lastName: String? = null,
    var displayName: String? = null,
    var location: String? = null,
    var subscriptions: ArrayList<String>? = null,
    var profilePicture: String? = null,
    var isUserPremium: Boolean = false,
    var email: String? = null
) : Parcelable {
    // Default (no-argument) constructor
    constructor() : this("")
}

fun FirebaseUser.toUserModel() = User(
    id = uid,
    email = email
)