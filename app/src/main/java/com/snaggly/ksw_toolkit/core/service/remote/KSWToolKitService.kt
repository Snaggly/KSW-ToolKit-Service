package com.snaggly.ksw_toolkit.core.service.remote

import android.app.Service
import com.snaggly.ksw_toolkit.IAdvancedBrightnessControl
import com.snaggly.ksw_toolkit.IKSWToolKitService
import com.snaggly.ksw_toolkit.IMcuListener
import com.snaggly.ksw_toolkit.ISystemOptionsControl
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode

class KSWToolKitService(val coreReaderHandler: McuReaderHandler) : IKSWToolKitService.Stub() {

    private val configManager = coreReaderHandler.config
    private val systemOptionsController = SystemOptionsController(coreReaderHandler)
    private val advancedBrightnessController = AdvancedBrightnessController(coreReaderHandler)

    override fun sendMcuCommand(cmdType: Int, data: ByteArray?): Boolean {
        if (!authenticate() || data == null)
            return false

        return if (McuLogic.mcuCommunicator == null)
            false
        else {
            McuLogic.mcuCommunicator?.sendCommand(cmdType, data, false)
            true
        }
    }

    override fun changeBtnConfig(btnType: Int, cmdType: Int, cmdValue: String?): Boolean {
        if (!authenticate())
            return false

        val eventConfig = configManager.eventManagers[EventManagerTypes.values()[btnType]]
        if (eventConfig == null)
            return false
        else {
            eventConfig.eventMode = EventMode.values()[cmdType]
            when (eventConfig.eventMode) {
                EventMode.NoAssignment -> {
                    eventConfig.appName = ""
                    eventConfig.keyCode = -1
                    eventConfig.mcuCommandMode = -1
                    eventConfig.taskerTaskName = ""
                }
                EventMode.KeyEvent -> {
                    try {
                        val cmdValueInt = cmdValue!!.toInt()
                        eventConfig.appName = ""
                        eventConfig.keyCode = cmdValueInt
                        eventConfig.mcuCommandMode = -1
                        eventConfig.taskerTaskName = ""
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                EventMode.StartApp -> {
                    try {
                        eventConfig.appName = cmdValue!!
                        eventConfig.keyCode = -1
                        eventConfig.mcuCommandMode = -1
                        eventConfig.taskerTaskName = ""
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                EventMode.McuCommand -> {
                    try {
                        val cmdValueInt = cmdValue!!.toInt()
                        eventConfig.appName = ""
                        eventConfig.keyCode = -1
                        eventConfig.mcuCommandMode = cmdValueInt
                        eventConfig.taskerTaskName = ""
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                EventMode.TaskerTask -> {
                    try {
                        eventConfig.appName = ""
                        eventConfig.keyCode = -1
                        eventConfig.mcuCommandMode = -1
                        eventConfig.taskerTaskName = cmdValue!!
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                else -> {}
            }
        }
        configManager.saveConfig()
        return true
    }

    override fun setDefaultBtnLayout() {
        configManager.eventManagers = EventManager.initStandardButtons()
        configManager.saveConfig()
    }

    override fun getConfig(): String? {
        return configManager.json
    }

    override fun setConfig(configJson: String?): Boolean {
        if (!authenticate())
            return false
        if (configJson == null)
            return false

        configManager.readConfig(configJson)
        configManager.saveConfig()

        coreReaderHandler.restartReader()
        return true
    }

    override fun registerMcuListener(listener: IMcuListener?): Boolean {
        if (!authenticate())
            return false
        return if (listener != null) {
            coreReaderHandler.registerMcuEventListener(listener)
            true
        } else false
    }

    override fun unregisterMcuListener(listener: IMcuListener?): Boolean {
        return if (listener != null) {
            coreReaderHandler.unregisterMcuEventListener(listener)
            true
        } else false
    }

    override fun getSystemOptionsController(): ISystemOptionsControl {
        return systemOptionsController
    }

    override fun getAdvancedBrightnessController(): IAdvancedBrightnessControl {
        return advancedBrightnessController
    }

    private fun authenticate() : Boolean {
        if (!ServiceValidation.hasAuthenticated) {
            return if (ServiceValidation.validate(coreReaderHandler.context)) {
                true
            } else {
                (coreReaderHandler.context as Service).stopSelf()
                false
            }
        }
        return true
    }
}