
package com.android.kernelmanager.utils.kernel.gpu;


public class GPU {

    public static boolean supported() {
        return GPUFreq.getInstance().supported() || SimpleGPU.supported() || AdrenoIdler.supported();
    }

}
