package com.wits.pms.bean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wits.pms.utils.CenterServiceManager;

public class TxzMessage {
    public static final String TXZ_DISMISS = "0";
    public static final String TXZ_SHOW = "1";
    public static final String TXZ_SHOW_STATUS = "vendor.wits.txz.status";
    public String action;
    public Bundle bundle;
    public int keyType;

    public TxzMessage(int keyType, String action, Bundle bundle) {
        bundle = bundle == null ? new Bundle() : bundle;
        this.action = action;
        this.keyType = keyType;
        bundle.putInt("key_type", keyType);
        bundle.putString("action", action);
        this.bundle = bundle;
    }

    public TxzMessage(Intent intent) {
        this.keyType = intent.getIntExtra("key_type", 0);
        this.action = intent.getStringExtra("action");
        this.bundle = intent.getExtras();
    }

    public void sendBroadCast(Context context) {
        Intent txzIntent = new Intent();
        txzIntent.putExtras(this.bundle);
        txzIntent.setAction("com.txznet.adapter.recv");
        Log.v("TxzMessage", "keyType: " + txzIntent.getIntExtra("key_type", 0) + " - action: " + txzIntent.getStringExtra("action"));
        context.sendBroadcastAsUser(txzIntent, UserHandle.getUserHandleForUid(CenterServiceManager.getUid(context)));
    }

    @NonNull
    public String toString() {
        return "keyType:" + this.keyType + " - action:" + this.action + "-" + this.bundle.toString();
    }
}
