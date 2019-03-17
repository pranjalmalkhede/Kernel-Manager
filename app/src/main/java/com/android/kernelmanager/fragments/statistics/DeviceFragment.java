
package com.android.kernelmanager.fragments.statistics;

import com.android.kernelmanager.R;
import com.android.kernelmanager.fragments.DescriptionFragment;
import com.android.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.android.kernelmanager.utils.Device;
import com.android.kernelmanager.views.recyclerview.CardView;
import com.android.kernelmanager.views.recyclerview.DescriptionView;
import com.android.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.util.List;


public class DeviceFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        String processor = Device.CPUInfo.getInstance().getProcessor();
        String hardware = Device.CPUInfo.getInstance().getVendor();
        String features = Device.CPUInfo.getInstance().getFeatures();
        int ram = (int) Device.MemInfo.getInstance().getTotalMem();

        if (!processor.isEmpty()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.processor), processor));
        }
        if (!hardware.isEmpty()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.vendor), hardware));
        }
        if (!features.isEmpty()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.features), features));
        }
        if (ram > 0) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.ram), ram + getString(R.string.mb)));
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        String[][] deviceInfos = {
                {getString(R.string.android_version), Device.getVersion()},
                {getString(R.string.android_api_level), String.valueOf(Device.getSDK())},
                {getString(R.string.android_codename), Device.getCodename()},
                {getString(R.string.fingerprint), Device.getFingerprint()},
                {getString(R.string.baseband), Device.getBaseBand()},
                {getString(R.string.bootloader), Device.getBootloader()},
                {getString(R.string.rom), Device.ROMInfo.getInstance().getVersion()},
        };

        String[][] boardInfos = {
                {getString(R.string.hardware), Device.getHardware()},
                {getString(R.string.architecture), Device.getArchitecture()},
        };

        CardView deviceCard = new CardView();
        String vendor = Device.getVendor();
        vendor = vendor.substring(0, 1).toUpperCase() + vendor.substring(1);
        deviceCard.setTitle(vendor + " " + Device.getModel());

        CardView boardCard = new CardView();
        boardCard.setTitle(Device.getBoard().toUpperCase());

        for (String[] deviceInfo : deviceInfos) {
            if (deviceInfo[1] != null && deviceInfo[1].isEmpty()) {
                continue;
            }
            DescriptionView info = new DescriptionView();
            info.setTitle(deviceInfo[0]);
            info.setSummary(deviceInfo[1]);
            deviceCard.addItem(info);
        }

        for (String[] boardInfo : boardInfos) {
            if (boardInfo[1] != null && boardInfo[1].isEmpty()) {
                continue;
            }
            DescriptionView info = new DescriptionView();
            info.setTitle(boardInfo[0]);
            info.setSummary(boardInfo[1]);
            boardCard.addItem(info);
        }

        items.add(deviceCard);
        items.add(boardCard);
    }

    @Override
    protected boolean showAd() {
        return true;
    }

}
