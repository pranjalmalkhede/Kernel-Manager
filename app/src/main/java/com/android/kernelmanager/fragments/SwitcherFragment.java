
package com.android.kernelmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.kernelmanager.R;



public class SwitcherFragment extends BaseFragment {

    private static final String PACKAGE = SwitcherFragment.class.getCanonicalName();
    private static final String INTENT_TITLE = PACKAGE + ".INTENT.TITLE";
    private static final String INTENT_SUMMARY = PACKAGE + ".INTENT.SUMMARY";
    private static final String INTENT_CHECKED = PACKAGE + ".INTENT.CHECKED";

    public static SwitcherFragment newInstance(String title, String summary, boolean checked,
                                               CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        Bundle args = new Bundle();
        args.putString(INTENT_TITLE, title);
        args.putString(INTENT_SUMMARY, summary);
        args.putBoolean(INTENT_CHECKED, checked);
        SwitcherFragment fragment = new SwitcherFragment();
        fragment.setArguments(args);
        fragment.mOnCheckedChangeListener = onCheckedChangeListener;
        return fragment;
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switcher, container, false);

        String title = getArguments().getString(INTENT_TITLE);
        String summary = getArguments().getString(INTENT_SUMMARY);
        boolean checked = getArguments().getBoolean(INTENT_CHECKED);

        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.summary)).setText(summary);
        SwitchCompat mSwitch = view.findViewById(R.id.switcher);
        mSwitch.setChecked(checked);
        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        return view;
    }

}
