package com.guido.app.db

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AppPrefs(context: Context) {
    fun clear() {
        editor.clear().apply()
    }

    private val prefs: SharedPreferences
    private val editor: SharedPreferences.Editor

    var isUserLoggedIn: Boolean get() = prefs.getBoolean(PREF_IS_DEMO_LOGGED_IN, false)
        set(value) {
            editor.putBoolean(PREF_IS_DEMO_LOGGED_IN, value)
            editor.apply()
        }


    var prefDistance: Int get() = prefs.getInt(PREF_DISTANCE, 5) * 1000
        set(value) {
            editor.putInt(PREF_DISTANCE, value)
            editor.apply()
        }

    companion object {
        const val PREF_DISTANCE = "PREF_DISTANCE"
        const val PREF_IS_DEMO_LOGGED_IN = "PREF_IS_DEMO_LOGGED_IN"
        const val PREF_NAME = "PREF_NAME"

    }

    init {
        prefs = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
        editor = prefs.edit()

    }
}