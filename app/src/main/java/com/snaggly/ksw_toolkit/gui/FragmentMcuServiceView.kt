package com.snaggly.ksw_toolkit.gui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.snaggly.ksw_toolkit.core.service.McuService

abstract class FragmentMcuServiceView(mcuServiceObserver: LiveData<McuService?>) : Fragment() {
    var mcuService: McuService? = null
}