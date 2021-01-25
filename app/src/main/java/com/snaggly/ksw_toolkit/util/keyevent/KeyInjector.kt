package com.snaggly.ksw_toolkit.util.keyevent

import android.app.Instrumentation

object KeyInjector {
    private val instrumentation = Instrumentation()

    fun sendKey(keyCode: Int) {
        instrumentation.sendKeyDownUpSync(keyCode)
    }
}