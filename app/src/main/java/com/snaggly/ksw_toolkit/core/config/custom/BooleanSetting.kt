package com.snaggly.ksw_toolkit.core.config.custom

import com.snaggly.ksw_toolkit.core.config.IConfigBean

class BooleanSetting(value: Boolean, private val parser: IConfigBean) {
    var data = value
        set(value) {
            field = value
            parser.saveConfig()
        }
}