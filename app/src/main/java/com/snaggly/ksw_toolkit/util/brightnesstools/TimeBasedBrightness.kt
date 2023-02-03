package com.snaggly.ksw_toolkit.util.brightnesstools

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.provider.Settings
import ca.rmen.sunrisesunset.SunriseSunset
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import java.util.*

class TimeBasedBrightness(private val context: Context, private val config: ConfigManager) : AdvancedBrightnessHandler() {
    enum class Daytime {
        Initial,
        Morning,
        Day,
        Night
    }

    companion object {
        var currentDaytime : Daytime = Daytime.Initial
    }
    private val handler = Handler(context.mainLooper)
    var iAmAlive = false

    init {
        currentDaytime = getCurrentDaytime()
        iAmAlive = true
        scheduleNextCycle()
    }

    private fun getSunriseTimeFromConfig() : Calendar {
        val sunriseTime = Calendar.getInstance()
        sunriseTime.set(Calendar.HOUR_OF_DAY, config.advancedBrightness.sunriseAt!!.split(":")[0].toInt())
        sunriseTime.set(Calendar.MINUTE, config.advancedBrightness.sunriseAt!!.split(":")[1].toInt())
        return sunriseTime
    }

    private fun getSunsetTimeFromConfig() : Calendar {
        val sunsetTime = Calendar.getInstance()
        sunsetTime.set(Calendar.HOUR_OF_DAY, config.advancedBrightness.sunsetAt!!.split(":")[0].toInt())
        sunsetTime.set(Calendar.MINUTE, config.advancedBrightness.sunsetAt!!.split(":")[1].toInt())
        return sunsetTime
    }

    private fun getCurrentDaytime() : Daytime {
        if (config.advancedBrightness.autoTimes!!) {
            autoDetectTimes(Calendar.getInstance())
        }

        val currentTime = Calendar.getInstance()
        val offsetToSunrise = currentTime.compareTo(getSunriseTimeFromConfig()) // ( offset<0 -> before sunrise, offset>=0 -> after sunrise)
        val offsetToSunset = currentTime.compareTo(getSunsetTimeFromConfig()) // ( offset<0 -> before sunset, offset>=0 -> after sunset)
        return if (offsetToSunrise < 0 && offsetToSunset < 0)
            Daytime.Morning
        else if (offsetToSunrise >= 0 && offsetToSunset < 0) {
            Daytime.Day
        } else {
            Daytime.Night
        }
    }

    @SuppressLint("MissingPermission")
    private fun autoDetectTimes(atDate : Calendar) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            val sunTimes = SunriseSunset.getSunriseSunset(atDate, it.latitude, it.longitude)
            if (sunTimes.size >=2) {
                config.advancedBrightness.sunriseAt = "${String.format("%02d", sunTimes[0].get(
                    Calendar.HOUR_OF_DAY))}:${String.format("%02d", sunTimes[0].get(Calendar.MINUTE))}"
                config.advancedBrightness.sunsetAt = "${String.format("%02d", sunTimes[1].get(
                    Calendar.HOUR_OF_DAY))}:${String.format("%02d", sunTimes[1].get(Calendar.MINUTE))}"
            }
        }
    }

    private fun scheduleNextCycle() {
        if (!iAmAlive)
            return
        trigger()
        when (currentDaytime) {
            Daytime.Morning -> {
                handler.postDelayed({ //Schedule to next sunrise in same day
                    currentDaytime = Daytime.Day
                    scheduleNextCycle()
                }, getSunriseTimeFromConfig().timeInMillis - Calendar.getInstance().timeInMillis)
            }
            Daytime.Day -> { //Schedule to next sunset
                handler.postDelayed({
                    currentDaytime = Daytime.Night
                    scheduleNextCycle()
                }, getSunsetTimeFromConfig().timeInMillis - Calendar.getInstance().timeInMillis)
            }
            Daytime.Night -> { //Schedule to next sunrise next day
                var nextSunrise = getSunriseTimeFromConfig()
                nextSunrise.add(Calendar.DATE, 1)
                if (config.advancedBrightness.autoTimes == true) { //Overwrite
                    autoDetectTimes(nextSunrise)
                    nextSunrise = getSunriseTimeFromConfig()
                }

                handler.postDelayed({
                    currentDaytime = Daytime.Day
                    scheduleNextCycle()
                }, nextSunrise.timeInMillis - Calendar.getInstance().timeInMillis)
            }
            else -> return
        }
    }

    override fun trigger() {
        val newBrightness : Int =
            if (McuLogic.isAnyLightOn) {
                when (currentDaytime) {
                    Daytime.Day -> BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.daylightHLBrightness!!)
                    Daytime.Night, Daytime.Morning -> BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.nightHLBrightnessLevel!!)
                    else -> return
                }
            } else {
                when(currentDaytime) {
                    Daytime.Day -> BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.daylightBrightness!!)
                    Daytime.Night, Daytime.Morning -> BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.nightBrightnessLevel!!)
                    else -> return
                }
            }

        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, newBrightness)
    }

    override fun destroy() {
        iAmAlive = false
        currentDaytime = Daytime.Initial
    }

}