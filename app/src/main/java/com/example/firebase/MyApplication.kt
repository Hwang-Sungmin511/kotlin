package com.example.firebase

import android.app.Application
import android.util.Log

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("hsm511", "Application -> onCreate")
    }
}