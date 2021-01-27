package com.wits.pms.statuscontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wits.pms.ICmdListener;
import com.wits.pms.IContentObserver;
import com.wits.pms.IPowerManagerAppService;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerManagerApp {
    private static ICmdListener cmdListener;
    private static Context context;
    private static final HashMap<String, IContentObserver> maps = new HashMap<>();

    public static void init(Context context2) {
        context = context2;
        context2.getContentResolver().registerContentObserver(Settings.System.getUriFor("bootTimes"), true, new ContentObserver(new Handler(context2.getMainLooper())) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (PowerManagerApp.cmdListener != null) {
                    PowerManagerApp.registerICmdListener(PowerManagerApp.cmdListener);
                }
                for (String key : PowerManagerApp.maps.keySet()) {
                    PowerManagerApp.registerIContentObserver(key, (IContentObserver) PowerManagerApp.maps.get(key));
                }
            }
        });
    }

    public static IPowerManagerAppService getManager() {
        IPowerManagerAppService service = IPowerManagerAppService.Stub.asInterface(getService("wits_pms"));
        Log.d("Snaggly", service.toString());
        return service;
    }

    public static void registerICmdListener(ICmdListener listener) {
        try {
            cmdListener = listener;
            if (getManager() != null) {
                getManager().registerCmdListener(listener);
            }
        } catch (RemoteException e) {
        }
    }

    public static void registerIContentObserver(String key, IContentObserver contentObserver) {
        Log.i("IPowerManagerService", contentObserver.getClass().getName());
        try {
            maps.put(key, contentObserver);
            if (getManager() != null) {
                getManager().registerObserver(key, contentObserver);
            }
        } catch (RemoteException e) {
        }
    }

    public static void unRegisterIContentObserver(IContentObserver contentObserver) {
        try {
            for (String key : maps.keySet()) {
                if (maps.get(key) == contentObserver) {
                    maps.remove(key, contentObserver);
                }
            }
            if (getManager() != null) {
                getManager().unregisterObserver(contentObserver);
            }
        } catch (RemoteException e) {
        }
    }

    @SuppressLint({"PrivateApi"})
    public static IBinder getService(String serviceName) {
        try {
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            return (IBinder) serviceManager.getMethod("getService", new Class[]{String.class}).invoke(serviceManager, new Object[]{serviceName});
        } catch (Exception e) {
            String name = PowerManagerApp.class.getName();
            Log.e(name, "error service init - " + serviceName, e);
            return null;
        }
    }

    public static boolean sendCommand(String jsonMsg) {
        try {
            return getManager().sendCommand(jsonMsg);
        } catch (RemoteException e) {
            Log.i(getManager().getClass().getName(), "error sendCommand", e);
            return false;
        }
    }

    public static void sendStatus(WitsStatus witsStatus) {
        if (getManager() != null) {
            try {
                getManager().sendStatus(new Gson().toJson((Object) witsStatus));
            } catch (RemoteException e) {
            }
        }
    }

    public static List<String> getDataListFromJsonKey(String key) {
        return (List) new Gson().fromJson(Settings.System.getString(context.getContentResolver(), key), new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public static void setBooleanStatus(String key, boolean value) throws RemoteException {
        getManager().addBooleanStatus(key, value);
    }

    public static void setStatusString(String key, String value) throws RemoteException {
        getManager().addStringStatus(key, value);
    }

    public static void setStatusInt(String key, int value) throws RemoteException {
        getManager().addIntStatus(key, value);
    }

    public static boolean getStatusBoolean(String key) throws RemoteException {
        return getManager().getStatusBoolean(key);
    }

    public static String getStatusString(String key) throws RemoteException {
        return getManager().getStatusString(key);
    }

    public static int getStatusInt(String key) throws RemoteException {
        return getManager().getStatusInt(key);
    }

    public static int getSettingsInt(String key) throws RemoteException {
        return getManager().getSettingsInt(key);
    }

    public static String getSettingsString(String key) throws RemoteException {
        return getManager().getSettingsString(key);
    }

    public static void setSettingsInt(String key, int value) throws RemoteException {
        getManager().setSettingsInt(key, value);
    }

    public static void setSettingsString(String key, String value) throws RemoteException {
        getManager().setSettingsString(key, value);
    }

    public static void main(String... arg) {
        int i;
        double bl2max;
        double result;
        double bl1max = 0.21d;
        double result2 = 0.3d;
        double shouyi1 = 14.77d;
        double zj = 14.0d;
        double max = 2000.0d;
        int i2 = 0;
        double qd = 0.0d;
        double gzj = 0.0d;
        double gqd = 0.0d;
        while (true) {
            double result3 = qd;
            double gzj2 = gzj;
            int i3 = i2;
            if (((double) i3) <= max) {
                double qd2 = (double) i3;
                double shouyi2 = zj;
                double zj2 = max - ((double) i3);
                double realdamge = (((qd2 / 17.0d) / 100.0d) + 1.0d) * 15000.0d;
                double shouyi12 = shouyi1;
                double yingzi = realdamge * bl1max * (((zj2 / shouyi1) / 100.0d) + 1.0d);
                double max2 = max;
                double max3 = realdamge * result2 * (((zj2 / shouyi2) / 100.0d) + 1.0d);
                double bl1max2 = bl1max;
                double step2 = max3 + yingzi + (((1.0d - bl1max) - result2) * realdamge);
                if (zj2 % 100.0d == 0.0d || zj2 == 0.0d || qd2 == 0.0d) {
                    PrintStream printStream = System.out;
                    bl2max = result2;
                    StringBuilder sb = new StringBuilder();
                    sb.append("影子 卷云:");
                    i = i3;
                    sb.append((realdamge / 15000.0d) * (((zj2 / 15.18d) / 100.0d) + 1.0d));
                    sb.append("-");
                    sb.append((realdamge / 15000.0d) * (((zj2 / 8.728d) / 100.0d) + 1.0d));
                    printStream.print(sb.toString());
                    System.out.print("-----专精:" + zj2 + " -强度:" + qd2);
                    System.out.println("-----卷云:" + ((int) max3) + " -影子:" + ((int) yingzi) + "-一共:" + ((int) step2));
                } else {
                    bl2max = result2;
                    i = i3;
                }
                double temp = step2;
                if (result3 < temp) {
                    result = temp;
                    gqd = qd2;
                    gzj = zj2;
                } else {
                    gzj = gzj2;
                    result = result3;
                }
                i2 = i + 1;
                double d = qd2;
                qd = result;
                double d2 = zj2;
                zj = shouyi2;
                shouyi1 = shouyi12;
                max = max2;
                bl1max = bl1max2;
                result2 = bl2max;
            } else {
                double d3 = result2;
                double d4 = shouyi1;
                double d5 = zj;
                double d6 = max;
                System.out.println("-----" + result3 + "    专精:" + gzj2 + "强度:" + gqd);
                return;
            }
        }
    }
}
