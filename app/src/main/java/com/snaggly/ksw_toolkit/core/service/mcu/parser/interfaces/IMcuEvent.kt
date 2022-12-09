package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.parser.*
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class IMcuEvent(context: Context, backTapper: BackTapper) {
    var benzDataEvent : IBenzDataEvent = BenzDataEventLogger
    var carDataEvent : ICarDataEvent = CarDataEventLogger
    var screenSwitchEvent : IScreenSwitchEvent = ScreenSwitchEvent(backTapper)
    var powerEvent = PowerEvent(backTapper)
    val buttonClickEvent = ButtonClickEvent
    val idleEvent = IdleEvent(backTapper)
    val systemStatusEvent = SystemStatusEvent(backTapper)
    val timeEvent = TimeEvent(context)
    val accEvent = AccEvent(context)
    val mcuVerEvent = McuVersionEvent
    val touchEvent = TouchEvent(context)
    val canCheckEvent = CanCheckEvent(context)
    val mediaEvent = MediaEvent()
    val mediaStringEvent = MediaStringEvent()
    val mediaStateEvent = MediaStateEvent()
    val mediaDiscStatusEvent = MediaDiscStatusEvent()
    val mediaVolumeEvent = MediaVolumeEvent()
    val bluetoothStatusEvent = BluetoothStatusEvent()
    val bluetoothNameEvent = BluetoothNameEvent()
    val eqDataEvent = EQDataEvent(context)
    val txzInfoEvent = TxzInfoEvent(context)
    abstract fun getMcuEvent(cmdType: Int, data: ByteArray, context: Context): EventManagerTypes
}