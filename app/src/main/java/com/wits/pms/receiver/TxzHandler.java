package com.wits.pms.receiver;

import android.content.Context;

import com.wits.pms.bean.TxzMessage;
import com.wits.pms.statuscontrol.PowerManagerApp;
import com.wits.pms.utils.SystemProperties;

public class TxzHandler {
    public static void closeSpeech(Context context) {
        new TxzMessage(2400, "txz.window.close", null).sendBroadCast(context);
    }

    public static void openSpeech(Context context) {
        new TxzMessage(2400, "txz.window.open", null).sendBroadCast(context);
    }

    public static boolean isUsing() {
        return PowerManagerApp.getSettingsInt("Support_TXZ") == 1;
    }

    public static boolean isShowing() {
        return SystemProperties.get(TxzMessage.TXZ_SHOW_STATUS).equals("1");
    }
}
