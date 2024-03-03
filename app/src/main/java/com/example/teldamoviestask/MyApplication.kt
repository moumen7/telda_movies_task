package com.example.teldamoviestask

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}