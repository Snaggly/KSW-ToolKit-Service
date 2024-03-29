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
import com.wits.pms.ICmdListener;
import com.wits.pms.IContentObserver;
import com.wits.pms.IPowerManagerAppService;
import java.util.HashMap;
import java.util.Objects;

public class PowerManagerApp {
    private static ICmdListener cmdListener;
    private static final HashMap<String, IContentObserver> maps = new HashMap<>();
    private static final String TAG = "WitsStatus";
    private static final String ERROR_MSG = "No PowerManagerApp found";

    public static void init(Context context) {
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor("bootTimes"), true, new ContentObserver(new Handler(context.getMainLooper())) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (PowerManagerApp.cmdListener != null) {
                    PowerManagerApp.registerICmdListener(PowerManagerApp.cmdListener);
                }
                for (String key : PowerManagerApp.maps.keySet()) {
                    PowerManagerApp.registerIContentObserver(key, Objects.requireNonNull(PowerManagerApp.maps.get(key)));
                }
            }
        });
    }

    public static IPowerManagerAppService getManager() {
        return IPowerManagerAppService.Stub.asInterface(getService("wits_pms"));
    }

    public static void registerICmdListener(ICmdListener listener) {
        try {
            cmdListener = listener;
            if (getManager() != null) {
                getManager().registerCmdListener(listener);
            }
        } catch (RemoteException ignored) {
        }
    }

    public static void registerIContentObserver(String key, IContentObserver contentObserver) {
        Log.i("IPowerManagerService", contentObserver.getClass().getName());
        try {
            maps.put(key, contentObserver);
            if (getManager() != null) {
                getManager().registerObserver(key, contentObserver);
            }
        } catch (RemoteException ignored) {
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
        } catch (RemoteException ignored) {
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
                getManager().sendStatus(new Gson().toJson(witsStatus));
            } catch (RemoteException ignored) {
            }
        }
    }

    public static void setBooleanStatus(String key, boolean value) {
        try {
            getManager().addBooleanStatus(key, value);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
        }
    }

    public static void setStatusString(String key, String value) {
        try {
            getManager().addStringStatus(key, value);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
        }
    }

    public static void setStatusInt(String key, int value) {
        try {
            getManager().addIntStatus(key, value);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
        }
    }

    public static boolean getStatusBoolean(String key) {
        try {
            return getManager().getStatusBoolean(key);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
            return false;
        }
    }

    public static String getStatusString(String key) {
        try {
            return getManager().getStatusString(key);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
            return null;
        }
    }

    public static int getStatusInt(String key) {
        try {
            return getManager().getStatusInt(key);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
            return -1;
        }
    }

    public static int getSettingsInt(String key)  {
        try {
            return getManager().getSettingsInt(key);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
            return -1;
        }
    }

    public static String getSettingsString(String key) {
        try {
            return getManager().getSettingsString(key);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
            return null;
        }
    }

    public static void setSettingsInt(String key, int value) {
        try {
            getManager().setSettingsInt(key, value);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
        }
    }

    public static void setSettingsString(String key, String value) {
        try {
            getManager().setSettingsString(key, value);
        }
        catch (Exception e) {
            Log.e(TAG, ERROR_MSG);
        }
    }
}
