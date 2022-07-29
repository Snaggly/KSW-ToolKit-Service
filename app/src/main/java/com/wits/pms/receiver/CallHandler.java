package com.wits.pms.receiver;

import android.content.Context;
import android.os.Bundle;
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

    private static void zlinkHandleAutoCall() {
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
                return;
        }
    }

    private static void zlinkHandleCall(Context context) {
        String callStatus = SystemProperties.get(ZlinkMessage.ZLINK_CALL);
        Log.d("CenterControl", "zlinkHandleCall  callStatus = " + callStatus);
        if (callStatus.equals("1")) {
            zlinkCallOn(context);
        } else if (callStatus.equals("2")) {
            zlinkCallOff(context);
        }
    }

    private static void zlinkCallOn(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("command", "REQ_SPEC_FUNC_CMD");
        bundle.putInt("specFuncCode", 5);
        new ZlinkMessage(ZlinkMessage.ZLINK_NORMAL_ACTION, "REQ_SPEC_FUNC_CMD", bundle).sendBroadCast(context);
    }

    private static void zlinkCallOff(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("command", "REQ_SPEC_FUNC_CMD");
        bundle.putInt("specFuncCode", 6);
        new ZlinkMessage(ZlinkMessage.ZLINK_NORMAL_ACTION, "REQ_SPEC_FUNC_CMD", bundle).sendBroadCast(context);
    }

    public static void handleAccept(Context context) {
        if (!SystemProperties.get(ZlinkMessage.ZLINK_CONNECT).equals("1") && !SystemProperties.get(ZlinkMessage.ZLINK_CARPLAY_WRIED_CONNECT).equals("1")) {
            if (!SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_AUTO_CONNECT).equals("1") && !SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_MIRROR_CONNECT).equals("1")) {
                if (!SystemProperties.get(AutoKitMessage.AUTOBOX_CONNECT).equals("1")) {
                    if ("true".equals(SystemProperties.get("persist.sys.hicar_connect"))) {
                        WitsCommand.sendCommand(7, 111);
                    } else {
                        acceptPhone();
                    }
                } else {
                    AutoKitCallBackImpl.acceptPhone(context);
                }
            } else {
                zlinkHandleAutoCall();
            }
        } else {
            zlinkHandleCall(context);
        }
    }

    public static void handleReject(Context context) {
        if (!SystemProperties.get(ZlinkMessage.ZLINK_CONNECT).equals("1") && !SystemProperties.get(ZlinkMessage.ZLINK_CARPLAY_WRIED_CONNECT).equals("1")) {
            if (!SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_AUTO_CONNECT).equals("1") && !SystemProperties.get(ZlinkMessage.ZLINK_ANDROID_MIRROR_CONNECT).equals("1")) {
                if (!SystemProperties.get(AutoKitMessage.AUTOBOX_CONNECT).equals("1")) {
                    if ("true".equals(SystemProperties.get("persist.sys.hicar_connect"))) {
                        WitsCommand.sendCommand(7, 112);
                    } else {
                        handUpPhone();
                    }
                } else {
                    AutoKitCallBackImpl.rejectPhone(context);
                }
            } else {
                zlinkHandleAutoCall();
            }
        } else {
            zlinkHandleCall(context);
        }
    }
}
