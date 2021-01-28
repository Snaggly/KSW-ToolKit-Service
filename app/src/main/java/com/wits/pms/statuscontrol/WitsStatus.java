package com.wits.pms.statuscontrol;

import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;

import projekt.auto.mcu.ksw.model.McuStatus;

public class WitsStatus {
    public String jsonArg;
    public int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        this.type = type2;
    }

    public String getJsonArg() {
        return this.jsonArg;
    }

    public void setJsonArg(String jsonArg2) {
        this.jsonArg = jsonArg2;
    }

    public static WitsStatus getWitsStatusFormJson(String jsonArg2) {
        return new Gson().fromJson(jsonArg2, WitsStatus.class);
    }

    public WitsStatus(int type2, String jsonArg2) {
        this.type = type2;
        this.jsonArg = jsonArg2;
    }

    public static void logMcuStatus(McuStatus mcuStatus) {
        Log.i("IPowerManagerAppService", "sendStatus Msg: " + new Gson().toJson(new WitsStatus(5, new Gson().toJson(mcuStatus))));
    }

    public static void sendOutMcuStatus(McuStatus mcuStatus) {
        PowerManagerApp.sendStatus(new WitsStatus(5, new Gson().toJson(mcuStatus)));
    }

    public static McuStatus.CarData getCarDataStatus() throws RemoteException {
        return new Gson().fromJson(PowerManagerApp.getManager().getStatusString("carData"), McuStatus.CarData.class);
    }

    public static McuStatus.BenzData getBenzDataStatus() throws RemoteException {
        return new Gson().fromJson(PowerManagerApp.getManager().getStatusString("benzData"), McuStatus.BenzData.class);
    }

    public static McuStatus.ACData getACDataStatus() throws RemoteException {
        return new Gson().fromJson(PowerManagerApp.getManager().getStatusString("acData"), McuStatus.ACData.class);
    }
}
