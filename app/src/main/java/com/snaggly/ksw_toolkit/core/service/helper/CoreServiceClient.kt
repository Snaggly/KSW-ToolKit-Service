package com.snaggly.ksw_toolkit.core.service.helper

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.snaggly.ksw_toolkit.core.service.CoreService

abstract class CoreServiceClient(application: Application) : AndroidViewModel(application) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var coreService: CoreService? = null
    }

    val coreService: CoreService?
        get() = CoreServiceClient.coreService
}