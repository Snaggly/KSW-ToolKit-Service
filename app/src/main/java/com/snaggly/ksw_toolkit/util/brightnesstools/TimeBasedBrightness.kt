package com.snaggly.ksw_toolkit.util.brightnesstools

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import ca.rmen.sunrisesunset.SunriseSunset
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.AdvancedBrightness
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import java.util.*

class TimeBasedBrightness(private val context: Context) : AdvancedBrightnessHandler() {
    enum class Daytime {
        Initial,
        Morning,
        Day,
        Night
    }

    companion object {
        var currentDaytime : Daytime = Daytime.Initial

        private var sunriseTime : Calendar = Calendar.getInstance()
        private var sunsetTime : Calendar = Calendar.getInstance()

        fun setBrightness(context: Context) {
            val config = ConfigManager.getConfig(context)
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

        fun scheduleNextCycle(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                Intent(context, TimeToggleReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
            when (currentDaytime) {
                Daytime.Morning -> {
                    alarmManager.set(
                        AlarmManager.RTC,
                        sunriseTime.timeInMillis,
                        pendingIntent)
                }
                Daytime.Day -> { //Schedule to next sunset
                    alarmManager.set(
                        AlarmManager.RTC,
                        sunsetTime.timeInMillis,
                        pendingIntent)
                }
                Daytime.Night -> { //Schedule to next sunrise next day
                    alarmManager.set(
                        AlarmManager.RTC,
                        sunriseTime.apply { set(Calendar.DATE, 1) }.timeInMillis,
                        pendingIntent)
                }
                else -> return
            }
        }

        fun convertStringToCalendar(string : String?) : Calendar? {
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
    }

    private val config : ConfigManager = ConfigManager.getConfig(context)
    private val alarmManager : AlarmManager
    private val pendingIntent : PendingIntent
    private var locationManager : LocationManager? = null

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

        currentDaytime = getCurrentDaytime()

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, TimeToggleReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        setBrightness(context)
        scheduleNextCycle(context)

        if (config.advancedBrightness.autoTimes == true) {
            setUpLocationUpdates()
        }
    }

    private fun getCurrentDaytime() : Daytime {
        val currentTime = Calendar.getInstance()
        val offsetToSunrise = currentTime.compareTo(sunriseTime) // ( offset<0 -> before sunrise, offset>=0 -> after sunrise)
        val offsetToSunset = currentTime.compareTo(sunsetTime) // ( offset<0 -> before sunset, offset>=0 -> after sunset)
        return if (offsetToSunrise < 0 && offsetToSunset < 0)
            Daytime.Morning
        else if (offsetToSunrise >= 0 && offsetToSunset < 0) {
            Daytime.Day
        } else {
            Daytime.Night
        }
    }

    private val locationListener = LocationListener {
        val sunTimes = SunriseSunset.getSunriseSunset(Calendar.getInstance(), it.latitude, it.longitude)
        if (sunTimes.size < 2) {
            return@LocationListener
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

        val newDaytime = getCurrentDaytime()
        if (currentDaytime != newDaytime) {
            currentDaytime = newDaytime
            setBrightness(context)
        }
        scheduleNextCycle(context)
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationUpdates() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            50000f, //Only update times when travelled at least 50km from origin
            locationListener,
            context.mainLooper
        )
    }

    override fun trigger() {
        setBrightness(context)
    }

    override fun destroy() {
        locationManager?.removeUpdates(locationListener)
        alarmManager.cancel(pendingIntent)
        currentDaytime = Daytime.Initial
    }

}