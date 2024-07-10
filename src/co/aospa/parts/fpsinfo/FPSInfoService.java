// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.fpsinfo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import co.aospa.parts.utils.FileUtils;

public class FPSInfoService extends Service {
    private View mView;
    private HandlerThread mHandlerThread;
    private Handler mBackgroundHandler;
    private final String TAG = "FPSInfoService";
    private String mFps = null;

    private static final String MEASURED_FPS = "/sys/class/drm/card0/sde-crtc-0/measured_fps";

    private IDreamManager mDreamManager;

    private class FPSView extends View {
        private Paint mOnlinePaint;
        private float mAscent;
        private int mFH;
        private int mMaxWidth = 0;

        private int mNeededWidth;
        private int mNeededHeight;

        private boolean mDataAvail;

        private Handler mCurFPSHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj == null || msg.what != 1) {
                    return;
                }
                String msgData = (String) msg.obj;
                msgData = msgData.trim().split("\\s+")[1];
                mFps = msgData + " FPS";
                mDataAvail = true;
                updateDisplay();
            }
        };

        FPSView(Context c) {
            super(c);
            float density = c.getResources().getDisplayMetrics().density;
            int paddingPx = Math.round(9 * density);
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            setBackgroundColor(Color.argb(0x0, 0, 0, 0));

            final int textSize = Math.round(16 * density);

            mOnlinePaint = new Paint();
            mOnlinePaint.setAntiAlias(true);
            mOnlinePaint.setTextSize(textSize);
            mOnlinePaint.setColor(Color.YELLOW);
            mOnlinePaint.setShadowLayer(5.0f, 0.0f, 0.0f, Color.BLACK);

            mAscent = mOnlinePaint.ascent();
            float descent = mOnlinePaint.descent();
            mFH = (int) (descent - mAscent + .5f);

            updateDisplay();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            mCurFPSHandler.removeMessages(1);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(resolveSize(mNeededWidth, widthMeasureSpec),
                    resolveSize(mNeededHeight, heightMeasureSpec));
        }

        private String getFPSInfoString() {
            return mFps;
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!mDataAvail) {
                return;
            }

            final int W = mNeededWidth;
            final int RIGHT = getWidth() - 1;

            int x = RIGHT - mPaddingLeft;
            int top = mPaddingTop + 2;
            int bottom = mPaddingTop + mFH - 2;

            int y = mPaddingTop - (int) mAscent;

            String s = getFPSInfoString();
            canvas.drawText(s, RIGHT - mPaddingLeft - mMaxWidth,
                    y - 1, mOnlinePaint);
            y += mFH;
        }

        void updateDisplay() {
            if (!mDataAvail) {
                return;
            }

            if (mOnlinePaint != null) {
                mMaxWidth = (int) mOnlinePaint.measureText(mFps);
            }

            int neededWidth = mPaddingLeft + mPaddingRight + mMaxWidth;
            int neededHeight = mPaddingTop + mPaddingBottom + 40;
            if (neededWidth != mNeededWidth || neededHeight != mNeededHeight) {
                mNeededWidth = neededWidth;
                mNeededHeight = neededHeight;
                requestLayout();
            } else {
                invalidate();
            }
        }

        public Handler getHandler() {
            return mCurFPSHandler;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mView = new FPSView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.y = 50;
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.setTitle("FPS Info");

        mHandlerThread = new HandlerThread("FPSInfoServiceHandlerThread");
        mHandlerThread.start();
        mBackgroundHandler = new Handler(mHandlerThread.getLooper());

        startTask();

        mDreamManager = IDreamManager.Stub.asInterface(
                ServiceManager.checkService(DreamService.DREAM_SERVICE));
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTask();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
        mView = null;
        unregisterReceiver(mScreenStateReceiver);
        mHandlerThread.quitSafely();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(TAG, "ACTION_SCREEN_ON " + isDozeMode());
                if (!isDozeMode()) {
                    mView.setVisibility(View.VISIBLE);
                    startTask();
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "ACTION_SCREEN_OFF");
                mView.setVisibility(View.GONE);
                stopTask();
            }
        }
    };

    private boolean isDozeMode() {
        try {
            if (mDreamManager != null && mDreamManager.isDreaming()) {
                return true;
            }
        } catch (RemoteException e) {
            return false;
        }
        return false;
    }

    private void startTask() {
        mBackgroundHandler.post(mCurFPSTask);
    }

    private void stopTask() {
        mBackgroundHandler.removeCallbacks(mCurFPSTask);
    }

    private Runnable mCurFPSTask = new Runnable() {
        @Override
        public void run() {
            try {
                String fpsVal = FileUtils.readOneLine(MEASURED_FPS);
                mView.getHandler().sendMessage(mView.getHandler().obtainMessage(1, fpsVal));
                mBackgroundHandler.postDelayed(this, 1000);
            } catch (Exception e) {
                Log.e(TAG, "Error reading FPS value", e);
            }
        }
    };
}
