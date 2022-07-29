package com.wits.pms.receiver;

import android.content.Context;
import com.wits.pms.bean.AutoKitMessage;
import com.wits.pms.statuscontrol.PowerManagerApp;

public class AutoKitCallBackImpl {
    public static final int AUTO_BOX_CONTROL_CMD_NEXT = 13;
    public static final int AUTO_BOX_CONTROL_CMD_PAUSE = 11;
    public static final int AUTO_BOX_CONTROL_CMD_PLAY = 10;
    public static final int AUTO_BOX_CONTROL_CMD_PLAYORPAUSE = 12;
    public static final int AUTO_BOX_CONTROL_CMD_PREV = 14;
    public static final int AUTO_BOX_MODE_ANDROIDAUTO_WORK_START = 1;
    public static final int AUTO_BOX_MODE_ANDROID_MIRROR_START = 15;
    public static final int AUTO_BOX_MODE_ANDROID_MIRROR_STOP = 16;
    public static final int AUTO_BOX_MODE_BACKCAR = 10;
    public static final int AUTO_BOX_MODE_BACKGROUND = 9;
    public static final int AUTO_BOX_MODE_CARPLAY_WORK_START = 5;
    public static final int AUTO_BOX_MODE_CONNECTING_PHONE = 11;
    public static final int AUTO_BOX_MODE_FOREGROUND = 8;
    public static final int AUTO_BOX_MODE_IPHONE_MIRROR_START = 17;
    public static final int AUTO_BOX_MODE_IPHONE_MIRROR_STOP = 18;
    public static final int AUTO_BOX_MODE_MUSIC_START = 19;
    public static final int AUTO_BOX_MODE_MUSIC_STOP = 20;
    public static final int AUTO_BOX_MODE_NAVI_REPORT_START = 6;
    public static final int AUTO_BOX_MODE_NAVI_REPORT_STOP = 7;
    public static final int AUTO_BOX_MODE_PHONECALL_START = 3;
    public static final int AUTO_BOX_MODE_PHONECALL_STOP = 4;
    public static final int AUTO_BOX_MODE_RELEASE_MIC = 13;
    public static final int AUTO_BOX_MODE_REQUEST_BLUETOOTH_MAC = 14;
    public static final int AUTO_BOX_MODE_REQUEST_BLUETOOTH_PIN_CODE = 21;
    public static final int AUTO_BOX_MODE_REQUEST_MIC = 12;
    public static final int AUTO_BOX_MODE_WORK_STOP = 2;
    public static final String AutoKitPkgName = "cn.manstep.phonemirrorBox";
    static final String TAG = "CenterCallBack";
    private static AutoKitCallBackImpl autoKitCallBack;

    public static boolean isUsing() {
        return PowerManagerApp.getStatusString("topApp").equals(AutoKitPkgName);
    }

    public static void drapUp(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 5);
        }
    }

    public static void drapDown(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 6);
        }
    }

    public static void drapLeft(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 3);
        }
    }

    public static void drapRight(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 4);
        }
    }

    public static void enter(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 7);
            AutoKitMessage.obtainMsgSendOut(mContext, 8);
        }
    }

    public static void acceptPhone(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 15);
        }
    }

    public static void rejectPhone(Context mContext) {
        if (isUsing()) {
            AutoKitMessage.obtainMsgSendOut(mContext, 16);
        }
    }

    public static void musicPause(Context mContext) {
        AutoKitMessage.obtainMsgSendOut(mContext, 11);
    }

    public static void musicPlay(Context mContext) {
        AutoKitMessage.obtainMsgSendOut(mContext, 10);
    }
}
