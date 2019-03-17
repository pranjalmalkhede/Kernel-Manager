
package com.android.kernelmanager.utils.root;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.kernelmanager.services.monitor.Monitor;
import com.android.kernelmanager.utils.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Control {

    private static final String TAG = Control.class.getSimpleName();

    private static Control sControl;

    private boolean mProfileMode;
    private LinkedHashMap<String, String> mProfileCommands = new LinkedHashMap<>();

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public static String setProp(String prop, String value) {
        return "setprop " + prop + " " + value;
    }

    public static String startService(String prop) {
        return setProp("ctl.start", prop) + ";start " + prop;
    }

    public static String stopService(String prop) {
        return setProp("ctl.stop", prop) + ";stop " + prop;
    }

    public static String write(String text, String path) {
        return "echo '" + text + "' > " + path;
    }

    public static String chmod(String permission, String file) {
        return "chmod " + permission + " " + file;
    }

    public static String chown(String group, String file) {
        return "chown " + group + " " + file;
    }

    private void apply(String command, String category, String id, final Context context) {
        if (context != null) {
            if (mProfileMode) {
                Log.i(TAG, "Added to profile: " + id);
                mProfileCommands.put(id, command);
            }
        }

        if (command.startsWith("#")) return;
        RootUtils.runCommand(command);
        Log.i(TAG, command);
        if (context != null) {
            context.bindService(new Intent(context, Monitor.class), new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName componentName,
                                                       IBinder iBinder) {
                            Monitor.MonitorBinder monitorBinder =
                                    (Monitor.MonitorBinder) iBinder;
                            monitorBinder.onSettingsChange();
                            context.unbindService(this);
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName componentName) {
                        }
                    },
                    Context.BIND_AUTO_CREATE);
        }
    }

    private synchronized void run(final String command, final String category, final String id,
                                  final Context context) {
        mExecutor.execute(() -> apply(command, category, id, context));
    }

    private static Control getInstance() {
        if (sControl == null) {
            sControl = new Control();
        }
        return sControl;
    }

    public static void setProfileMode(boolean mode) {
        getInstance().mProfileMode = mode;
    }

    public static LinkedHashMap<String, String> getProfileCommands() {
        return getInstance().mProfileCommands;
    }

    public static void clearProfileCommands() {
        getInstance().mProfileCommands.clear();
    }

    public static void runSetting(String command, String category, String id, Context context) {
        getInstance().run(command, category, id, context);
    }

}
