package com.snaggly.ksw_toolkit.util.brightnesstools

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.collection.McuCommands

abstract class AdvancedBrightnessHandler {
    abstract fun trigger()
    abstract fun destroy()

    companion object {
        private val emptyHandler = object : AdvancedBrightnessHandler() {
            override fun trigger() { }
            override fun destroy() { }
        }

        fun getHandler() : AdvancedBrightnessHandler {
            return emptyHandler
        }

        fun getHandler(context: Context, config: ConfigManager) : AdvancedBrightnessHandler {
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_On) //Set MCU to manual brightness, so this handler manages
            return if (config.advancedBrightness.isTimeBasedEnabled!!) {
                TimeBasedBrightness(context, config)
            }
            /*else if (config.advancedBrightness.isUSBBasedEnabled!!) {
                //TODO Not yet implemented, maybe will look into Yocto-Light.
                getHandler()
            }*/
            else {
                if (PowerManagerApp.getSettingsInt("Backlight_auto_set") == 0) { //If automatic brightness was on -> reset.
                    McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
                }
                getHandler()
            }
        }
    }
}