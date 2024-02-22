package com.example.medicinestore.di

import com.example.medicinestore.util.ISharedPreferencesUtil
import com.example.medicinestore.util.SharePreferenceUtil
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module
@InstallIn(SingletonComponent::class)
interface OtherInterfacesModule {

    @Binds
    fun bindSharePreferencesUtil(sharePreferencesUtil: SharePreferenceUtil): ISharedPreferencesUtil
}