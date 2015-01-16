package com.cvte.util;

import android.util.Log;

/**
 * @description: Api for log.
 * @author: Saul
 * @date: 14-9-25
 * @version: 1.0
 */
public class LogUtil {
    private static String DEFAULT_TAG = "MengYouSDK";
    private static boolean mLogAll = true;
    private static final boolean LOGD_DEBUG = true;
    private static final boolean LOGI_DEBUG = true;
    private static final boolean LOGE_DEBUG = true;
    private static final int STACK_INDEX = 2;

    /**
     * Used for persistent log
     * @param msg
     */
    public static void d(String msg) {
        if (LOGD_DEBUG && mLogAll) {
            Log.d(DEFAULT_TAG, getInformation(msg));
        }
    }

    /**
     * Used for temp log
     * @param msg
     */
    public static void i(String msg) {
        if (LOGI_DEBUG && mLogAll) {
            Log.i(DEFAULT_TAG, getInformation(msg));
        }
    }

    /**
     * Used for exception log
     * @param msg
     */
    public static void e(String msg) {
        if (LOGE_DEBUG && mLogAll) {
            Log.e(DEFAULT_TAG, getInformation(msg));
        }
    }

    private static String getInformation(String msg) {
        Exception exception = new Exception();
        return exception.getStackTrace()[STACK_INDEX].getFileName() + "|"
                + exception.getStackTrace()[STACK_INDEX].getLineNumber() + "|" + msg;
    }

    public static void setLog(boolean enable) {
        mLogAll = enable;
    }
}
