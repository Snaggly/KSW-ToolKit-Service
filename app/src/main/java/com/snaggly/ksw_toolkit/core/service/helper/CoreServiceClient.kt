package com.snaggly.ksw_toolkit.core.service.helper

import androidx.lifecycle.ViewModel
import com.snaggly.ksw_toolkit.core.service.CoreService

abstract class CoreServiceClient : ViewModel() {
    companion object {
        var coreService: CoreService? = null
    }

    val coreService: CoreService?
        get() = CoreServiceClient.coreService
}