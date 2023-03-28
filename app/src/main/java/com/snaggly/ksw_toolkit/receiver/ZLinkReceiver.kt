package com.snaggly.ksw_toolkit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wits.pms.bean.ZlinkMessage
import com.wits.pms.handler.ZLinkHandler
import com.wits.pms.statuscontrol.PowerManagerApp
import com.wits.pms.utils.SystemProperties

class ZLinkReceiver : BroadcastReceiver(), ICustomReceiver<ZLinkReceiver.ZLinkData>{
    enum class Connection {
        Disconnected,
        CarPlayWired,
        CarPlayWireless,
        AirPlayWired,
        AirPlayWireless,
        AndroidAutoWired,
        AndroidAutoWireless,
        AndroidMirrorWired,
        AndroidMirrorWireless
    }

    data class ZLinkData(var currentConnection: Connection, var isShowing: Boolean, var isCalling: Boolean)

    companion object {
        val CurrentDataSet : ZLinkData
        get() {
            return ZLinkData(
                currentConnection = if (SystemProperties.get(ZlinkMessage.ZLINK_CONNECT) == "1") Connection.CarPlayWireless
                else if (SystemProperties.get(ZlinkMessage.ZLINK_CARPLAY_WRIED_CONNECT) == "1") Connection.CarPlayWired
                else if (SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_AUTO_CONNECT) == "1") Connection.AndroidAutoWired
                else if (SystemProperties.get(ZlinkMessage.ZLINK_AIRPLAY_WIRED_CONNECT) == "1") Connection.AirPlayWired
                else if (SystemProperties.get(ZlinkMessage.ZLINK_AIRPLAY_WIRED_CONNECT) == "0") Connection.AirPlayWireless
                else if (SystemProperties.get(ZlinkMessage.ZLINK_AIRPLAY_CONNECT) == "1") Connection.AirPlayWireless
                else if (SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_MIRROR_CONNECT) == "1") Connection.AndroidMirrorWired
                else Connection.Disconnected,
                isShowing = ZLinkHandler.isUsing(),
                isCalling = ZLinkHandler.isCalling()
            )
        }
    }

    private var callBackHandler : ((ZLinkData) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ZlinkMessage.ZLINK_NORMAL_ACTION)
            return

        val message = ZlinkMessage(intent)

        when (message.status) {
            "DISCONNECT" -> {
                dataSet.currentConnection = Connection.Disconnected
            }
            "CONNECTED" -> {
                dataSet.currentConnection = when (message.bundle.getString("phoneMode")) {
                    "carplay_wired" -> Connection.CarPlayWired
                    "carplay_wireless" -> Connection.CarPlayWireless
                    "airplay_wired" -> Connection.AirPlayWired
                    "airplay_wireless" -> Connection.AirPlayWireless
                    "auto_wired" -> Connection.AndroidAutoWired
                    "auto_wireless" -> Connection.AndroidAutoWireless
                    "android_mirror_wired" -> Connection.AndroidMirrorWired
                    "android_mirror_wireless" -> Connection.AndroidMirrorWireless
                    else -> Connection.CarPlayWired
                }
            }
            "MAIN_PAGE_SHOW" -> {
                dataSet.isShowing = true
            }
            "MAIN_PAGE_HIDDEN" -> {
                dataSet.isShowing = false
            }
            "PHONE_CALL_ON" -> dataSet.isCalling = true //Doesn't seem to work for Android Auto!
            "PHONE_CALL_OFF" -> dataSet.isCalling = false
        }

        callBackHandler?.let { it(dataSet) }
    }

    override fun setReceiverHandler(handler: (ZLinkData) -> Unit) {
        callBackHandler = handler
    }
}