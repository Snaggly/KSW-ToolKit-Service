package com.snaggly.ksw_toolkit.core.config.custom

import com.snaggly.ksw_toolkit.core.config.IConfigBean

class IntegerSetting(value: Int) {
    var data = value
        set(value) {
            field = value
            IConfigBean.configManager?.saveConfig()
        }
}