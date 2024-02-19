package com.example.medicinestore

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MedicineStoreApplication:Application() {
    companion object{
        @JvmStatic
        fun getApplication(context: Context) = context.applicationContext as MedicineStoreApplication
    }
}