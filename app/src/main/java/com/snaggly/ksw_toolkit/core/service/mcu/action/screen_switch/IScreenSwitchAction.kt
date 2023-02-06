package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

interface IScreenSwitchAction {
    fun performOnAndroidSwitch()
    fun performOnOEMSwitch()
    fun restoreState()
}