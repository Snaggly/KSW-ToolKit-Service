package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object CarDataEventLogger : ICarDataEvent {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        when {
            data[0] == 0x19.toByte() -> {
                McuLogic.mcuStat.carData.parseFromCarDataEvent(data)
            }
            data[0] == 0x10.toByte() -> {
                McuLogic.mcuStat.carData.parseFromBrakeBeltEvent(data)
            }
            data[0] == 0x12.toByte() -> {
                McuLogic.mcuStat.carData.parseFromDoorEvent(data)
            }
            data[0] == 0x1C.toByte() -> {
                McuLogic.mcuStat.acData.parseFromACDataEvent(data)
            }
        }
        McuLogic.sendCarData()

        return EventManagerTypes.CarData
    }
}