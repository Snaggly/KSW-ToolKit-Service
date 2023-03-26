package com.snaggly.ksw_toolkit.util.brightnesstools

import android.annotation.SuppressLint
import android.content.Context
import ca.rmen.sunrisesunset.SunriseSunset
import com.google.android.gms.location.*
import com.snaggly.ksw_toolkit.IAutoTimeListener
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.AdvancedBrightness
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("MissingPermission")
class DaytimeObserver(private val context: Context) {
    enum class Daytime{
        Initial,
        Morning,
        Day,
        Night
    }

    companion object{
        var AutoTimeListeners = ArrayList<IAutoTimeListener>()
        var CurrentDaytime = Daytime.Initial
    }

    private val daytimeListeners = ArrayList<((newDaytime : Daytime) -> Unit)>()
    private var sunriseTime : Calendar = Calendar.getInstance()
    private var sunsetTime : Calendar = Calendar.getInstance()
    private val config : ConfigManager = ConfigManager.getConfig(context)

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
    }

    private var currentDaytime
        get() = CurrentDaytime
        set(value) {
            val flagNewData = CurrentDaytime != value
            CurrentDaytime = value
            if (flagNewData) { //Notify new change
                notifyListeners(value)
            }
        }

    @Synchronized
    fun registerDaytimeListener(listener: ((newDaytime : Daytime) -> Unit)) {
        daytimeListeners.add(listener)

        if (daytimeListeners.size == 1 || !timeWatcher.amAlive) {
            Thread(timeWatcher).start()
            if (config.advancedBrightness.autoTimes == true) {
                fusedLocationManager.requestLocationUpdates(locationReq, locationCalB, context.mainLooper)
            }
        } else {
            listener.invoke(currentDaytime)
        }
    }

    fun removeDaytimeListener(listener: ((newDaytime : Daytime) -> Unit)) {
        daytimeListeners.remove(listener)
        if (daytimeListeners.size == 0) {
            timeWatcher.stopWatcher()
            fusedLocationManager.removeLocationUpdates(locationCalB)
            currentDaytime = Daytime.Initial
        }
    }

    fun clearAllListeners() {
        timeWatcher.stopWatcher()
        fusedLocationManager.removeLocationUpdates(locationCalB)
        currentDaytime = Daytime.Initial
        daytimeListeners.clear()
    }

    private fun notifyListeners(daytime: Daytime) {
        daytimeListeners.removeAll(emptyList())
        for (listener in daytimeListeners) {
            listener(daytime)
        }
    }

    private fun detectCurrentDaytime() : Daytime {
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

        val deadListeners = ArrayList<IAutoTimeListener>()
        for (listener in AutoTimeListeners) {
            try {
                listener.updateAutoTime(config.advancedBrightness.sunriseAt, config.advancedBrightness.sunsetAt)
            }
            catch (_ : Exception) {
                deadListeners.add(listener)
            }
        }
        AutoTimeListeners.removeAll(deadListeners.toSet())
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


    private val timeWatcher = object : Runnable {
        var amAlive = false @Synchronized get @Synchronized set

        override fun run() {
            amAlive = true
            while(amAlive) {
                checkTime()
            }
        }

        fun stopWatcher() {
            amAlive = false
            currentDaytime = Daytime.Initial
        }

        private fun checkTime() {
            detectCurrentDaytime()
            Thread.sleep(5000)
        }
    }

}