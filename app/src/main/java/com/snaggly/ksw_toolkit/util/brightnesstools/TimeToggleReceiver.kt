package com.snaggly.ksw_toolkit.util.brightnesstools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null)
            return
        TimeBasedBrightness.currentDaytime = when (TimeBasedBrightness.currentDaytime) {
            TimeBasedBrightness.Daytime.Morning ->  TimeBasedBrightness.Daytime.Day
            TimeBasedBrightness.Daytime.Day -> TimeBasedBrightness.Daytime.Night
            TimeBasedBrightness.Daytime.Night -> TimeBasedBrightness.Daytime.Morning
            else -> return
        }

        TimeBasedBrightness.setBrightness(context)
        TimeBasedBrightness.scheduleNextCycle(context)
    }
}