package com.snaggly.ksw_toolkit.util.manager

import android.content.Context
import android.media.AudioManager
import com.wits.pms.statuscontrol.PowerManagerApp

class AndroidVolumeManager(context: Context) {
    class WitsConnectionException : Exception()

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val sharedPref = context.getSharedPreferences(this::javaClass.name, Context.MODE_PRIVATE)

    @Throws(WitsConnectionException::class)
    fun restoreSavedVolumes() {
        try {
            val armMediaVol = PowerManagerApp.getManager().getSettingsInt("Android_media_vol")
            val armPhoneVol = PowerManagerApp.getManager().getSettingsInt("Android_phone_vol")
            val carPhoneVol = PowerManagerApp.getManager().getSettingsInt("Car_phone_vol")
            val carNaviVol = PowerManagerApp.getManager().getSettingsInt("Car_navi_vol")

            PowerManagerApp.getManager().setSettingsInt("Android_media_vol", armMediaVol)
            PowerManagerApp.getManager().setSettingsInt("Android_phone_vol", armPhoneVol)
            PowerManagerApp.getManager().setSettingsInt("Car_phone_vol", carPhoneVol)
            PowerManagerApp.getManager().setSettingsInt("Car_navi_vol", carNaviVol)
        } catch (ex: Exception) {
            throw WitsConnectionException()
        }

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            sharedPref.getInt("MUSIC", audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_NOTIFICATION,
            sharedPref.getInt("NOTIFICATION", audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_ACCESSIBILITY,
            sharedPref.getInt("ACCESSIBILITY", audioManager.getStreamMaxVolume(AudioManager.STREAM_ACCESSIBILITY)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            sharedPref.getInt("ALARM", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_DTMF,
            sharedPref.getInt("DTMF", audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            sharedPref.getInt("RING", audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_SYSTEM,
            sharedPref.getInt("SYSTEM", audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)),
            0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            sharedPref.getInt("VOICE_CALL", audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)),
            0)
    }

    fun saveCurrentVolumes() {
        sharedPref.edit()
            .putInt("MUSIC", audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
            .putInt("NOTIFICATION", audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION))
            .putInt("ACCESSIBILITY", audioManager.getStreamVolume(AudioManager.STREAM_ACCESSIBILITY))
            .putInt("ALARM", audioManager.getStreamVolume(AudioManager.STREAM_ALARM))
            .putInt("DTMF", audioManager.getStreamVolume(AudioManager.STREAM_DTMF))
            .putInt("RING", audioManager.getStreamVolume(AudioManager.STREAM_RING))
            .putInt("SYSTEM", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM))
            .putInt("VOICE_CALL", audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL))
            .apply()
    }
}