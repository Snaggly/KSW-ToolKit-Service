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
    private var view: View? = null
    private val handler = Handler(context.mainLooper)
    private var hasAlreadyDrawn = false

    fun drawBackWindow(mcuCommunicator: McuCommunicator) {
        if (getHasAlreadyDrawn()) {
            return
        }
        setHasAlreadyDrawn(true)

        handler.post {
            val windowParam = WindowManager.LayoutParams()
            windowParam.apply {
                type = 2010
                flags = windowParam.flags.or(1024).or(262144).or(524288)
                height = -1
                width = -1
                format = 1
                alpha = 1.0f
                x = 0
                y = 0
            }

            view = View(context)
            view?.apply {
                isClickable = true
                layoutParams = ViewGroup.LayoutParams(-1, -1)
                setOnClickListener {
                    returnBackToArm(mcuCommunicator)
                }
                setOnTouchListener { v, _ ->
                    v.performClick()
                    return@setOnTouchListener false
                }
            }
            windowManager.addView(view, windowParam)
        }
    }

    fun removeBackWindow() {
        if (getHasAlreadyDrawn()) {
            handler.post {
                windowManager.removeViewImmediate(view)
                setHasAlreadyDrawn(false)
            }
        }
    }

    private fun returnBackToArm(mcuCommunicator: McuCommunicator) {
        mcuCommunicator.sendCommand(McuCommands.SWITCH_TO_ANDROID)
    }

    @Synchronized fun getHasAlreadyDrawn() : Boolean {
        return hasAlreadyDrawn
    }

    @Synchronized private fun setHasAlreadyDrawn(newVal : Boolean) {
        hasAlreadyDrawn = newVal
    }
}