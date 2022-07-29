package com.wits.pms.utils;

public class Utils {
    public static int getIndexValue2DataNew(byte data, int index, int length) {
        if (index < 0 || index > 7) {
            return data;
        }
        if (length < 1) {
            return data;
        }
        int calcLength = Math.min(length, 8);
        int calcData = 0;
        for (int i = 0; i < calcLength; i++) {
            calcData |= 1 << i;
        }
        return ((data & 255) >> index) & calcData;
    }
}
