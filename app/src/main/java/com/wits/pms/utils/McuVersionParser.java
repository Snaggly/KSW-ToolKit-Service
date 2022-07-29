package com.wits.pms.utils;

import com.wits.pms.statuscontrol.WitsStatus;

import projekt.auto.mcu.ksw.model.McuStatus;

public class McuVersionParser {
    public static void parseMcuVersion(McuStatus mcuStatus, byte[] data) {
        byte[] mcub = new byte[40];
        System.arraycopy(data, 0, mcub, 0, mcub.length);
        mcuStatus.mcuVerison = new String(mcub);
        WitsStatus.sendOutMcuStatus(mcuStatus);
    }
}
