package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.bean.TxzMessage
import com.wits.pms.utils.Utils.getIndexValue2DataNew
import projekt.auto.mcu.ksw.model.McuStatus.CarData


class TxzInfoEvent(val context: Context) {
    fun getTxzInfoEvent(data: ByteArray) : EventManagerTypes {
        val isCarDoorClose = getIndexValue2DataNew(data[0], 1, 3) === 0
        val isSeatbeltOn = getIndexValue2DataNew(data[0], 2, 3) === 0
        val isEnoughOil = getIndexValue2DataNew(data[0], 3, 3) === 0
        val isNormalSpeed = getIndexValue2DataNew(data[0], 4, 3) === 0
        val isTempNormal = getIndexValue2DataNew(data[0], 5, 3) === 0
        Log.d(
            "KswMcuListener",
            "onMcuMessage: CMD_TXZ_DATA isCarDoorClose =$isCarDoorClose  isSeatbeltOn =$isSeatbeltOn  isNoEnoughOil =$isEnoughOil isNormalSpeed =$isNormalSpeed isTempNormal =$isTempNormal"
        )
        val isNormalState =
            isCarDoorClose && isSeatbeltOn && isEnoughOil && isNormalSpeed && isTempNormal
        sendCarInfo2Txz(
            isNormalState,
            isCarDoorClose,
            isSeatbeltOn,
            isNormalSpeed,
            isTempNormal,
            isEnoughOil,
            McuLogic.mcuStat.carData
        )
        return EventManagerTypes.TxzInfoEvent
    }

    private fun sendCarInfo2Txz(
        isNormalState: Boolean,
        iscarDoorClose: Boolean,
        isSeatBeltOn: Boolean,
        isNormalSpeed: Boolean,
        isTempNormal: Boolean,
        isEnoughOil: Boolean,
        cardata: CarData?
    ) {
        val bundle = Bundle()
        bundle.putBoolean("isNormalState", isNormalState)
        bundle.putBoolean("cardoor", iscarDoorClose)
        bundle.putBoolean("seatbelt", isSeatBeltOn)
        bundle.putBoolean("isNormalSpeed", isNormalSpeed)
        bundle.putInt("speed", cardata?.speed ?: 0)
        bundle.putBoolean("isEnoughOil", isEnoughOil)
        bundle.putBoolean("isTempNormal", isTempNormal)
        bundle.putFloat("ambient_temp", cardata?.airTemperature ?: 0.0f)
        val txzMessage = TxzMessage(2400, "txz.mcu.carinfo", bundle)
        txzMessage.sendBroadCast(context)
    }
}