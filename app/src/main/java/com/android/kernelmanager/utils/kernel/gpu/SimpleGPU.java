
package com.android.kernelmanager.utils.kernel.gpu;

import android.content.Context;

import com.android.kernelmanager.fragments.ApplyOnBootFragment;
import com.android.kernelmanager.utils.Utils;
import com.android.kernelmanager.utils.root.Control;


public class SimpleGPU {

    private static final String SIMPLE_GPU_PARAMETERS = "/sys/module/simple_gpu_algorithm/parameters";
    private static final String SIMPLE_GPU_ACTIVATE = SIMPLE_GPU_PARAMETERS + "/simple_gpu_activate";
    private static final String SIMPLE_GPU_LAZINESS = SIMPLE_GPU_PARAMETERS + "/simple_laziness";
    private static final String SIMPLE_RAMP_THRESHOLD = SIMPLE_GPU_PARAMETERS + "/simple_ramp_threshold";

    public static void setSimpleGpuRampThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value * 1000), SIMPLE_RAMP_THRESHOLD), SIMPLE_RAMP_THRESHOLD, context);
    }

    public static int getSimpleGpuRampThreshold() {
        return Utils.strToInt(Utils.readFile(SIMPLE_RAMP_THRESHOLD)) / 1000;
    }

    public static boolean hasSimpleGpuRampThreshold() {
        return Utils.existFile(SIMPLE_RAMP_THRESHOLD);
    }

    public static void setSimpleGpuLaziness(int value, Context context) {
        run(Control.write(String.valueOf(value), SIMPLE_GPU_LAZINESS), SIMPLE_GPU_LAZINESS, context);
    }

    public static int getSimpleGpuLaziness() {
        return Utils.strToInt(Utils.readFile(SIMPLE_GPU_LAZINESS));
    }

    public static boolean hasSimpleGpuLaziness() {
        return Utils.existFile(SIMPLE_GPU_LAZINESS);
    }

    public static void enableSimpleGpu(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", SIMPLE_GPU_ACTIVATE), SIMPLE_GPU_ACTIVATE, context);
    }

    public static boolean isSimpleGpuEnabled() {
        return Utils.readFile(SIMPLE_GPU_ACTIVATE).equals("1");
    }

    public static boolean hasSimpleGpuEnable() {
        return Utils.existFile(SIMPLE_GPU_ACTIVATE);
    }

    public static boolean supported() {
        return Utils.existFile(SIMPLE_GPU_PARAMETERS);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
