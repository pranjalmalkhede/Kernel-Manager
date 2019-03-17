
package com.android.kernelmanager.utils.kernel.cpu;

import android.content.Context;

import com.android.kernelmanager.fragments.ApplyOnBootFragment;
import com.android.kernelmanager.utils.Utils;
import com.android.kernelmanager.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Misc {

    private static final String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";
    private static final String CPU_WQ_POWER_SAVING = "/sys/module/workqueue/parameters/power_efficient";
    private static final String CPU_AVAILABLE_CFS_SCHEDULERS = "/sys/devices/system/cpu/sched_balance_policy/available_sched_balance_policy";
    private static final String CPU_CURRENT_CFS_SCHEDULER = "/sys/devices/system/cpu/sched_balance_policy/current_sched_balance_policy";

    private static final String CPU_QUIET = "/sys/devices/system/cpu/cpuquiet";
    private static final String CPU_QUIET_ENABLE = CPU_QUIET + "/cpuquiet_driver/enabled";
    private static final String CPU_QUIET_TEGRA_ENABLE = CPU_QUIET + "/tegra_cpuquiet/enable";
    private static final String CPU_QUIET_AVAILABLE_GOVERNORS = CPU_QUIET + "/available_governors";
    private static final String CPU_QUIET_CURRENT_GOVERNOR = CPU_QUIET + "/current_governor";

    private static final String CPU_TOUCH_BOOST = "/sys/module/msm_performance/parameters/touchboost";

    private static String[] sAvailableCFSSchedulers;
    private static String[] sCpuQuietAvailableGovernors;

    public static void enableCpuTouchBoost(boolean enabled, Context context) {
        run(Control.write(enabled ? "1" : "0", CPU_TOUCH_BOOST), CPU_TOUCH_BOOST, context);
    }

    public static boolean isCpuTouchBoostEnabled() {
        return Utils.readFile(CPU_TOUCH_BOOST).equals("1");
    }

    public static boolean hasCpuTouchBoost() {
        return Utils.existFile(CPU_TOUCH_BOOST);
    }

    public static void setCpuQuietGovernor(String value, Context context) {
        run(Control.write(value, CPU_QUIET_CURRENT_GOVERNOR), CPU_QUIET_CURRENT_GOVERNOR, context);
    }

    public static String getCpuQuietCurGovernor() {
        return Utils.readFile(CPU_QUIET_CURRENT_GOVERNOR);
    }

    public static List<String> getCpuQuietAvailableGovernors() {
        if (sCpuQuietAvailableGovernors == null) {
            sCpuQuietAvailableGovernors = Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sCpuQuietAvailableGovernors));
    }

    public static boolean hasCpuQuietGovernors() {
        return Utils.existFile(CPU_QUIET_AVAILABLE_GOVERNORS) && Utils.existFile(CPU_QUIET_CURRENT_GOVERNOR)
                && !Utils.readFile(CPU_QUIET_AVAILABLE_GOVERNORS).equals("none");
    }

    public static void enableCpuQuiet(boolean enabled, Context context) {
        if (Utils.existFile(CPU_QUIET_ENABLE)) {
            run(Control.write(enabled ? "1" : "0", CPU_QUIET_ENABLE), CPU_QUIET_ENABLE, context);
        } else {
            run(Control.write(enabled ? "1" : "0", CPU_QUIET_TEGRA_ENABLE), CPU_QUIET_TEGRA_ENABLE, context);
        }
    }

    public static boolean isCpuQuietEnabled() {
        return Utils.readFile(Utils.existFile(CPU_QUIET_ENABLE) ? CPU_QUIET_ENABLE
                : CPU_QUIET_TEGRA_ENABLE).equals("1");
    }

    public static boolean hasCpuQuietEnable() {
        return Utils.existFile(CPU_QUIET_ENABLE) || Utils.existFile(CPU_QUIET_TEGRA_ENABLE);
    }

    public static boolean hasCpuQuiet() {
        return Utils.existFile(CPU_QUIET);
    }

    public static void setCFSScheduler(String value, Context context) {
        run(Control.write(value, CPU_CURRENT_CFS_SCHEDULER), CPU_CURRENT_CFS_SCHEDULER, context);
    }

    public static String getCurrentCFSScheduler() {
        return Utils.readFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static List<String> getAvailableCFSSchedulers() {
        if (sAvailableCFSSchedulers == null) {
            sAvailableCFSSchedulers = Utils.readFile(CPU_AVAILABLE_CFS_SCHEDULERS).split(" ");
        }
        return new ArrayList<>(Arrays.asList(sAvailableCFSSchedulers));
    }

    public static boolean hasCFSScheduler() {
        return Utils.existFile(CPU_AVAILABLE_CFS_SCHEDULERS) && Utils.existFile(CPU_CURRENT_CFS_SCHEDULER);
    }

    public static void enablePowerSavingWq(boolean enabled, Context context) {
        run(Control.chmod("644", CPU_WQ_POWER_SAVING), CPU_WQ_POWER_SAVING + "chmod", context);
        run(Control.write(enabled ? "Y" : "N", CPU_WQ_POWER_SAVING), CPU_WQ_POWER_SAVING, context);
    }

    public static boolean isPowerSavingWqEnabled() {
        return Utils.readFile(CPU_WQ_POWER_SAVING).equals("Y");
    }

    public static boolean hasPowerSavingWq() {
        return Utils.existFile(CPU_WQ_POWER_SAVING);
    }

    public static void setMcPowerSaving(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_MC_POWER_SAVING), CPU_MC_POWER_SAVING, context);
    }

    public static int getCurMcPowerSaving() {
        return Utils.strToInt(Utils.readFile(CPU_MC_POWER_SAVING));
    }

    public static boolean hasMcPowerSaving() {
        return Utils.existFile(CPU_MC_POWER_SAVING);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
