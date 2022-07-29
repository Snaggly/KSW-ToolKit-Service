package com.wits.pms.utils;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;

public class AccLight {
    private static final String ACC_CHANGE_PATH = "/sys/class/leds/led_p/brightness";
    public static long mDelay;
    public static boolean showing;
    private static final int last = 0;
    private static final Runnable accLightRunnable = AccLight::run;

    public static void show(long delay) {
        mDelay = delay;
        if (!showing) {
            showing = true;
            new Thread(accLightRunnable).start();
        }
    }

    private static void run() {
        while (true) {
            try {
                Thread.sleep(AccLight.mDelay);
            } catch (InterruptedException ignored) {
            }
            try {
                File awakeTimeFile = new File(AccLight.ACC_CHANGE_PATH);
                FileWriter fr = new FileWriter(awakeTimeFile);
                fr.write(AccLight.last + "");
                fr.close();
            } catch (Exception e2) {
                Log.e("AccLight", "write failed. cause:", e2);
            }
        }
    }
}
