package com.wits.pms.utils;

import android.content.Context;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.snaggly.ksw_toolkit.R;

import java.lang.reflect.Method;

public class TouchControl {
    private static final int MSG_ADD = 0;
    private static final int MSG_HIDE_POINT = 4;
    private static final int MSG_REMOVE = 2;
    private static final int MSG_SHOW_POINT = 3;
    private static final int MSG_UPDATE = 1;
    private static final String TAG = "TouchControl";
    private View mInterceptView;
    private static final Class<InputManager> cl = InputManager.class;
    private static boolean wasAdded;
    private final Context mContext;
    private long mDownTime;
    private WindowManager.LayoutParams mPointerLayoutParams;
    private ImageView mPointerView;
    private final int mScreenHeight;
    private final int mScreenWidth;
    private final WindowManager mWindowManager;
    private final long AUTO_HIDE_TIME = 10000;
    private final InputManager mInputManager;
    private final Handler mHandler;

    public interface OnScreenStatusListener {
        void openScreen();
    }

    private static InputManager getInputManager() {
        try {
            Method method = cl.getMethod("getInstance");
            return (InputManager)method.invoke(cl);
        } catch (Exception e) {
            Log.e(TAG, "Error getting InputManager");
            return null;
        }
    }

    private void injectInputEvent(InputManager mInputManager, MotionEvent e, int i) {
        try {
            Method method = cl.getMethod("injectInputEvent", InputEvent.class, int.class);
            method.invoke(mInputManager, e, i);
        } catch (Exception ex) {
            Log.e(TAG, "Unable to injectInput");
        }
    }

    public TouchControl(Context context) {
        this.mInputManager = getInputManager();
        this.mContext = context;

        this.mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        try {
                            mWindowManager.addView(TouchControl.this.mPointerView, TouchControl.this.mPointerLayoutParams);
                        }
                        catch (Exception e) {
                            Log.e(TAG, "Unable to add view");
                            e.printStackTrace();
                        }
                        return;
                    case 1:
                        mWindowManager.updateViewLayout(TouchControl.this.mPointerView, TouchControl.this.mPointerLayoutParams);
                        return;
                    case 2:
                    default:
                        return;
                    case 3:
                        mPointerView.setVisibility(View.VISIBLE);
                        return;
                    case 4:
                        mPointerView.setVisibility(View.GONE);
                }
            }
        };

        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        this.mWindowManager.getDefaultDisplay().getSize(outSize);
        this.mScreenWidth = outSize.x;
        this.mScreenHeight = outSize.y;
        Log.i(TAG, "init width:" + outSize.x + " - height:" + outSize.y);
    }

    public synchronized void opPointerEvent(int x, int y, int action) {
        if (this.mPointerView == null) {
            obtainPointerView(this.mContext);
        }
        WindowManager.LayoutParams layoutParams = this.mPointerLayoutParams;
        int x2 = (this.mScreenWidth * x) / 255;
        layoutParams.x = x2;
        WindowManager.LayoutParams layoutParams2 = this.mPointerLayoutParams;
        int y2 = (this.mScreenHeight * y) / 255;
        layoutParams2.y = y2;
        Log.i(TAG, "mPointerLayoutParams x:" + this.mPointerLayoutParams.x + "-y:" + this.mPointerLayoutParams.y);
        this.mHandler.sendEmptyMessage(1);
        if (this.mHandler.hasMessages(4)) {
            this.mHandler.removeMessages(4);
        }
        this.mHandler.sendEmptyMessage(3);
        switch (action) {
            case 0:
                touchDown(x2, y2);
                break;
            case 1:
                touchUp(x2, y2);
                this.mHandler.sendEmptyMessageDelayed(4, 10000);
                break;
            case 2:
                touchMove(x2, y2);
                if (!isDown()) {
                    this.mHandler.sendEmptyMessageDelayed(4, 10000);
                    break;
                }
                break;
        }
    }

    private void obtainPointerView(Context context) {
        this.mPointerLayoutParams = new WindowManager.LayoutParams();
        this.mPointerLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        this.mPointerLayoutParams.height = 38;
        this.mPointerLayoutParams.width = 38;
        this.mPointerLayoutParams.flags |= 776;
        this.mPointerLayoutParams.format = 1;
        this.mPointerLayoutParams.gravity = 51;
        this.mPointerLayoutParams.x = 0;
        this.mPointerLayoutParams.y = 0;
        this.mPointerView = new ImageView(context);
        this.mPointerView.setImageResource(R.mipmap.pointer);
        this.mPointerView.setLayoutParams(new FrameLayout.LayoutParams(38, 38));
        this.mPointerView.setVisibility(View.VISIBLE);
        this.mHandler.sendEmptyMessage(0);
    }

    public boolean isDown() {
        return this.mDownTime != 0;
    }

    private void touchDown(int x, int y) {
        this.mDownTime = SystemClock.uptimeMillis();
        MotionEvent event = getMotionEvent(this.mDownTime, this.mDownTime, 0, x, y);
        injectInputEvent(mInputManager, event, 2);
    }

    private void touchUp(int x, int y) {
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = getMotionEvent(this.mDownTime, eventTime, 1, x, y);
        this.mDownTime = 0L;
        injectInputEvent(mInputManager, event, 2);
    }

    private void touchMove(int x, int y) {
        if (this.mDownTime == 0) {
            return;
        }
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = getMotionEvent(this.mDownTime, eventTime, 2, x, y);
        injectInputEvent(mInputManager, event, 0);
    }

    private static MotionEvent getMotionEvent(long downTime, long eventTime, int action, float x, float y) {
        MotionEvent.PointerProperties properties = new MotionEvent.PointerProperties();
        properties.id = 0;
        properties.toolType = 3;
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        coords.pressure = 1.0f;
        coords.size = 1.0f;
        coords.x = x;
        coords.y = y;
        return MotionEvent.obtain(downTime, eventTime, action, 1, new MotionEvent.PointerProperties[]{properties}, new MotionEvent.PointerCoords[]{coords}, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
    }

    public static int getXFromData(byte[] data) {
        return ((data[0] & 255) << 8) + (data[1] & 255);
    }

    public static int getYFromData(byte[] data) {
        return ((data[2] & 255) << 8) + (data[3] & 255);
    }
}
