package com.snaggly.ksw_toolkit.core.service.view

import android.content.Context
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.WitsStatus
import com.wits.pms.utils.SystemProperties
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands


class BackTapper(val context: Context) {
    companion object {
        var timesInitialized = 0
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val windowParam: WindowManager.LayoutParams = WindowManager.LayoutParams()
    private val view = View(context)
    private val handler = Handler(context.mainLooper)
    private var mLastEventTime: Long = 0
    private var mCurrentTouchX = 0
    private var mCurrentTouchY = 0
    private var mLastTouchX = 0
    private var mLastTouchY = 0
    private var mLongPressDownTime: Long = 0
    private var continuousSend = false

    init {
        windowParam.apply {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
            flags = windowParam.flags.or(WindowManager.LayoutParams.FLAG_FULLSCREEN).or(
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            ).or(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            height = -1
            width = -1
            format = 1
            alpha = 1.0f
            x = 0
            y = 0
        }

        view.apply {
            isClickable = true
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            setOnTouchListener { _, motion ->
                if (McuLogic.isReversing && McuLogic.realSysMode == 2 && filter(motion)) {
                    mCurrentTouchX = motion.x.toInt()
                    mCurrentTouchY = motion.y.toInt()
                    try {
                        if (SystemProperties.get("app.carMode.control") == "0")
                            mCurrentTouchY += 62
                    } catch (ignored: Exception) {
                    }
                    if (continuousSend) {
                        sendTouchA(motion, McuLogic.mcuCommunicator)
                    } else {
                        sendTouchB(motion, McuLogic.mcuCommunicator)
                    }
                    mLastTouchX = mCurrentTouchX;
                    mLastTouchY = mCurrentTouchY;
                    mLastEventTime = motion.eventTime
                    if (motion.action == 1) {
                        mLastEventTime = 0L
                    }
                } else {
                    if (motion.action == MotionEvent.ACTION_DOWN) {
                        returnBackToArm()
                        removeBackWindow()
                    }
                }
                performClick()
                return@setOnTouchListener false
            }
        }

        timesInitialized++
    }

    private fun filter(event: MotionEvent): Boolean {
        return mLastEventTime == 0.toLong() || event.eventTime - mLastEventTime >= 25 || event.action == 1
    }

    private fun isLongClick(event: MotionEvent): Boolean {
        if (mLastEventTime == 0L) {
            return false
        }
        val downTime = event.eventTime - mLastEventTime
        val x = (mCurrentTouchX - mLastTouchX).toFloat()
        val y = (mCurrentTouchY - mLastTouchY).toFloat()
        val longPress = x < 5.0f && x > -5.0f && y < 5.0f && y > -5.0f
        if (event.action == 1) {
            this.mLongPressDownTime = 0L
        } else if (longPress) {
            this.mLongPressDownTime += downTime
        }
        return this.mLongPressDownTime >= 1500
    }

    private fun sendTouchB(motion: MotionEvent, mcuCommunicator: McuCommunicator?) {
        if (mcuCommunicator == null)
            return
        val touchData = ByteArray(6)
        var i = 0
        touchData[0] = McuLogic.realSysMode.toByte()
        val x = mCurrentTouchX
        val y = mCurrentTouchY
        Log.i(
            "KswMcuLogic",
            "sendTouchDataB Touch X - $mCurrentTouchX ; Y - $mCurrentTouchY- event:$motion"
        )
        var z = true
        touchData[1] = (x shr 8).toByte()
        touchData[2] = x.toByte()
        touchData[3] = (y shr 8).toByte()
        touchData[4] = y.toByte()
        if (motion.action == 0) {
            if (motion.action != 0) {
                z = false
            }
            val isDown = z
            if (!isDown) {
                i = 2
            }
            touchData[5] = i.toByte()
            mcuCommunicator.sendCommand(107, touchData, false)
        }
    }

    private fun sendTouchA(motion: MotionEvent, mcuCommunicator: McuCommunicator?) {
        if (mcuCommunicator == null)
            return
        val touchData = ByteArray(6)
        var i = 0
        touchData[0] = 10
        val x = mCurrentTouchX
        val y = mCurrentTouchY
        Log.i(
            "KswMcuLogic",
            "sendTouchDataA Touch X - $mCurrentTouchX ; Y - $mCurrentTouchY- event:$motion"
        )
        touchData[1] = (x shr 8).toByte()
        touchData[2] = x.toByte()
        touchData[3] = (y shr 8).toByte()
        touchData[4] = y.toByte()
        val isLongClicked: Boolean = isLongClick(motion)
        val isActionUp = motion.action == 1
        if (isActionUp) {
            i = 2
        } else if (isLongClicked) {
            i = 1
        }
        touchData[5] = i.toByte()
        mcuCommunicator.sendCommand(107, touchData, false)
    }

    @Synchronized
    fun drawBackWindow() {
        val topApp = WitsStatus.getTopApp()
        if (getHasAlreadyDrawn() /*|| topApp == "com.wits.ksw" || topApp == "com.wits.ksw.launcher.view.lexus.OEMFMActivity"*/) {
            return
        }

        if (!McuLogic.isReversing && !McuLogic.turnedOffScreen && McuLogic.realSysMode == 1) {
            return
        }

        continuousSend = try {
            Settings.System.getInt(context.contentResolver, "touch_continuous_send", 0) == 1
        } catch (ignored: Exception) {
            false
        }

        handler.post {
            try {
                windowManager.addView(view, windowParam)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun removeBackWindow() {
        handler.post {
            try {
                if (getHasAlreadyDrawn()) {
                    windowManager.removeViewImmediate(view)
                }
            } catch (ignored: Exception) {
            }
        }
    }

    @Synchronized
    private fun returnBackToArm() {
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_ANDROID)
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SYS_SCREEN_ON)
    }

    @Synchronized
    fun getHasAlreadyDrawn(): Boolean {
        return view.isShown
    }
}