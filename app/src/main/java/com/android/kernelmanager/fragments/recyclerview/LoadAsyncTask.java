
package com.android.kernelmanager.fragments.recyclerview;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;



class LoadAsyncTask<T extends RecyclerViewFragment, RESULT> extends AsyncTask<Void, Void, RESULT> {

    public abstract static class LoadHandler<T extends RecyclerViewFragment, RESULT> {
        public void onPreExecute(T fragment) {
        }

        public abstract RESULT doInBackground(T fragment);

        public void onPostExecute(T fragment, RESULT result) {
        }
    }

    private WeakReference<T> mRefFragment;
    private LoadHandler<T, RESULT> mListener;

    LoadAsyncTask(T fragment, @NonNull LoadHandler<T, RESULT> listener) {
        mRefFragment = new WeakReference<>(fragment);
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        T fragment = mRefFragment.get();
        if (fragment != null) {
            mListener.onPreExecute(fragment);
        }
    }

    @Override
    protected RESULT doInBackground(Void... voids) {
        T fragment = mRefFragment.get();
        if (fragment != null) {
            return mListener.doInBackground(fragment);
        }
        return null;
    }

    @Override
    protected void onPostExecute(RESULT result) {
        super.onPostExecute(result);

        T fragment = mRefFragment.get();
        if (fragment != null) {
            mListener.onPostExecute(fragment, result);
        }
    }
}
