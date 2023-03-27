package com.wits.pms.handler;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.wits.pms.statuscontrol.PowerManagerApp;
import com.wits.pms.statuscontrol.WitsStatus;

import java.util.Objects;

public class AutoNavi {
    public static final String AutoNaviPkgName = "com.autonavi.amapauto";
    private static void sendBroadcast(int extraOperaInt, Context context) throws RemoteException {
        if (isUsing()) {
            Intent intent = new Intent();
            intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
            intent.putExtra("KEY_TYPE", 10027);
            intent.putExtra("EXTRA_TYPE", 1);
            intent.putExtra("EXTRA_OPERA", extraOperaInt);
            context.sendBroadcast(intent);
        }
    }

    public static boolean isUsing() {
        return Objects.equals(PowerManagerApp.getStatusString("topApp"), AutoNaviPkgName);
    }

    public static void dragRight(Context context) throws ExceptionInInitializerError, RemoteException{
        sendBroadcast(0, context);
    }

    public static void dragLeft(Context context) throws ExceptionInInitializerError, RemoteException{
        sendBroadcast(1, context);
    }
}
