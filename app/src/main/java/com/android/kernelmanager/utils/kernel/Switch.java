
package com.android.kernelmanager.utils.kernel;



public class Switch {

    private String mEnable;
    private String mDisable;

    public Switch(String enable, String disable) {
        mEnable = enable;
        mDisable = disable;
    }

    public String getEnable() {
        return mEnable;
    }

    public String getDisable() {
        return mDisable;
    }

}
