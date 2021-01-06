package com.snaggly.ksw_toolkit.core.service.helper

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.snaggly.ksw_toolkit.core.service.McuService

class McuServiceConnector(private val context: Context, private val activity: Activity) {
    private val mService = MutableLiveData<McuService?>()

    fun getService(): LiveData<McuService?> {
        return mService
    }

    private val serviceConnector = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService.postValue((service as McuService.McuServiceBinder).getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService.postValue(null)
        }
    }

    fun connectToService() {
        if (McuService.bindable) {
            Intent(context, McuService::class.java).also { intent ->
                activity.bindService(intent, serviceConnector, Context.BIND_AUTO_CREATE)
            }
        }
    }
}