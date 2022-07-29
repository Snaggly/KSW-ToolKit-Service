package com.snaggly.ksw_toolkit.util.commander

import android.app.Instrumentation

object KeyInjector {
    private val instrumentation = Instrumentation()

    fun sendKey(keyCode: Int?) {
        if (keyCode != null) {
            instrumentation.sendKeyDownUpSync(keyCode)
        }
    }
}