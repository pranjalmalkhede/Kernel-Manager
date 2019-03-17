
package com.android.kernelmanager.utils;

import com.crashlytics.android.Crashlytics;
import com.android.kernelmanager.BuildConfig;


public class Log {

    private static final String TAG = "KernelAdiutor";

    public static void i(String tag, String message) {
        android.util.Log.i(TAG, getMessage(tag, message));
    }

    public static void e(String tag, String message) {
        android.util.Log.e(TAG, getMessage(tag, message));
    }

    public static void crashlyticsI(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(TAG, getMessage(tag, message));
        } else {
            Crashlytics.log(android.util.Log.INFO, TAG, getMessage(tag, message));
        }
    }

    public static void crashlyticsE(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, getMessage(tag, message));
        } else {
            Crashlytics.log(android.util.Log.ERROR, TAG, getMessage(tag, message));
        }
    }

    private static String getMessage(String tag, String message) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        String className = element.getClassName();

        return Utils.strFormat("[%s][%s] %s - %s",
                className.substring(className.lastIndexOf(".") + 1),
                element.getMethodName(),
                tag,
                message);
    }

}
