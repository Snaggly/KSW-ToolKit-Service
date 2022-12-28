package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ICarDataEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.CallBackBinder
import com.wits.pms.statuscontrol.WitsStatus

class CarDataEventLogger(context: Context) : ICarDataEvent(context) {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        when {
            data[0] == 0x19.toByte() && data.size > 15 -> {
                McuLogic.mcuStat.carData.parseFromCarDataEvent(data)
            }
            data[0] == 0x10.toByte() && data.size > 2 -> {
                lightEvent.getCarDataEvent(data)
                McuLogic.mcuStat.carData.parseFromBrakeBeltEvent(data)
                McuLogic.systemStatus.parseFromMcuEvent(data)
                WitsStatus.sendOutIll(McuLogic.systemStatus.ill)
                WitsStatus.sendOutEpb(McuLogic.systemStatus.epb)
                CallBackBinder.handleLRReverse(McuLogic.mcuStat.carData.signalRight, McuLogic.mcuStat.carData.signalLeft)
            }
            data[0] == 0x12.toByte() && data.size > 1 -> {
                McuLogic.mcuStat.carData.parseFromDoorEvent(data)
            }
            data[0] == 0x1C.toByte() && data.size > 5 -> {
                McuLogic.mcuStat.acData.parseFromACDataEvent(data)
            }
        }
        McuLogic.sendCarData()

        return EventManagerTypes.CarData
    }
}