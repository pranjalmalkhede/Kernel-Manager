
package com.android.kernelmanager.utils.kernel.gpu;

import android.content.Context;

import com.android.kernelmanager.fragments.ApplyOnBootFragment;
import com.android.kernelmanager.utils.Utils;
import com.android.kernelmanager.utils.root.Control;


public class AdrenoIdler {

    private static final String ADRENO_IDLER_PARAMETERS = "/sys/module/adreno_idler/parameters";
    private static final String ADRENO_IDLER_ACTIVATE = ADRENO_IDLER_PARAMETERS + "/adreno_idler_active";
    private static final String ADRENO_IDLER_DOWNDIFFERENTIAL = ADRENO_IDLER_PARAMETERS + "/adreno_idler_downdifferential";
    private static final String ADRENO_IDLER_IDLEWAIT = ADRENO_IDLER_PARAMETERS + "/adreno_idler_idlewait";
    private static final String ADRENO_IDLER_IDLEWORKLOAD = ADRENO_IDLER_PARAMETERS + "/adreno_idler_idleworkload";

    public static void setAdrenoIdlerIdleWorkload(int value, Context context) {
        run(Control.write(String.valueOf(value * 1000), ADRENO_IDLER_IDLEWORKLOAD),
                ADRENO_IDLER_IDLEWORKLOAD, context);
    }

    public static int getAdrenoIdlerIdleWorkload() {
        return Utils.strToInt(Utils.readFile(ADRENO_IDLER_IDLEWORKLOAD)) / 1000;
    }

    public static boolean hasAdrenoIdlerIdleWorkload() {
        return Utils.existFile(ADRENO_IDLER_IDLEWORKLOAD);
    }

    public static void setAdrenoIdlerIdleWait(int value, Context context) {
        run(Control.write(String.valueOf(value), ADRENO_IDLER_IDLEWAIT), ADRENO_IDLER_IDLEWAIT, context);
    }

    public static int getAdrenoIdlerIdleWait() {
        return Utils.strToInt(Utils.readFile(ADRENO_IDLER_IDLEWAIT));
    }

    public static boolean hasAdrenoIdlerIdleWait() {
        return Utils.existFile(ADRENO_IDLER_IDLEWAIT);
    }

    public static void setAdrenoIdlerDownDiff(int value, Context context) {
        run(Control.write(String.valueOf(value), ADRENO_IDLER_DOWNDIFFERENTIAL),
                ADRENO_IDLER_DOWNDIFFERENTIAL, context);
    }

    public static int getAdrenoIdlerDownDiff() {
        return Utils.strToInt(Utils.readFile(ADRENO_IDLER_DOWNDIFFERENTIAL));
    }

    public static boolean hasAdrenoIdlerDownDiff() {
        return Utils.existFile(ADRENO_IDLER_DOWNDIFFERENTIAL);
    }

    public static void enableAdrenoIdler(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", ADRENO_IDLER_ACTIVATE), ADRENO_IDLER_ACTIVATE, context);
    }

    public static boolean isAdrenoIdlerEnabled() {
        return Utils.readFile(ADRENO_IDLER_ACTIVATE).equals("Y");
    }

    public static boolean hasAdrenoIdlerEnable() {
        return Utils.existFile(ADRENO_IDLER_ACTIVATE);
    }

    public static boolean supported() {
        return Utils.existFile(ADRENO_IDLER_PARAMETERS);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
