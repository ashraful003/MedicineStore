package com.example.medicinestore.util

interface ISharedPreferencesUtil {
    fun logout()
    fun gerAuthToken():String?
    fun setAuthToken(token:String)
}