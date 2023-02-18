package com.snaggly.ksw_toolkit.core.service.mcu.action

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.commander.AppStarter
import com.snaggly.ksw_toolkit.util.commander.KeyInjector
import com.snaggly.ksw_toolkit.util.commander.McuCommander
import com.snaggly.ksw_toolkit.util.commander.TaskerTaskStarter
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode
import com.snaggly.ksw_toolkit.util.list.keyevent.KeyCode
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsEnum
import com.wits.pms.bean.TxzMessage
import com.wits.pms.bean.ZlinkMessage
import com.wits.pms.receiver.*
import com.wits.pms.statuscontrol.WitsCommand
import com.wits.pms.utils.SystemProperties

open class EventAction(private val context: Context) {
    private val zLinkHandler = ZLinkHandler(context)
    open fun processAction(cmdType: Int, data: ByteArray, event: EventManagerTypes?, config: ConfigManager) {
        var keyBypass = false
        if (event != null) {
            if (event == EventManagerTypes.NavigationButton && ZLinkHandler.isUsing()) {
                zLinkHandler.navigation()
                return
            }
            val eventConfig = config.eventManagers[event]
            when (eventConfig?.eventMode) {
                EventMode.KeyEvent -> {
                    if (ZLinkHandler.isUsing()) {
                        keyBypass = true
                        when (eventConfig.keyCode) {
                            KeyCode.DPAD_UP.keycode -> {
                                if (event == EventManagerTypes.KnobTiltUp)
                                    zLinkHandler.tiltUp()
                                else
                                    zLinkHandler.turnLeft()
                            }
                            KeyCode.DPAD_DOWN.keycode -> {
                                if (event == EventManagerTypes.KnobTiltDown)
                                    zLinkHandler.tiltDown()
                                else
                                    zLinkHandler.turnRight()
                            }
                            KeyCode.ENTER.keycode -> zLinkHandler.knobPress()
                            /* Not letting ZLink itself handling the Left/Right commands enabled tilting to sidebar for AA
                            KeyCode.DPAD_RIGHT.keycode -> zLinkHandler.tiltRight()
                            KeyCode.DPAD_LEFT.keycode -> zLinkHandler.tiltLeft()
                            */
                            KeyCode.CALL.keycode -> {
                                if (event == EventManagerTypes.TelephoneButton && SystemProperties.get(ZlinkMessage.ZLINK_CALL_ING) == "1")
                                    zLinkHandler.telHangUp()
                                else
                                    zLinkHandler.telPick()
                            }
                            KeyCode.ENDCALL.keycode -> zLinkHandler.telHangUp()
                            KeyCode.MEDIA_PREVIOUS.keycode -> zLinkHandler.mediaPrev()
                            KeyCode.MEDIA_NEXT.keycode -> zLinkHandler.mediaNext()
                            KeyCode.MEDIA_PLAY_PAUSE.keycode -> zLinkHandler.playPause()
                            KeyCode.BACK.keycode -> zLinkHandler.back()
                            KeyCode.VOICE_ASSIST.keycode -> zLinkHandler.voiceAssist()
                            else -> keyBypass = false
                        }
                    }
                    else if (AutoKitCallBackImpl.isUsing()) {
                        when (eventConfig.keyCode) {
                            KeyCode.DPAD_UP.keycode -> {
                                if (event == EventManagerTypes.KnobTurnLeft) {
                                    keyBypass = true
                                    AutoKitCallBackImpl.drapLeft(context.applicationContext)
                                }
                            }
                            KeyCode.DPAD_DOWN.keycode -> {
                                if (event == EventManagerTypes.KnobTurnRight) {
                                    keyBypass = true
                                    AutoKitCallBackImpl.drapRight(context.applicationContext)
                                }
                            }
                            KeyCode.ENTER.keycode -> AutoKitCallBackImpl.enter(context.applicationContext)
                            else -> keyBypass = false
                        }
                    }
                    else if (eventConfig.keyCode == KeyCode.VOICE_ASSIST.keycode && TxzHandler.isUsing()) {
                        keyBypass = true
                        if (TxzHandler.isShowing())
                            TxzHandler.closeSpeech(context)
                        else
                            TxzHandler.openSpeech(context)
                    }

                    if (keyBypass)
                        return

                    if (!(McuLogic.actionLock && eventConfig.keyCode == KeyCode.HOME.keycode))
                        KeyInjector.sendKey(eventConfig.keyCode)
                    when (eventConfig.keyCode) {
                        KeyCode.DPAD_RIGHT.keycode -> AutoNavi.dragRight(context.applicationContext)
                        KeyCode.DPAD_LEFT.keycode -> AutoNavi.dragLeft(context.applicationContext)
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
                    McuCommander.executeCommand(
                        McuCommandsEnum.values[eventConfig.mcuCommandMode!!],
                        McuLogic.mcuCommunicator,
                        context
                    )
                }
                EventMode.TaskerTask -> {
                    TaskerTaskStarter.launchTaskerTaskById(eventConfig.taskerTaskName, context)
                }
                else -> {}
            }
        }
    }
}