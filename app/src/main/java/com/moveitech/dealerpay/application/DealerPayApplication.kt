package com.moveitech.dealerpay.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class DealerPayApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }


}