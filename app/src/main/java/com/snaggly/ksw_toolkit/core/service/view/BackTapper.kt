package com.snaggly.ksw_toolkit.core.service.view

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class BackTapper(val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var view: View
    private val handler = Handler(context.mainLooper)
    private var hasAlreadyDrawn = false

    fun drawBackWindow(mcuCommunicator: McuCommunicator) {
        if (getHasAlreadyDrawn()) {
            return
        }
        setHasAlreadyDrawn(true)

        handler.post {
            val windowParam = WindowManager.LayoutParams()
            windowParam.type = 2010
            windowParam.flags = windowParam.flags.or(1024).or(262144).or(524288)
            windowParam.height = -1
            windowParam.width = -1
            windowParam.format = 1
            windowParam.alpha = 1.0f
            windowParam.x = 0
            windowParam.y = 0

            view = View(context)
            view.isClickable = true
            view.layoutParams = ViewGroup.LayoutParams(-1, -1)
            view.setOnClickListener {  }
            view.setOnTouchListener { v, _ ->
                returnBackToArm(mcuCommunicator)
                v.performClick()
                windowManager.removeViewImmediate(view)
                setHasAlreadyDrawn(true)
                return@setOnTouchListener false
            }
            windowManager.addView(view, windowParam)
        }
    }

    private fun returnBackToArm(mcuCommunicator: McuCommunicator) {
        mcuCommunicator.sendCommand(McuCommands.SWITCH_TO_ANDROID)
    }

    @Synchronized private fun getHasAlreadyDrawn() : Boolean {
        return hasAlreadyDrawn
    }

    @Synchronized private fun setHasAlreadyDrawn(newVal : Boolean) {
        hasAlreadyDrawn = newVal
    }
}