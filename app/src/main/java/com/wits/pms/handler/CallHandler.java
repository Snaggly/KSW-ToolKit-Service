package com.wits.pms.handler;

import android.content.Context;
import android.util.Log;

import com.wits.pms.bean.AutoKitMessage;
import com.wits.pms.bean.ZlinkMessage;
import com.wits.pms.statuscontrol.PowerManagerApp;
import com.wits.pms.statuscontrol.WitsCommand;
import com.wits.pms.utils.SystemProperties;

public class CallHandler {
    private static void handUpPhone() {
        boolean isConnected = PowerManagerApp.getStatusBoolean("isConnected");
        if (isConnected) {
            WitsCommand.sendCommand(3, 109, "");
        }
    }

    private static void acceptPhone() {
        boolean isConnected = PowerManagerApp.getStatusBoolean("isConnected");
        if (isConnected) {
            WitsCommand.sendCommand(3, 112, "");
        }
    }

    private static void zlinkHandleAutoCall(Context context) {
        int callStatus = 0;
        callStatus = PowerManagerApp.getStatusInt("callStatus");

        switch (callStatus) {
            case 4:
            case 6:
                handUpPhone();
                return;
            case 5:
                acceptPhone();
                return;
            default:
                zlinkCallOn(context);
        }
    }

    private static void zlinkHandleCall(Context context) {
        String callStatus = SystemProperties.get(ZlinkMessage.ZLINK_CALL);
        Log.d("CenterControl", "zlinkHandleCall  callStatus = " + callStatus);
        if (callStatus.equals("2")) {
            zlinkCallOff(context);
        } else {
            zlinkCallOn(context);
        }
    }

    private static void zlinkCallOn(Context context) {
        new ZLinkHandler(context).telPick();
    }

    private static void zlinkCallOff(Context context) {
        new ZLinkHandler(context).telHangUp();
    }

    public static void handleAccept(Context context) {
        if (SystemProperties.get(ZlinkMessage.ZLINK_CONNECT).equals("1") || SystemProperties.get(ZlinkMessage.ZLINK_CARPLAY_WRIED_CONNECT).equals("1")) {
            zlinkHandleCall(context);
        }
        else if (SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_AUTO_CONNECT).equals("1") || SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_MIRROR_CONNECT).equals("1")) {
            zlinkHandleAutoCall(context);
        }
        else if (SystemProperties.get(AutoKitMessage.AUTOBOX_CONNECT).equals("1")) {
            AutoKitCallBackImpl.acceptPhone(context);
        }
        else if (HiCarHandler.isUsing()) {
            HiCarHandler.acceptCall();
        } else {
            acceptPhone();
        }
    }

    public static void handleReject(Context context) {
        if (SystemProperties.get(ZlinkMessage.ZLINK_CONNECT).equals("1") || SystemProperties.get(ZlinkMessage.ZLINK_CARPLAY_WRIED_CONNECT).equals("1")) {
            zlinkHandleCall(context);
        }
        else if (SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_AUTO_CONNECT).equals("1") || SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_MIRROR_CONNECT).equals("1")) {
            zlinkHandleAutoCall(context);
        }
        else if (SystemProperties.get(AutoKitMessage.AUTOBOX_CONNECT).equals("1")) {
            AutoKitCallBackImpl.rejectPhone(context);
        }
        else if (HiCarHandler.isUsing()) {
            HiCarHandler.rejectCall();
        } else {
            handUpPhone();
        }
    }
}
