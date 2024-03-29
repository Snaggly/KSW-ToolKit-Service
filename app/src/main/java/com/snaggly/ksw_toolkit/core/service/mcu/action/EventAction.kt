package com.snaggly.ksw_toolkit.core.service.mcu.action

import android.content.Context
import android.os.Handler
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
import com.wits.pms.handler.*
import com.wits.pms.statuscontrol.WitsCommand

open class EventAction(private val context: Context) {
    companion object {
        private var zLinkHomeClicked = false
        private var zLinkHomeDoubleClicked = false
    }

    private val zLinkHandler = ZLinkHandler(context)
    open fun processAction(cmdType: Int, data: ByteArray, event: EventManagerTypes?, config: ConfigManager) {
        var keyBypass = false
        val isUsingZLink = ZLinkHandler.isUsing() && ZLinkHandler.isConnected()
        if (event != null) {
            if (isUsingZLink) {
                if (event == EventManagerTypes.NavigationButton) {
                    zLinkHandler.navigation()
                    return
                }
                else if (event == EventManagerTypes.ModeButton){
                    zLinkHandler.media()
                    return
                }
            }
            val eventConfig = config.eventManagers[event]
            when (eventConfig?.eventMode) {
                EventMode.KeyEvent -> {
                    if (isUsingZLink) {
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
                            //KeyCode.CALL.keycode -> CallHandler.handleAccept(context)
                            //KeyCode.ENDCALL.keycode -> CallHandler.handleReject(context)
                            KeyCode.MEDIA_PREVIOUS.keycode -> zLinkHandler.mediaPrev()
                            KeyCode.MEDIA_NEXT.keycode -> zLinkHandler.mediaNext()
                            KeyCode.MEDIA_PLAY_PAUSE.keycode -> zLinkHandler.playPause()
                            KeyCode.MUSIC.keycode -> zLinkHandler.media()
                            KeyCode.MODE_SWITCH.keycode -> zLinkHandler.media()
                            KeyCode.NAVIGATION.keycode -> zLinkHandler.navigation()
                            KeyCode.HOME.keycode -> {
                                if (!zLinkHomeClicked) {
                                    zLinkHomeClicked = true
                                    zLinkHandler.home()
                                    Handler(context.mainLooper).postDelayed({
                                        zLinkHomeClicked = false
                                        zLinkHomeDoubleClicked = false
                                    }, 1000)
                                } else {
                                    zLinkHomeDoubleClicked = true
                                    keyBypass = false
                                }
                            }
                            KeyCode.BACK.keycode -> zLinkHandler.back()
                            KeyCode.VOICE_ASSIST.keycode -> zLinkHandler.voiceAssist()
                            else -> keyBypass = false
                        }
                    }
                    else if (AutoKitCallBackImpl.isUsing()) {
                        keyBypass = true
                        when (eventConfig.keyCode) {
                            KeyCode.DPAD_UP.keycode -> {
                                if (event == EventManagerTypes.KnobTurnLeft) {
                                    AutoKitCallBackImpl.drapLeft(context.applicationContext)
                                }
                            }
                            KeyCode.DPAD_DOWN.keycode -> {
                                if (event == EventManagerTypes.KnobTurnRight) {
                                    AutoKitCallBackImpl.drapRight(context.applicationContext)
                                }
                            }
                            KeyCode.ENTER.keycode -> {
                                AutoKitCallBackImpl.enter(context.applicationContext)
                                keyBypass = false
                            }
                            else -> keyBypass = false
                        }
                    }
                    else if (AutoNavi.isUsing()) {
                        keyBypass = false
                        when (eventConfig.keyCode) {
                            KeyCode.DPAD_RIGHT.keycode -> AutoNavi.dragRight(context.applicationContext)
                            KeyCode.DPAD_LEFT.keycode -> AutoNavi.dragLeft(context.applicationContext)
                        }
                    }
                    else if (eventConfig.keyCode == KeyCode.VOICE_ASSIST.keycode && TxzHandler.isUsing()) {
                        keyBypass = true
                        if (TxzHandler.isShowing())
                            TxzHandler.closeSpeech(context)
                        else
                            TxzHandler.openSpeech(context)
                    }
                    else if (HiCarHandler.isUsing()) {
                        keyBypass = true
                        when (eventConfig.keyCode) {
                            KeyCode.APP_SWITCH.keycode -> WitsCommand.sendCommand(7, 101, "")
                            KeyCode.VOICE_ASSIST.keycode -> WitsCommand.sendCommand(7, 102, "")
                        }
                    }

                    if (keyBypass)
                        return
                    if (!(McuLogic.actionLock && eventConfig.keyCode == KeyCode.HOME.keycode)) {
                        when (eventConfig.keyCode) {
                            KeyCode.CALL.keycode -> CallHandler.handleAccept(context.applicationContext)
                            KeyCode.ENDCALL.keycode -> CallHandler.handleReject(context.applicationContext)
                            else -> KeyInjector.sendKey(eventConfig.keyCode)
                        }
                    }
                }
                EventMode.StartApp -> {
                    AppStarter.launchAppById(eventConfig.appName, context)
                }
                EventMode.McuCommand -> {
                    val command = eventConfig.mcuCommandMode ?: return
                    McuCommander.executeCommand(
                        McuCommandsEnum.values[command],
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