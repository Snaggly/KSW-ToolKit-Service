package com.snaggly.ksw_toolkit.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.Toast
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.wits.pms.statuscontrol.PowerManagerApp


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent!!.action) {
            try {
                val armMediaVol = PowerManagerApp.getManager().getSettingsInt("Android_media_vol")
                val armPhoneVol = PowerManagerApp.getManager().getSettingsInt("Android_phone_vol")
                val carPhoneVol = PowerManagerApp.getManager().getSettingsInt("Car_phone_vol")
                val carNaviVol = PowerManagerApp.getManager().getSettingsInt("Car_navi_vol")

                PowerManagerApp.getManager().setSettingsInt("Android_media_vol", armPhoneVol)
                PowerManagerApp.getManager().setSettingsInt("Android_phone_vol", armMediaVol)
                PowerManagerApp.getManager().setSettingsInt("Car_phone_vol", carPhoneVol)
                PowerManagerApp.getManager().setSettingsInt("Car_navi_vol", carNaviVol)
            } catch (ex : Exception) {
                Toast.makeText(context, "Unable to connect to Wits!", Toast.LENGTH_LONG).show()
            }

            val config = ConfigManager.getConfig(context?.filesDir!!.absolutePath)

            if (config.systemTweaks.maxVolume.data) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_ACCESSIBILITY, audioManager.getStreamMaxVolume(AudioManager.STREAM_ACCESSIBILITY), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_DTMF, audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0)
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0)
            }

            if (config.systemTweaks.startAtBoot.data) {
                context.startForegroundService(Intent(context, CoreService::class.java))
            }
        }
    }
}