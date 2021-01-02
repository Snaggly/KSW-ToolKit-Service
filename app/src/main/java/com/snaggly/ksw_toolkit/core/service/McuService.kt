package com.snaggly.ksw_toolkit.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class McuService : Service() {

    inner class McuServiceBinder : Binder() {
        fun getService(): McuService = this@McuService
    }

    private val binder = McuServiceBinder()
    var counter = 1

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Thread {
            while (true){
                Log.d("Snaggle McuService", "Is Running ${counter++}")
                Thread.sleep(1000)
            }
        }.start()
    }
}