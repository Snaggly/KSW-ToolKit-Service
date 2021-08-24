package com.snaggly.ksw_toolkit.core.service.mcu.action

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.commander.AppStarter
import com.snaggly.ksw_toolkit.util.commander.KeyInjector
import com.snaggly.ksw_toolkit.util.commander.McuCommander
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode
import com.snaggly.ksw_toolkit.util.list.keyevent.KeyCode
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsEnum

open class EventAction(private val context: Context) {
    open fun processAction(cmdType: Int, data: ByteArray, event: EventManagerTypes?, config: ConfigManager) {
        if (event != null) {
            val eventConfig = config.eventManagers[event]
            when (eventConfig?.eventMode) {
                EventMode.KeyEvent -> {
                    if (!(McuLogic.actionLock && eventConfig.keyCode.data == KeyCode.HOME.keycode))
                        KeyInjector.sendKey(eventConfig.keyCode.data)
                }
                EventMode.StartApp -> {
                    AppStarter.launchAppById(eventConfig.appName.data, context)
                }
                EventMode.McuCommand -> {
                    McuCommander.executeCommand(McuCommandsEnum.values[eventConfig.mcuCommandMode.data], McuLogic.mcuCommunicator, context)
                }
                else -> {}
            }
        }
    }
}