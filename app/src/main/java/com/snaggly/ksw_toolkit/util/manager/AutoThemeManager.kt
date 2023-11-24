package com.snaggly.ksw_toolkit.util.manager

import android.app.UiModeManager
import android.content.Context
import android.os.Handler
import com.snaggly.ksw_toolkit.receiver.ZLinkReceiver
import com.snaggly.ksw_toolkit.receiver.ZLinkReceiver.Connection
import com.wits.pms.handler.ZLinkHandler

class AutoThemeManager(val context: Context) {
    private val uiModeManager = context.getSystemService(UiModeManager::class.java)
    private val zLinkHandler = ZLinkHandler(context)

    private var isAnyLightOn = false
    private var isNightTime = false

    private fun handleThemeChange() {
        if (isAnyLightOn || isNightTime) {
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
            if (ZLinkReceiver.CurrentDataSet.isShowing && ZLinkReceiver.CurrentDataSet.currentConnection != Connection.Disconnected) {
                zLinkHandler.setDarkTheme()
            }
        } else {
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
            if (ZLinkReceiver.CurrentDataSet.isShowing && ZLinkReceiver.CurrentDataSet.currentConnection != Connection.Disconnected) {
                zLinkHandler.setLightTheme()
            }
        }
    }

    fun handleThemeChangeByLightEvent(isAnyLightOn: Boolean) {
        this.isAnyLightOn = isAnyLightOn
        handleThemeChange()
    }

    fun handleThemeChangeByTime(isNightTime: Boolean) {
        this.isNightTime = isNightTime
        handleThemeChange()
    }

    fun zlinkHandler(data: ZLinkReceiver.ZLinkData) {
        if (data.isShowing && data.currentConnection != Connection.Disconnected) {
            if (isAnyLightOn || isNightTime) {
                Handler(context.mainLooper).postDelayed(
                    { zLinkHandler.setDarkTheme() },
                    1000
                )
            } else {
                Handler(context.mainLooper).postDelayed(
                    { zLinkHandler.setLightTheme() },
                    1000
                )
            }
        }
    }
}