package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.IMcuEvent
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class McuEvent(private val context: Context, backTapper: BackTapper) : IMcuEvent(context, backTapper) {
    override fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes {
        when (cmdType) {
            0xA1 -> {
                return if (data.size > 2 && data[0] == 0x17.toByte() && data[2] == 0x01.toByte()) {
                    buttonClickEvent.getClickEvent(data)
                } else if (data[0] == 0x1A.toByte() && data.size > 1) {
                    screenSwitchEvent.getScreenSwitch(data)
                } else if (data[0] == 0x18.toByte() && data.size > 6) {
                    timeEvent.getTimeEvent(data)
                } else {
                    carDataEvent.getCarDataEvent(data)
                }
            }
            0x1B -> return touchEvent.getTouchEvent(data)
            0x1D -> return benzDataEvent.getBenzDataEvent(data)
            0x1C -> {
                return if (data[0] == 1.toByte()) {
                    idleEvent.getIdleEvent(data)
                } else {
                    idleEvent.getIdleEvent(byteArrayOf(2))
                }
            }
            0x15 -> return systemStatusEvent.parseSystemStatusEvent(data)
            0x1A -> {
                return if (data[0] == 1.toByte()) {
                    systemStatusEvent.parseSystemStatusEvent(byteArrayOf(2))
                } else {
                    systemStatusEvent.parseSystemStatusEvent(byteArrayOf(1))
                }
            }
            0x10 -> return accEvent.getAccEvent(data)
            0x11 -> return powerEvent.getPowerEvent(data, screenSwitchEvent)
            0x1F -> return mediaEvent.getMediaEvent(data)
            0x20 -> return mediaStringEvent.getMediaEvent(data)
            0x21 -> return mediaStateEvent.getMediaEvent(data)
            0x22 -> return mediaDiscStatusEvent.getMediaEvent(data)
            0x23 -> return mediaVolumeEvent.handleNewVolume(data)
            0x24 -> return bluetoothStatusEvent.getBluetoothStatusEvent(data)
            0x25 -> return bluetoothNameEvent.getNameEvent(data)
            0x26 -> return eqDataEvent.getEQDataEvent(data)
            0x27 -> return txzInfoEvent.getTxzInfoEvent(data)
            0x1E -> return if(data.size > 5) canCheckEvent.getCanCheckEvent(data) else EventManagerTypes.Dummy
            0x18 -> return mcuVerEvent.getMcuVersionEvent(data)
            else -> return EventManagerTypes.Dummy
        }
    }
}