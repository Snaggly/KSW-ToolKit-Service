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
import com.wits.pms.receiver.AutoKitCallBackImpl
import com.wits.pms.receiver.AutoNavi
import com.wits.pms.receiver.CallHandler
import com.wits.pms.statuscontrol.WitsCommand

open class EventAction(private val context: Context) {
    open fun processAction(cmdType: Int, data: ByteArray, event: EventManagerTypes?, config: ConfigManager) {
        if (event != null) {
            val eventConfig = config.eventManagers[event]
            when (eventConfig?.eventMode) {
                EventMode.KeyEvent -> {
                    if (AutoKitCallBackImpl.isUsing()) when (event) {
                        EventManagerTypes.KnobTurnLeft -> eventConfig.keyCode = KeyCode.DPAD_LEFT.keycode
                        EventManagerTypes.KnobTurnRight -> eventConfig.keyCode = KeyCode.DPAD_RIGHT.keycode
                    }
                    if (!(McuLogic.actionLock && eventConfig.keyCode == KeyCode.HOME.keycode))
                        KeyInjector.sendKey(eventConfig.keyCode)
                    when (eventConfig.keyCode) {
                        KeyCode.DPAD_UP.keycode -> AutoKitCallBackImpl.drapUp(context.applicationContext)
                        KeyCode.DPAD_DOWN.keycode -> AutoKitCallBackImpl.drapDown(context.applicationContext)
                        KeyCode.ENTER.keycode -> AutoKitCallBackImpl.enter(context.applicationContext)
                        KeyCode.DPAD_RIGHT.keycode -> {
                            AutoKitCallBackImpl.drapRight(context.applicationContext)
                            AutoNavi.dragRight(context.applicationContext)
                        }
                        KeyCode.DPAD_LEFT.keycode -> {
                            AutoKitCallBackImpl.drapLeft(context.applicationContext)
                            AutoNavi.dragLeft(context.applicationContext)
                        }
                        KeyCode.CALL.keycode -> {
                            WitsCommand.sendCommand(7, 113, "")
                            CallHandler.handleAccept(context.applicationContext)
                        }
                        KeyCode.ENDCALL.keycode -> {
                            WitsCommand.sendCommand(7, 113, "")
                            CallHandler.handleReject(context.applicationContext)
                        }
                        KeyCode.APP_SWITCH.keycode -> WitsCommand.sendCommand(7, 101, "")
                        KeyCode.VOICE_ASSIST.keycode -> WitsCommand.sendCommand(7, 102, "")
                    }
                }
                EventMode.StartApp -> {
                    AppStarter.launchAppById(eventConfig.appName, context)
                }
                EventMode.McuCommand -> {
                    McuCommander.executeCommand(McuCommandsEnum.values[eventConfig.mcuCommandMode!!], McuLogic.mcuCommunicator, context)
                }
                else -> {}
            }
        }
    }
}