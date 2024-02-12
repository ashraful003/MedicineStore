package com.example.medicinestore

import android.app.Application
import android.content.Context

class MedicineStoreApplication:Application() {
    companion object{
        fun getApplication(context: Context) = context.applicationContext as MedicineStoreApplication
    }
}