package com.snaggly.ksw_toolkit.util.manager

import android.app.UiModeManager
import android.content.Context
import android.content.IntentFilter
import android.os.Handler
import com.snaggly.ksw_toolkit.receiver.ZLinkReceiver
import com.wits.pms.bean.ZlinkMessage
import com.wits.pms.handler.ZLinkHandler
import com.snaggly.ksw_toolkit.receiver.ZLinkReceiver.Connection

class AutoThemeManager(val context: Context, private val zLinkReceiver: ZLinkReceiver) {
    private val uiModeManager = context.getSystemService(UiModeManager::class.java)
    private val zLinkHandler = ZLinkHandler(context)

    private var isAnyLightOn = false
    private var isNightTime = false

    private fun handleThemeChange() {
        if (isAnyLightOn || isNightTime) {
            if (ZLinkReceiver.CurrentDataSet.isShowing && ZLinkReceiver.CurrentDataSet.currentConnection != Connection.Disconnected) {
                zLinkHandler.setDarkTheme()
                zLinkReceiver.setReceiverHandler {
                    if (!it.isShowing) {
                        uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                        zLinkReceiver.setReceiverHandler { }
                    }
                }
            }
            else {
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                zLinkReceiver.setReceiverHandler {
                    if (it.isShowing && it.currentConnection != Connection.Disconnected) {
                        Handler(context.mainLooper).postDelayed({ zLinkHandler.setDarkTheme() }, 1000)
                        zLinkReceiver.setReceiverHandler { }
                    }
                }
            }
        }
        else {
            if (ZLinkReceiver.CurrentDataSet.isShowing && ZLinkReceiver.CurrentDataSet.currentConnection != Connection.Disconnected) {
                zLinkHandler.setLightTheme()
                zLinkReceiver.setReceiverHandler {
                    if (!it.isShowing) {
                        uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                        zLinkReceiver.setReceiverHandler { }
                    }
                }
            }
            else {
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                zLinkReceiver.setReceiverHandler {
                    if (it.isShowing && it.currentConnection != Connection.Disconnected) {
                        Handler(context.mainLooper).postDelayed({ zLinkHandler.setLightTheme() }, 1000)
                        zLinkReceiver.setReceiverHandler { }
                    }
                }
            }
        }
    }

    fun handleThemeChangeByLightEvent(isAnyLightOn : Boolean) {
        this.isAnyLightOn = isAnyLightOn
        handleThemeChange()
    }

    fun handleThemeChangeByTime(isNightTime: Boolean) {
        this.isNightTime = isNightTime
        handleThemeChange()
    }
}