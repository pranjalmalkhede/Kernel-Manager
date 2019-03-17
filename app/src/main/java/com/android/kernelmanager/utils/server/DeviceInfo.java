
package com.android.kernelmanager.utils.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DeviceInfo {

    private String mAndroidVersion;
    private String mKernelVersion;
    private String mAppVersion;
    private String mBoard;
    private String mModel;
    private String mVendor;
    private String mCpuInfo;
    private String mFingerprint;
    private List<String> mCommands;
    private long mAverageSOT;
    private long mCpu;
    private long mScore;

    private boolean mValid = true;

    public DeviceInfo(JSONObject json) {
        try {
            mAndroidVersion = json.getString("android_version");
            mKernelVersion = json.getString("kernel_version");
            mAppVersion = json.getString("app_version");
            mBoard = json.getString("board");
            mModel = json.getString("model");
            mVendor = json.getString("vendor");
            mCpuInfo = json.getString("cpuinfo");
            mFingerprint = json.getString("fingerprint");

            JSONArray commands = json.getJSONArray("commands");
            mCommands = new ArrayList<>();

            for (int i = 0; i < commands.length(); i++) {
                mCommands.add(commands.getString(i));
            }

            JSONArray times = json.getJSONArray("times");
            for (int i = 0; i < times.length(); i++) {
                mAverageSOT += times.getInt(i);
            }
            mAverageSOT /= times.length();
            mAverageSOT *= 100;

            mCpu = json.getLong("cpu");
            mScore = Math.round(json.getDouble("score"));
        } catch (JSONException ignored) {
            mValid = false;
        }
    }

    public String getAndroidVersion() {
        return mAndroidVersion;
    }

    public String getKernelVersion() {
        return mKernelVersion;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public String getBoard() {
        return mBoard;
    }

    public String getModel() {
        return mModel;
    }

    public String getVendor() {
        return mVendor;
    }

    public String getCpuInfo() {
        return mCpuInfo;
    }

    public String getFingerprint() {
        return mFingerprint;
    }

    public List<String> getCommands() {
        return mCommands;
    }

    public long getAverageSOT() {
        return mAverageSOT;
    }

    public long getCpu() {
        return mCpu;
    }

    public long getScore() {
        return mScore;
    }

    public boolean valid() {
        return mValid;
    }

}
