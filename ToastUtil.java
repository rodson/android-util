package com.cvte.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * @description: Singleton toast, update immediately.
 * @author: Saul
 * @date: 14-10-16
 * @version: 1.0
 */
public class ToastUtil {
    private static final boolean ENABLE = true;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static Toast mToast = null;
    private static Object mSynchronizedObject = new Object();

    public static void showLong(Context context, String msg) {
        show(context, msg, Toast.LENGTH_LONG);
    }

    public static void showShort(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    private static void show(Context context, final String msg, final int duration) {
        if (ENABLE) {
            Toast.makeText(context, msg, duration).show();
//            if (mToast == null) {
//                mToast = Toast.makeText(context, msg, duration);
//            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            synchronized (mSynchronizedObject) {
//                                mToast.cancel();
//                                mToast.setText(msg);
//                                mToast.setDuration(duration);
//                                mToast.show();
//                            }
//                        }
//                    });
//                }
//            }).start();
        }
    }
}
