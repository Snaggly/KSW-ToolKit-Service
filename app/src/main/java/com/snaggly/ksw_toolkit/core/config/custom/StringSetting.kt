package com.snaggly.ksw_toolkit.core.config.custom

import com.snaggly.ksw_toolkit.core.config.IConfigBean

class StringSetting(string: String, private val parser: IConfigBean) {
    var data = string
        set(value) {
            field = value
            parser.saveConfig()
        }
}