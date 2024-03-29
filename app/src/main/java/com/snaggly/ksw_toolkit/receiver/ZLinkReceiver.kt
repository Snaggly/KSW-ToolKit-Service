package com.snaggly.ksw_toolkit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wits.pms.bean.ZlinkMessage
import com.wits.pms.handler.ZLinkHandler
import com.wits.pms.utils.SystemProperties

class ZLinkReceiver : BroadcastReceiver(), ICustomReceiver<ZLinkReceiver.ZLinkData> {
    enum class Connection {
        Disconnected,
        CarPlayWired,
        CarPlayWireless,
        AirPlayWired,
        AirPlayWireless,
        AndroidAutoWired,
        AndroidAutoWireless,
        AndroidMirrorWired,
        AndroidMirrorWireless,
        Unknown
    }

    data class ZLinkData(
        var currentConnection: Connection,
        var isShowing: Boolean,
        var isCalling: Boolean,
        var isRinging: Boolean,
        var mainAudio: Boolean,
        var siriOn: Boolean //Interesting quirk: During a phone call this turns on
    )
    /*
    Calling flows as:
    PHONE_RING_ON
    PHONE_CALL_ON
    MAIN_AUDIO_START (<- up to here ringer still going- not actually calling)
    PHONE_RING_OFF (<- clicked on accept)
    SIRI_ON
    SIRI_OFF (<- clicked on hang up)
    PHONE_CALL_OFF
    MAIN_AUDIO_STOP
     */

    companion object {
        val CurrentDataSet = ZLinkData(
            currentConnection = if (SystemProperties.get(ZlinkMessage.ZLINK_CONNECT) == "1") Connection.CarPlayWireless
            else if (SystemProperties.get(ZlinkMessage.ZLINK_CARPLAY_WRIED_CONNECT) == "1") Connection.CarPlayWired
            else if (SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_AUTO_CONNECT) == "1") Connection.AndroidAutoWired
            else if (SystemProperties.get(ZlinkMessage.ZLINK_AIRPLAY_WIRED_CONNECT) == "1") Connection.AirPlayWired
            else if (SystemProperties.get(ZlinkMessage.ZLINK_AIRPLAY_WIRED_CONNECT) == "0") Connection.AirPlayWireless
            else if (SystemProperties.get(ZlinkMessage.ZLINK_AIRPLAY_CONNECT) == "1") Connection.AirPlayWireless
            else if (SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_MIRROR_CONNECT) == "1") Connection.AndroidMirrorWired
            else Connection.Disconnected,
            isShowing = ZLinkHandler.isUsing(),
            isCalling = ZLinkHandler.isCalling(),
            isRinging = ZLinkHandler.isRinging(),
            mainAudio = false,
            siriOn = false
        )
    }

    private var callBackHandler: ((ZLinkData) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ZlinkMessage.ZLINK_NORMAL_ACTION)
            return

        val message = ZlinkMessage(intent)

        if (message.status != null) {
            when (message.status) {
                "DISCONNECT" -> {
                    CurrentDataSet.currentConnection = Connection.Disconnected
                }

                "CONNECTED" -> {
                    CurrentDataSet.currentConnection =
                        when (message.bundle.getString("phoneMode")) {
                            "carplay_wired" -> Connection.CarPlayWired
                            "carplay_wireless" -> Connection.CarPlayWireless
                            "airplay_wired" -> Connection.AirPlayWired
                            "airplay_wireless" -> Connection.AirPlayWireless
                            "auto_wired" -> Connection.AndroidAutoWired
                            "auto_wireless" -> Connection.AndroidAutoWireless
                            "android_mirror_wired" -> Connection.AndroidMirrorWired
                            "android_mirror_wireless" -> Connection.AndroidMirrorWireless
                            else -> Connection.Unknown
                        }
                }

                "MAIN_PAGE_SHOW" -> {
                    CurrentDataSet.isShowing = true
                }

                "MAIN_PAGE_HIDDEN" -> {
                    CurrentDataSet.isShowing = false
                }
                // Following don't seem to work for Android Auto!
                "PHONE_CALL_ON" -> CurrentDataSet.isCalling = true
                "PHONE_CALL_OFF" -> CurrentDataSet.isCalling = false
                "PHONE_RING_ON" -> CurrentDataSet.isRinging = true
                "PHONE_RING_OFF" -> CurrentDataSet.isRinging = false
                "MAIN_AUDIO_START" -> CurrentDataSet.mainAudio = true
                "MAIN_AUDIO_STOP" -> CurrentDataSet.mainAudio = false
            }
        } else if (message.command != null) {
            when (message.command) {
                "SIRI_ON" -> CurrentDataSet.siriOn = true
                "SIRI_OFF" -> CurrentDataSet.siriOn = false
            }
        }

        callBackHandler?.let { it(CurrentDataSet) }
    }

    override fun setReceiverHandler(handler: ((ZLinkData) -> Unit)?) {
        callBackHandler = handler
    }
}