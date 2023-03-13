package com.snaggly.ksw_toolkit.util.brightnesstools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import ca.rmen.sunrisesunset.SunriseSunset
import com.google.android.gms.location.*
import com.snaggly.ksw_toolkit.IAutoTimeListener
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.AdvancedBrightness
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import java.util.*

@SuppressLint("MissingPermission")
class TimeBasedBrightness(private val context: Context) : AdvancedBrightnessHandler() {
    enum class Daytime {
        Initial,
        Morning,
        Day,
        Night
    }

    companion object{
        var autoTimeListeners = ArrayList<IAutoTimeListener>()
    }

    var currentDaytime : Daytime = Daytime.Initial

    private var sunriseTime : Calendar = Calendar.getInstance()
    private var sunsetTime : Calendar = Calendar.getInstance()

    fun setBrightness(context: Context) {
        val config = ConfigManager.getConfig(context)
        val newBrightness : Int =
            if (McuLogic.isAnyLightOn) {
                when (currentDaytime) {
                    Daytime.Day -> config.advancedBrightness.daylightHLBrightness!!//BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.daylightHLBrightness!!)
                    Daytime.Night, Daytime.Morning -> config.advancedBrightness.nightHLBrightnessLevel!!//BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.nightHLBrightnessLevel!!)
                    else -> return
                }
            } else {
                when(currentDaytime) {
                    Daytime.Day -> config.advancedBrightness.daylightBrightness!!//BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.daylightBrightness!!)
                    Daytime.Night, Daytime.Morning -> config.advancedBrightness.nightBrightnessLevel!!//BrightnessConverter.convertPercentToAndroidUnit(config.advancedBrightness.nightBrightnessLevel!!)
                    else -> return
                }
            }
        PowerManagerApp.setSettingsInt("Brightness", newBrightness)
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(newBrightness.toByte()))
    }

    private fun convertStringToCalendar(string : String?) : Calendar? {
        if (string == null) {
            return null
        }
        val splitString = string.split(":")
        if (splitString.size < 2) {
            return null
        }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, splitString[0].toInt())
            set(Calendar.MINUTE, splitString[1].toInt())
            set(Calendar.SECOND, 0)
        }
    }

    fun detectCurrentDaytime() : Daytime {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val sunriseHour = sunriseTime.get(Calendar.HOUR_OF_DAY)
        val sunriseMinute = sunriseTime.get(Calendar.MINUTE)
        val sunsetHour = sunsetTime.get(Calendar.HOUR_OF_DAY)
        val sunsetMinute = sunsetTime.get(Calendar.MINUTE)

        currentDaytime = if (currentHour < sunriseHour || (currentHour == sunriseHour && currentMinute < sunriseMinute)) { //6:05 < 6:44
            Daytime.Morning
        } else if (currentHour > sunsetHour || (currentHour == sunsetHour && currentMinute >= sunsetMinute)) { // 18:36 > 22:19
            Daytime.Night
        } else {
            Daytime.Day
        }

        return currentDaytime
    }

    private val config : ConfigManager = ConfigManager.getConfig(context)

    private val timeWatcher = object : Thread() {
        var amAlive = false

        override fun start() {
            if (!amAlive)
                super.start()
        }

        override fun run() {
            amAlive = true
            while(amAlive) {
                checkTime()
            }
        }

        fun stopWatcher() {
            amAlive = false
        }

        private fun checkTime() {
            val lCurDayT = currentDaytime
            if (lCurDayT != detectCurrentDaytime()) {
                setBrightness(context)
            }
            sleep(5000)
        }
    }
    private val fusedLocationManager = LocationServices.getFusedLocationProviderClient(context)
    private val locationReq : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER,1800000).build()
    private val locationCalB = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val result = locationResult.lastLocation ?: return
            setUpTimesFromGPS(result.latitude, result.longitude)
        }
    }

    init {
        val configSunriseTime = convertStringToCalendar(config.advancedBrightness.sunriseAt)
        val configSunsetTime = convertStringToCalendar(config.advancedBrightness.sunsetAt)

        if (configSunriseTime != null && configSunsetTime != null) {
            sunriseTime = configSunriseTime
            sunsetTime = configSunsetTime
        } else {
            AdvancedBrightness.initDefault().let {
                sunriseTime = convertStringToCalendar(it.sunriseAt)!!
                sunsetTime = convertStringToCalendar(it.sunsetAt)!!
            }
        }

        timeWatcher.start()

        if (config.advancedBrightness.autoTimes == true) {
            fusedLocationManager.requestLocationUpdates(locationReq, locationCalB, context.mainLooper)
        }
    }

    private fun setUpTimesFromGPS(latitude : Double, longitude : Double) {
        val sunTimes = SunriseSunset.getSunriseSunset(Calendar.getInstance(), latitude, longitude)
        if (sunTimes.size < 2) {
            return
        }
        val sunrise = sunTimes[0]
        val sunset = sunTimes[1]
        config.advancedBrightness.sunriseAt = "${String.format("%02d", sunrise.get(
            Calendar.HOUR_OF_DAY))}:${String.format("%02d", sunrise.get(Calendar.MINUTE))}"
        config.advancedBrightness.sunsetAt = "${String.format("%02d", sunset.get(
            Calendar.HOUR_OF_DAY))}:${String.format("%02d", sunset.get(Calendar.MINUTE))}"
        config.saveConfig() //Save new times for next time

        sunriseTime = sunrise
        sunsetTime = sunset

        for (listener in autoTimeListeners) {
            listener.updateAutoTime(config.advancedBrightness.sunriseAt, config.advancedBrightness.sunsetAt)
        }
    }

    override fun trigger() {
        setBrightness(context)
    }

    override fun destroy() {
        fusedLocationManager.removeLocationUpdates(locationCalB)
        timeWatcher.stopWatcher()
        currentDaytime = Daytime.Initial
    }

}