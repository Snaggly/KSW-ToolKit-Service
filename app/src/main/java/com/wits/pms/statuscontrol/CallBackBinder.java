package com.wits.pms.statuscontrol;

import android.util.Log;

import com.wits.pms.custom.ICallBackController;

public class CallBackBinder {
    private static ICallBackController controllerInstance;

    public static ICallBackController getServiceCallbackController() {
        if (controllerInstance == null) {
            controllerInstance = ICallBackController.Stub.asInterface(PowerManagerApp.getService("wits_callback"));
        }
        return controllerInstance;
    }

    public static void handleLRReverse(int signalRight, int signalLeft) {
        try {
            getServiceCallbackController().getICallBack().onLRReverse((signalRight >> 4) + ((signalLeft >> 3) * 2));
        } catch (Exception e) {
            Log.e("CallBackBinder", "Exception in invoking remote method\n"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void handleReverse(int ccd) {
        try {
            getServiceCallbackController().getICallBack().onReverse(ccd);
        } catch (Exception e) {
            Log.e("CallBackBinder", "Exception in invoking remote method\n"+e.getMessage());
            e.printStackTrace();
        }
    }
}
