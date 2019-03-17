
package com.android.kernelmanager.utils;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.mattprecious.swirl.SwirlView;


public class FingerprintUiHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private boolean mListening;
    private final FingerprintManagerCompat mFingerprintManagerCompat;
    private final SwirlView mSwirlView;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;

    /**
     * Builder class for {@link FingerprintUiHelper} in which injected fields from Dagger
     * holds its fields and takes other arguments in the {@link #build} method.
     */
    public static class FingerprintUiHelperBuilder {
        private final FingerprintManagerCompat mFingerprintManagerCompat;

        public FingerprintUiHelperBuilder(FingerprintManagerCompat fingerprintManagerCompat) {
            mFingerprintManagerCompat = fingerprintManagerCompat;
        }

        public FingerprintUiHelper build(SwirlView swirlView, Callback callback) {
            return new FingerprintUiHelper(mFingerprintManagerCompat, swirlView, callback);
        }
    }

    /**
     * Constructor for {@link FingerprintUiHelper}. This method is expected to be called from
     * only the {@link FingerprintUiHelperBuilder} class.
     */
    private FingerprintUiHelper(FingerprintManagerCompat fingerprintManagerCompat, SwirlView swirlView,
                                Callback callback) {
        mFingerprintManagerCompat = fingerprintManagerCompat;
        mSwirlView = swirlView;
        mCallback = callback;
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!mListening) {
            mListening = true;
            mCancellationSignal = new CancellationSignal();
            mSelfCancelled = false;
            mFingerprintManagerCompat
                    .authenticate(cryptoObject, 0, mCancellationSignal, this, null);
            mSwirlView.setState(SwirlView.State.ON);
        }
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            mListening = false;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError();
            mCallback.onError();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError();
    }

    @Override
    public void onAuthenticationFailed() {
        showError();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mSwirlView.setState(SwirlView.State.OFF);
        mSwirlView.postDelayed(mCallback::onAuthenticated, 100);
    }

    private void showError() {
        mSwirlView.setState(SwirlView.State.ERROR);
    }

    public interface Callback {
        void onAuthenticated();

        void onError();
    }
}
