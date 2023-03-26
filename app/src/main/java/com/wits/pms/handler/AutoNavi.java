package com.wits.pms.handler;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.wits.pms.statuscontrol.WitsStatus;

public class AutoNavi {
    private static void sendBroadcast(int extraOperaInt, Context context) throws RemoteException {
        if (WitsStatus.getTopApp().contains("autonavi")) {
            Intent intent = new Intent();
            intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
            intent.putExtra("KEY_TYPE", 10027);
            intent.putExtra("EXTRA_TYPE", 1);
            intent.putExtra("EXTRA_OPERA", extraOperaInt);
            context.sendBroadcast(intent);
        }
    }

    public static void dragRight(Context context) throws ExceptionInInitializerError, RemoteException{
        sendBroadcast(0, context);
    }

    public static void dragLeft(Context context) throws ExceptionInInitializerError, RemoteException{
        sendBroadcast(1, context);
    }
}
