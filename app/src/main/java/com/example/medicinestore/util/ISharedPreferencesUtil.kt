package com.example.medicinestore.util

interface ISharedPreferencesUtil {
    fun logout()
    fun getAuthToken():String?
    fun setAuthToken(token:String)
}