
package com.android.kernelmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.kernelmanager.R;


public class LoadingFragment extends BaseFragment {

    private String mTitle;
    private String mSummary;

    private TextView mTitleView;
    private TextView mSummaryView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);

        mTitleView = rootView.findViewById(R.id.title);
        mSummaryView = rootView.findViewById(R.id.summary);

        setup();
        return rootView;
    }

    public void setTitle(String title) {
        mTitle = title;
        setup();
    }

    public void setSummary(String summary) {
        mSummary = summary;
        setup();
    }

    private void setup() {
        if (mTitleView != null) {
            if (mTitle == null) {
                mTitleView.setVisibility(View.GONE);
            } else {
                mTitleView.setVisibility(View.VISIBLE);
                mTitleView.setText(mTitle);
            }

            mSummaryView.setText(mSummary);
        }
    }
}
