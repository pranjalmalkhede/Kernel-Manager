
package com.android.kernelmanager.utils;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class WebpageReader {

    public interface WebpageListener {

        void onSuccess(String url, String raw, CharSequence html);

        void onFailure(String url);
    }

    private Activity mActivity;
    private HttpURLConnection mConnection;
    private WebpageListener mWebpageListener;

    public WebpageReader(Activity activity, WebpageListener webpageListener) {
        mActivity = activity;
        mWebpageListener = webpageListener;
    }

    public void get(final String link) {
        new Thread(() -> {
            BufferedReader reader = null;
            try {
                mConnection = (HttpsURLConnection) new URL(link).openConnection();
                mConnection.setRequestMethod("GET");
                mConnection.setConnectTimeout(10000);
                mConnection.setReadTimeout(10000);

                InputStream inputStream;
                if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = mConnection.getInputStream();
                } else {
                    inputStream = mConnection.getErrorStream();
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                success(link, sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
                failure(link);
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void success(final String url, final String result) {
        mActivity.runOnUiThread(()
                -> mWebpageListener.onSuccess(url, result, Utils.htmlFrom(result)));
    }

    private void failure(final String url) {
        mActivity.runOnUiThread(() -> mWebpageListener.onFailure(url));
    }

    public void cancel() {
        new Thread(() -> {
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }).start();
    }

}
