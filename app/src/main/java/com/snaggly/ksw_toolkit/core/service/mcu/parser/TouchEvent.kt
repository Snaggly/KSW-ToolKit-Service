package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.utils.TouchControl


class TouchEvent(context: Context) {
    private val mTouchControl = TouchControl(context)

    fun getTouchEvent(data: ByteArray) : EventManagerTypes{
        if (data.size <= 4)
            return EventManagerTypes.Dummy
        val x = TouchControl.getXFromData(data)
        val y = TouchControl.getYFromData(data)
        val buttonDown = data[4] == 1.toByte()
        var action = 2
        if (buttonDown) {
            if (!mTouchControl.isDown) {
                action = 0
            }
        } else if (mTouchControl.isDown) {
            action = 1
        }
        mTouchControl.opPointerEvent(x, 255 - y, action)
        return EventManagerTypes.TouchEvent
    }
}