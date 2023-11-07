package com.innoappsai.guido.db

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AppPrefs(context: Context) {
    fun clear() {
        editor.clear().apply()
    }

    private val prefs: SharedPreferences
    private val editor: SharedPreferences.Editor


    var fcmKey: String? get() = prefs.getString(PREF_FCM_KEY, null)
        set(value) {
            editor.putString(PREF_FCM_KEY, value)
            editor.apply()
        }
    var userId: String? get() = prefs.getString(PREF_USER_ID, null)
        set(value) {
            editor.putString(PREF_USER_ID, value)
            editor.apply()
        }

    var authToken: String? get() = prefs.getString(PREF_AUTH_TOKEN, null)
        set(value) {
            editor.putString(PREF_AUTH_TOKEN, value)
            editor.apply()
        }

    var isUserLoggedIn: Boolean
        get() = prefs.getBoolean(PREF_IS_DEMO_LOGGED_IN, false)
        set(value) {
            editor.putBoolean(PREF_IS_DEMO_LOGGED_IN, value)
            editor.apply()
        }


    var prefDistance: Int
        get() = prefs.getInt(PREF_DISTANCE, 5) * 1000
        set(value) {
            editor.putInt(PREF_DISTANCE, value)
            editor.apply()
        }

    var hyperLocalSearchPoolingTime: Long
        get() = prefs.getLong(PREF_POLLING_TIME, 1) // Retrieve and convert to minutes
        set(value) {
            editor.putLong(PREF_POLLING_TIME, value) // Store as milliseconds
            editor.apply()
        }

    var isTTSEnabled: Boolean
        get() = prefs.getBoolean(PREF_TTS, false)
        set(value) {
            editor.putBoolean(PREF_TTS, value)
            editor.apply()
        }

    companion object {
        const val PREF_DISTANCE = "PREF_DISTANCE"
        const val PREF_POLLING_TIME = "PREF_DPREF_POLLING_TIMEISTANCE"
        const val PREF_TTS = "PREF_TTS"
        const val PREF_USER_ID = "PREF_USER_ID"
        const val PREF_FCM_KEY = "PREF_FCM_KEY"
        const val PREF_AUTH_TOKEN = "PREF_AUTH_TOKEN"
        const val PREF_IS_DEMO_LOGGED_IN = "PREF_IS_DEMO_LOGGED_IN"
        const val PREF_NAME = "PREF_NAME"

    }

    init {
        prefs = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
        editor = prefs.edit()

    }
}