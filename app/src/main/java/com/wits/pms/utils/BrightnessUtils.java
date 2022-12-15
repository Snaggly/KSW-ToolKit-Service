package com.wits.pms.utils;

public class BrightnessUtils {
    private static final double A = 0.17883277;
    private static final double B = 0.28466892;
    private static final double C = 0.5599107;
    public static final int GAMMA_SPACE_MAX = 1023;
    private static final double R = 0.5;

    public static final int convertGammaToLinear(int val, int min, int max) {
        double ret;
        double normalizedVal = (double) val / 1023.0;
        if (normalizedVal <= R) {
            ret = Math.sqrt(normalizedVal / R);
        } else {
            ret = Math.exp((normalizedVal - C) / A) + B;
        }
        return (int) Math.round((((double) max - (double) min) * (ret / 12.0)) + (double) min);
    }

    public static final int convertLinearToGamma(int val, int min, int max) {
        double ret;
        double normalizedVal = (((double) val - (double) min) / ((double) max - (double) min)) * 12.0;
        if (normalizedVal <= 1.0) {
            ret = Math.sqrt(normalizedVal) * R;
        } else {
            ret = C + (Math.log(normalizedVal - B) * A);
        }
        return (int) Math.round(1023.0 * ret);
    }

    public static double getPercentage(double value, int min, int max) {
        if (value > max) {
            return 1.0;
        }
        if (value < min) {
            return 0.0;
        }
        return (value - min) / (max - min);
    }
}