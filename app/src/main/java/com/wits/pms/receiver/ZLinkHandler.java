package com.wits.pms.receiver;

import android.content.Context;

import com.wits.pms.bean.ZlinkMessage;
import com.wits.pms.statuscontrol.PowerManagerApp;

public class ZLinkHandler {
    public static boolean isUsing() {
        return PowerManagerApp.getStatusString("topApp").equals(ZlinkMessage.ZLINK_NORMAL_ACTION);
    }

    private final Context mContext;

    public ZLinkHandler(Context mContext) {
        this.mContext = mContext;
    }

    public void back() {
        ZlinkMessage.sendZlinkMessage(4, mContext);
    }

    public void tiltUp() {
        ZlinkMessage.sendZlinkMessage(19, mContext);
    }

    public void tiltDown() {
        ZlinkMessage.sendZlinkMessage(20, mContext);
    }

    public void tiltLeft() {
        ZlinkMessage.sendZlinkMessage(1, mContext);
    }

    public void tiltRight() {
        ZlinkMessage.sendZlinkMessage(2, mContext);
    }

    public void knobPress() {
        ZlinkMessage.sendZlinkMessage(66, mContext);
    }

    public void mediaPrev() {
        ZlinkMessage.sendZlinkMessage(88, mContext);
    }

    public void mediaNext() {
        ZlinkMessage.sendZlinkMessage(87, mContext);
    }

    public void playPause() {
        ZlinkMessage.sendZlinkMessage(85, mContext);
    }

    public void telPick() {
        ZlinkMessage.sendZlinkMessage(5, mContext);
    }

    public void telHangUp() {
        ZlinkMessage.sendZlinkMessage(6, mContext);
    }

    public void navigation() {
        ZlinkMessage.sendZlinkMessage(1504, mContext);
    }

    public void turnRight() {
        ZlinkMessage.sendZlinkMessage(1502, mContext);
    }

    public void turnLeft() {
        ZlinkMessage.sendZlinkMessage(1501, mContext);
    }

    public void voiceAssist() {
        ZlinkMessage.sendZlinkMessage(1500, mContext);
    }

    public void setDarkTheme() {
        new ZlinkMessage("com.zjinnova.zlink.action.OUT_DARK_START").sendBroadCast(mContext);
    }

    public void setLightTheme() {
        new ZlinkMessage("com.zjinnova.zlink.action.OUT_DARK_STOP").sendBroadCast(mContext);
    }
}
