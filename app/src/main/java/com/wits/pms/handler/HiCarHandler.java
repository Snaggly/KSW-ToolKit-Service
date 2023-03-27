package com.wits.pms.handler;

import com.wits.pms.statuscontrol.WitsCommand;
import com.wits.pms.utils.SystemProperties;

public class HiCarHandler {
    public static boolean isUsing() {
        return SystemProperties.get("persist.sys.hicar_connect").equals("true");
    }

    public static void acceptCall() {
        WitsCommand.sendCommand(7, 111);
    }

    public static void rejectCall() {
        WitsCommand.sendCommand(7, 112);
    }
}
