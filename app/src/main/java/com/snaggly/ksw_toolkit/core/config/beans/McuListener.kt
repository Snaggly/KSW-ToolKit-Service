package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting

class McuListener(enableKsw: Boolean, configMaster: IConfigBean) {
    var enableKsw = BooleanSetting(enableKsw, configMaster)

    companion object {
        fun initMcuListener(configMaster: IConfigBean) : McuListener {
            return McuListener(enableKsw = true, configMaster)
        }
    }
}