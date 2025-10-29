package com.app.mytasks

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 * MyTaskApp
 *
 * @author stephingeorge
 * @date 28/10/2025
 */
@HiltAndroidApp
class MyTaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this) // ✅ ensures Firebase is ready

    }


}