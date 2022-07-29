package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import android.content.Intent
import android.os.UserHandle
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.utils.CenterServiceManager

class CanCheckEvent(val context: Context) {
    fun getCanCheckEvent(data: ByteArray) : EventManagerTypes {
        val intent = Intent("com.wits.pms.MCU_CHECK_CAR")
        intent.flags = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP or Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        intent.putExtra("checkCanStatus", byteArrayOf(data[0],data[1],data[2],data[3],data[4],data[5]))
        context.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(CenterServiceManager.getUid(context)))
        return EventManagerTypes.CanStatusCheck
    }
}