package com.wits.pms.bean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;

import com.wits.pms.utils.CenterServiceManager;

public class AutoKitMessage {
    public static final String AUTOBOX_CALL = "vendor.wits.autobox.call";
    public static final String AUTOBOX_CALL_ING = "2";
    public static final String AUTOBOX_CALL_OFF = "0";
    public static final String AUTOBOX_CALL_ON = "1";
    public static final String AUTOBOX_CONNECT = "vendor.wits.autobox.connected";
    public static final String AUTO_BOX_CONTROL_CMD_DATA = "cn.manstep.phonemirrorBox.AUTO_BOX_CONTROL_CMD_DATA";
    public static final String AUTO_BOX_CONTROL_CMD_DATA_STRING = "cn.manstep.phonemirrorBox.AUTO_BOX_CONTROL_CMD_DATA_STRING";
    public static final String AUTO_BOX_CONTROL_CMD_EVT = "cn.manstep.phonemirrorBox.AUTO_BOX_CONTROL_CMD_EVT";
    public static final String AUTO_BOX_MODE_CHANGE_DATA = "cn.manstep.phonemirrorBox.AUTO_BOX_MODE_CHANGE_DATA";
    public static final String AUTO_BOX_RECEIVE_ACTION = "cn.manstep.phonemirrorBox.AUTO_BOX_MODE_CHANGE_EVT";
    public String action;
    public Bundle bundle;
    public Context context;
    public int receiveKey;

    public AutoKitMessage(Context context, String action, Bundle bundle) {
        this.action = action;
        this.bundle = bundle;
        this.context = context;
    }

    public AutoKitMessage(Intent intent) {
        this.action = intent.getAction();
        this.bundle = intent.getExtras();
        if (this.bundle != null) {
            this.receiveKey = this.bundle.getInt(AUTO_BOX_MODE_CHANGE_DATA);
        }
    }

    public static void obtainMsgSendOut(Context context, int dataInt, String dataString) {
        Bundle bundle = new Bundle();
        bundle.putInt(AUTO_BOX_CONTROL_CMD_DATA, dataInt);
        bundle.putString(AUTO_BOX_CONTROL_CMD_DATA_STRING, dataString);
        new AutoKitMessage(context, AUTO_BOX_CONTROL_CMD_EVT, bundle).sendBroadCast();
    }

    public static void obtainMsgSendOut(Context context, int dataInt) {
        Bundle bundle = new Bundle();
        bundle.putInt(AUTO_BOX_CONTROL_CMD_DATA, dataInt);
        new AutoKitMessage(context, AUTO_BOX_CONTROL_CMD_EVT, bundle).sendBroadCast();
    }

    public void sendBroadCast() {
        Intent intent = new Intent();
        intent.putExtras(this.bundle);
        intent.setAction(this.action);
        Log.v("AutoKitMessage", "action: " + intent.getStringExtra("action"));
        this.context.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(CenterServiceManager.getUid(context)));
    }

    public String toString() {
        return "receiveKey:" + this.receiveKey + " - action:" + this.action + "-" + this.bundle.toString();
    }
}
