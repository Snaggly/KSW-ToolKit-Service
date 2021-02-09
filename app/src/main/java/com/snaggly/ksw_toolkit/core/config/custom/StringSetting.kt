package com.snaggly.ksw_toolkit.core.config.custom

import com.snaggly.ksw_toolkit.core.config.IConfigBean

class StringSetting(string: String) {
    var data = string
        set(value) {
            field = value
            IConfigBean.configManager?.saveConfig()
        }
}