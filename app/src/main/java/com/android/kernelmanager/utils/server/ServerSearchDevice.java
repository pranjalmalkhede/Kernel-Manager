
package com.android.kernelmanager.utils.server;

import android.app.Activity;

import com.android.kernelmanager.utils.WebpageReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ServerSearchDevice extends Server {

    private static final String DEVICE_GET = "/kerneladiutor/api/v1/device/get";
    private static final String BOARD_GET = "/kerneladiutor/api/v1/board/get";

    public interface DeviceSearchListener {
        void onDevicesResult(List<DeviceInfo> devices, int page);

        void onDevicesFailure();
    }

    public interface BoardSearchListener {
        void onBoardResult(List<String> boards);

        void onBoardFailure();
    }

    private Activity mActivity;

    private WebpageReader mDeviceReader;
    private WebpageReader mBoardReader;

    public ServerSearchDevice(String address, Activity activity) {
        super(address);
        mActivity = activity;
    }

    public void getDevices(final DeviceSearchListener deviceSearchListener, final int page, String board) {
        mDeviceReader = new WebpageReader(mActivity, new WebpageReader.WebpageListener() {
            @Override
            public void onSuccess(String url, String raw, CharSequence html) {
                try {
                    JSONArray devices = new JSONArray(raw);
                    List<DeviceInfo> list = new ArrayList<>();

                    for (int i = 0; i < devices.length(); i++) {
                        list.add(new DeviceInfo(devices.getJSONObject(i)));
                    }
                    deviceSearchListener.onDevicesResult(list, page == 0 ? 1 : page);
                } catch (JSONException ignored) {
                    deviceSearchListener.onDevicesFailure();
                }
            }

            @Override
            public void onFailure(String url) {
                deviceSearchListener.onDevicesFailure();
            }
        });
        mDeviceReader.get(getAddress(DEVICE_GET,
                new Query("page", String.valueOf(page == 0 ? 1 : page)),
                new Query("board", board)));
    }

    public void getBoards(final BoardSearchListener boardSearchListener) {
        mBoardReader = new WebpageReader(mActivity, new WebpageReader.WebpageListener() {
            @Override
            public void onSuccess(String url, String raw, CharSequence html) {
                try {
                    JSONArray boards = new JSONArray(raw);
                    List<String> list = new ArrayList<>();

                    for (int i = 0; i < boards.length(); i++) {
                        list.add(boards.getString(i));
                    }
                    Collections.sort(list);
                    boardSearchListener.onBoardResult(list);
                } catch (JSONException ignored) {
                    boardSearchListener.onBoardFailure();
                }
            }

            @Override
            public void onFailure(String url) {
                boardSearchListener.onBoardFailure();
            }
        });
        mBoardReader.get(getAddress(BOARD_GET));
    }

    public void cancel() {
        if (mDeviceReader != null) mDeviceReader.cancel();
        if (mBoardReader != null) mBoardReader.cancel();
    }

}
