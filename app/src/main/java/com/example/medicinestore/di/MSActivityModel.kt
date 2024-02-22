package com.example.medicinestore.di

import android.app.Activity
import com.example.medicinestore.util.MSActivityUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object MyGasBdActivityModule {

    @Provides
    fun providedMGBActivityUtil(activity: Activity): MSActivityUtil {
        return MSActivityUtil(activity as MSActivityUtil.ActivityListener)
    }
}