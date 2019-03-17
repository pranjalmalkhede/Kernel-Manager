
package com.android.kernelmanager.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.android.kernelmanager.BuildConfig;
import com.android.kernelmanager.R;
import com.android.kernelmanager.utils.AppSettings;
import com.android.kernelmanager.utils.Device;
import com.android.kernelmanager.utils.Log;
import com.android.kernelmanager.utils.Utils;
import com.android.kernelmanager.utils.kernel.cpu.CPUBoost;
import com.android.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.android.kernelmanager.utils.kernel.cpu.MSMPerformance;
import com.android.kernelmanager.utils.kernel.cpu.Temperature;
import com.android.kernelmanager.utils.kernel.gpu.GPU;
import com.android.kernelmanager.utils.root.RootUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mRootAccess;
    private TextView mBusybox;
    private TextView mCollectInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = findViewById(R.id.root_access_text);
        mBusybox = findViewById(R.id.busybox_text);
        mCollectInfo = findViewById(R.id.info_collect_text);

        // Hide huge banner in landscape mode
        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {

                new CheckingTask(this).execute();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0) {

            int result = data == null ? -1 : data.getIntExtra("result", -1);
            if (result == 0) {
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(
                            "com.android.kernelmanager", 0);
                    Utils.writeFile(applicationInfo.dataDir + "/license",
                            Utils.encodeString(Utils.getAndroidId(this)), false, true);
                } catch (PackageManager.NameNotFoundException ignored) {
                }
            }
            launch(result);

        } else if (requestCode == 1) {


            if (resultCode == 1) {
                new CheckingTask(this).execute();
            } else {
                finish();
            }

        }
    }

    /**
     * Launch {@link NavigationActivity} which is the actual interface
     *
     */
    private void launch(int code) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("result", code);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
    }

    private static class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private WeakReference<MainActivity> mRefActivity;

        private boolean mHasRoot;
        private boolean mHasBusybox;

        private CheckingTask(MainActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mHasRoot = RootUtils.rootAccess();
            publishProgress(0);

            if (mHasRoot) {
                mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);

                if (mHasBusybox) {
                    collectData();
                    publishProgress(2);
                }
            }
            return null;
        }

        /**
         * Determinate what sections are supported
         */
        private void collectData() {
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            CPUBoost.getInstance();

            // Assign core ctl min cpu
            CPUFreq.getInstance(activity);

            Device.CPUInfo.getInstance();
            Device.Input.getInstance();
            Device.MemInfo.getInstance();
            Device.ROMInfo.getInstance();
            Device.TrustZone.getInstance();
            GPU.supported();

            MSMPerformance.getInstance();

            Temperature.getInstance(activity);


            try {
                ProviderInstaller.installIfNeeded(activity);
            } catch (GooglePlayServicesNotAvailableException
                    | GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

            if (!BuildConfig.DEBUG) {
                // Send SoC type to analytics to collect stats
                Answers.getInstance().logCustom(new CustomEvent("SoC")
                        .putCustomAttribute("type", Device.getBoard()));
            }

            Log.crashlyticsI(TAG, "Build Display ID: "
                    + Device.getBuildDisplayId());
            Log.crashlyticsI(TAG, "ROM: "
                    + Device.ROMInfo.getInstance().getVersion());
            Log.crashlyticsI(TAG, "Kernel version: "
                    + Device.getKernelVersion(true));
            Log.crashlyticsI(TAG, "Board: " +
                    Device.getBoard());
        }

        /**
         * Let the user know what we are doing right now
         *
         * @param values progress
         *               0: Checking root
         *               1: Checking busybox/toybox
         *               2: Collecting information
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            int red = ContextCompat.getColor(activity, R.color.red);
            int green = ContextCompat.getColor(activity, R.color.green);
            switch (values[0]) {
                case 0:
                    activity.mRootAccess.setTextColor(mHasRoot ? green : red);
                    break;
                case 1:
                    activity.mBusybox.setTextColor(mHasBusybox ? green : red);
                    break;
                case 2:
                    activity.mCollectInfo.setTextColor(green);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
            if (!mHasRoot || !mHasBusybox) {
                Intent intent = new Intent(activity, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, activity.getString(mHasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        mHasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                activity.startActivity(intent);
                activity.finish();

                if (!BuildConfig.DEBUG) {
                    // Send problem to analytics to collect stats
                    Answers.getInstance().logCustom(new CustomEvent("Can't access")
                            .putCustomAttribute("no_found", mHasRoot ? "no busybox" : "no root"));
                }
                return;
            }

            // Initialize Google Ads
            MobileAds.initialize(activity, "ca-app-pub-1851546461606210~9501142287");

            // Execute another AsyncTask for license checking
            new LoadingTask(activity).execute();
        }

    }

    private static class LoadingTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<MainActivity> mRefActivity;

        private ApplicationInfo mApplicationInfo;
        private PackageInfo mPackageInfo;
        private boolean mPatched;
        private boolean mInternetAvailable;
        private boolean mLicensedCached;

        private LoadingTask(MainActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = mRefActivity.get();
            if (activity == null) return;
            try {
                PackageManager packageManager = activity.getPackageManager();
                mApplicationInfo = packageManager.getApplicationInfo(
                        "com.grarak.kerneladiutordonate", 0);
                mPackageInfo = packageManager.getPackageInfo(
                        "com.grarak.kerneladiutordonate", 0);
                if (BuildConfig.DEBUG) {
                    Utils.DONATED = false;
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MainActivity activity = mRefActivity.get();
            if (activity == null) return false;

            if (mApplicationInfo != null && mPackageInfo != null
                    && mPackageInfo.versionCode == 130) {
                try {
                    mPatched = !Utils.checkMD5("5c7a92a5b2dcec409035e1114e815b00",
                            new File(mApplicationInfo.publicSourceDir))
                            || Utils.isPatched(mApplicationInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Utils.existFile(mApplicationInfo.dataDir + "/license")) {
                    String content = Utils.readFile(mApplicationInfo.dataDir + "/license");
                    if (!content.isEmpty() && (content = Utils.decodeString(content)) != null) {
                        if (content.equals(Utils.getAndroidId(activity))) {
                            mLicensedCached = true;
                        }
                    }
                }

                try {
                    if (!mLicensedCached) {
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
                        urlConnection.setRequestProperty("User-Agent", "Test");
                        urlConnection.setRequestProperty("Connection", "close");
                        urlConnection.setConnectTimeout(3000);
                        urlConnection.connect();
                        mInternetAvailable = urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
                    }
                } catch (IOException ignored) {
                }

                return !mPatched;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean donationValid) {
            super.onPostExecute(donationValid);

            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            if (donationValid && mLicensedCached) {
                activity.launch(0);
            } else if (donationValid && mInternetAvailable) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.grarak.kerneladiutordonate",
                        "com.grarak.kerneladiutordonate.MainActivity"));
                activity.startActivityForResult(intent, 0);
            } else if (donationValid) {
                activity.launch(1);
            } else {
                if (mPatched && !BuildConfig.DEBUG) {
                    Answers.getInstance().logCustom(new CustomEvent("Pirated")
                            .putCustomAttribute("android_id", Utils.getAndroidId(activity)));
                }
                activity.launch(mPatched ? 3 : -1);
            }
        }
    }

}
