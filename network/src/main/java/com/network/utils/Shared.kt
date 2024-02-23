package com.network.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.Date

class SharedPref(myApp: AppClass) {

    val PREFS_NAME = "TAIBAH_AI_PREFS"
    private var sharedPref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var value: String? = null

    fun clearAllPreferences() {
        editor = sharedPref!!.edit().clear()
        editor?.apply()
    }


    fun removeKey(key: String?) {
        editor!!.remove(key)
        editor!!.commit()
    }

    //for boolean values
    fun storeBoolean(key: String?, boolValue: Boolean?) {
        editor = sharedPref!!.edit()
        editor?.putBoolean(key, boolValue!!)
        editor?.apply()
    }

    fun getBooleanDefaultTrue(key: String?): Boolean {
        return sharedPref!!.getBoolean(key, true)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref!!.getBoolean(key, false)
    }


    //for int values
    fun storeInt(key: String?, value: Int): Int {
        editor = sharedPref!!.edit()
        editor?.putInt(key, value)
        editor?.apply()
        return value
    }

    fun getInt(key: String): Int {
        return sharedPref!!.getInt(key, 0)
    }

    fun storeString(key: String?, value: String?) {
        try {
            editor = sharedPref!!.edit()
            editor?.putString(key, value)
            editor?.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getString(key: String?, `val`: String?): String? {
        try {
            value = sharedPref!!.getString(key, `val`)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }

    fun storeLong(key: String?, value: Long) {
        try {
            editor = sharedPref!!.edit()
            editor?.putLong(key, value)
            editor?.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun storeDate(key: String?, dateValue: Date?) {
        if (dateValue != null) {
            val timestamp = dateValue.time
            editor = sharedPref!!.edit()
            editor?.putLong(key, timestamp)
            editor?.apply()
        }
    }

    fun getDate(key: String?, defaultDate: Date? = null): Date? {
        val timestamp = sharedPref!!.getLong(key, -1)
        return if (timestamp != -1L) Date(timestamp) else defaultDate
    }


    fun getLong(key: String?, i: Int): Long? {
        return sharedPref?.getLong(key, 0)
    }

    fun setIsArchived(isArchived: Boolean) {
        val editor = sharedPref?.edit()
        editor?.putBoolean("isArchived", isArchived)
        editor?.apply()
    }

    fun getIsArchived(): Boolean {
        return sharedPref?.getBoolean("isArchived", false) ?: false
    }

    //store object
    fun storeObject(key: String?, obj: Any?) {
        val json = Gson().toJson(obj)
        storeString(key, json)
    }

    //get object
    fun <T> getObject(key: String?, classOfT: Class<T>?): T? {
        val obj = getString(key, "")
        if (obj.equals("", ignoreCase = true)) return null
        val gson = Gson()
        return gson.fromJson(obj, classOfT)
    }

    init {
        try {
            sharedPref = myApp.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}