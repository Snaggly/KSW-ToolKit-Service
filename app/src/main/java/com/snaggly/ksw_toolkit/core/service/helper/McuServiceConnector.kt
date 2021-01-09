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

class McuServiceConnector(private val context: Context) {
    private val mService = MutableLiveData<McuService?>()

    private val serviceConnector = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService.postValue((service as McuService.McuServiceBinder).getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService.postValue(null)
        }
    }

    fun connectToService() : LiveData<McuService?>{
        Intent(context, McuService::class.java).also { intent ->
            context.bindService(intent, serviceConnector, Context.BIND_AUTO_CREATE)
        }
        return mService
    }

    fun disconnectFromService() {
        context.unbindService(serviceConnector)
    }
}