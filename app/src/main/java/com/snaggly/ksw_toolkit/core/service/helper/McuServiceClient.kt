package com.snaggly.ksw_toolkit.core.service.helper

import androidx.lifecycle.ViewModel
import com.snaggly.ksw_toolkit.core.service.McuService

abstract class McuServiceClient : ViewModel() {
    companion object {
        var mcuService: McuService? = null
    }

    val mcuService: McuService?
        get() = McuServiceClient.mcuService
}