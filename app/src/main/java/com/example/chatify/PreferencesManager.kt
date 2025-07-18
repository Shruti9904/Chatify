package com.example.chatify

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key,value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key,value).apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
}

val IS_LOGGED_IN = "isLoggedIn"
val USER_ID = "userId"
val PHONE_NO = "phoneNo"