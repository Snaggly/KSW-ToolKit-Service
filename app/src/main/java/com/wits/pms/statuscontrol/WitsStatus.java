package com.wits.pms.statuscontrol;

import android.util.Log;

import com.google.gson.Gson;

import projekt.auto.mcu.ksw.model.McuStatus;
import projekt.auto.mcu.ksw.model.SystemStatus;

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

    public static void sendOutSystemStatus(SystemStatus systemStatus) {
        PowerManagerApp.sendStatus(new WitsStatus(1, new Gson().toJson(systemStatus)));
    }

    public static McuStatus.CarData getCarDataStatus() {
        return new Gson().fromJson(PowerManagerApp.getStatusString("carData"), McuStatus.CarData.class);
    }

    public static McuStatus.BenzData getBenzDataStatus() {
        return new Gson().fromJson(PowerManagerApp.getStatusString("benzData"), McuStatus.BenzData.class);
    }

    public static McuStatus.ACData getACDataStatus() {
        return new Gson().fromJson(PowerManagerApp.getStatusString("acData"), McuStatus.ACData.class);
    }

    public static String getMcuVersion() {
        return PowerManagerApp.getStatusString("mcuVerison");
    }

    public static String getTopApp() {
        String result = PowerManagerApp.getStatusString("topApp");
        if (result != null)
            return result;
        else
            return "";
    }

    public static SystemStatus getSystemStatus() {
        SystemStatus sysStatus = new SystemStatus();
        sysStatus.topApp = PowerManagerApp.getStatusString("topApp");
        sysStatus.screenSwitch = PowerManagerApp.getStatusInt("screenSwitch");
        sysStatus.lastMode = PowerManagerApp.getStatusInt("lastMode");
        sysStatus.ill = PowerManagerApp.getStatusInt("ill");
        sysStatus.acc = PowerManagerApp.getStatusInt("acc");
        sysStatus.epb = PowerManagerApp.getStatusInt("epb");
        sysStatus.ccd = PowerManagerApp.getStatusInt("ccd");
        sysStatus.dormant = PowerManagerApp.getStatusBoolean("dormant");
        sysStatus.rlight = PowerManagerApp.getStatusInt("rlight");
        return sysStatus;
    }

    public static void sendOutIll(int newVal) {
        PowerManagerApp.setSettingsInt("ill", newVal);
    }

    public static void sendOutEpb(int newVal) {
        PowerManagerApp.setSettingsInt("epb", newVal);
    }

    public static void setAcc(SystemStatus systemStatus, int newVal) {
        systemStatus.acc = newVal;
        PowerManagerApp.setSettingsInt("acc", newVal);
    }

    public static void setCcd(SystemStatus systemStatus, int newVal) {
        systemStatus.ccd = newVal;
        PowerManagerApp.setSettingsInt("ccd", newVal);
    }

    public static void setScreenSwitch(SystemStatus systemStatus, int newVal) {
        systemStatus.screenSwitch = newVal;
        PowerManagerApp.setSettingsInt("screenSwitch", newVal);
    }

    public static void sendOutMcuMedia(McuStatus.MediaData mediaData) {
        PowerManagerApp.setStatusString("mcuMediaJson", new Gson().toJson(mediaData));
    }

    public static void sendOutMcuBTStatus(McuStatus.CarBluetoothStatus bluetoothStatus) {
        PowerManagerApp.setStatusString("mcuBluetoothStatus", new Gson().toJson(bluetoothStatus));
    }

    public static void sendOutMcuMediaPlayStatus(McuStatus.MediaPlayStatus playStatus) {
        PowerManagerApp.setStatusString("mcuMediaPlayStatus", new Gson().toJson(playStatus));
    }

    public static void sendOutMcuDiscStatus(McuStatus.DiscStatus discStatus) {
        PowerManagerApp.setStatusString("mcuDiscStatus", new Gson().toJson(discStatus));
    }

    public static void sendOutMcuEqData(McuStatus.EqData eqData) {
        PowerManagerApp.setStatusString("mcuEqData", new Gson().toJson(eqData));
    }

    public static void setMcuVolumeLevel(int level) {
        PowerManagerApp.setStatusInt("mcu_volume_level", level);
    }

    public static void setMcuVolumeMute(boolean mute) {
        PowerManagerApp.setBooleanStatus("mcu_volume_mute", mute);
    }
}
