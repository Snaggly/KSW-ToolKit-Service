package com.snaggly.ksw_toolkit.core.config

interface IConfigBean {
    fun saveConfig()
    fun readConfig()

    companion object {
        var configManager : IConfigBean? = null
    }
}