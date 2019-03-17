
package com.android.kernelmanager.services;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.android.kernelmanager.R;
import com.android.kernelmanager.activities.MainActivity;
import com.android.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.android.kernelmanager.utils.kernel.gpu.GPUFreq;
import com.android.kernelmanager.utils.root.RootUtils;


public class DashClock extends DashClockExtension {

    private boolean mRunning = false;
    private ExtensionData extensionData;

    @Override
    protected void onUpdateData(int reason) {
        final String status = getString(R.string.app_name);
        final int cores = CPUFreq.getInstance(this).getCpuCount();

        if (extensionData == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            extensionData = new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_launcher_preview)
                    .clickIntent(intent);
        }
        if (!mRunning) {
            new Thread(() -> {
                while (true) {
                    try {
                        StringBuilder message = new StringBuilder();
                        if (RootUtils.rootAccess()) {
                            CPUFreq cpuFreq = CPUFreq.getInstance(DashClock.this);
                            GPUFreq gpuFreq = GPUFreq.getInstance();

                            StringBuilder cpu = new StringBuilder();
                            for (int i = 0; i < cores; i++) {
                                int freq = cpuFreq.getCurFreq(i) / 1000;
                                if (i != 0) cpu.append(" | ");
                                cpu.append(freq == 0 ? getString(R.string.offline) : freq);
                            }
                            if (cpu.length() > 0) {
                                message.append(getString(R.string.cpu)).append(": ")
                                        .append(cpu.toString()).append("\n");
                            }
                            message.append(getString(R.string.cpu_governor)).append(": ")
                                    .append(cpuFreq.getGovernor(false)).append("\n");

                            if (gpuFreq.hasCurFreq()) {
                                message.append(getString(R.string.gpu)).append(": ")
                                        .append(gpuFreq.getCurFreq() / 1000000)
                                        .append(getString(R.string.mhz));
                            }
                        } else {
                            message.append(getString(R.string.no_root));
                        }

                        publishUpdate(extensionData.status(status).expandedBody(message.toString()));
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }).start();
        }
        mRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RootUtils.closeSU();
    }

}
