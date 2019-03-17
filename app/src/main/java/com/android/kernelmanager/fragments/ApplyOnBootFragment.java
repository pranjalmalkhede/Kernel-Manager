
package com.android.kernelmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.kernelmanager.R;
import com.android.kernelmanager.fragments.kernel.CPUFragment;
import com.android.kernelmanager.fragments.kernel.GPUFragment;
import com.android.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.android.kernelmanager.utils.AppSettings;

import java.util.HashMap;


public class ApplyOnBootFragment extends BaseFragment {

    private static final String PACKAGE = ApplyOnBootFragment.class.getCanonicalName();
    private static final String INTENT_CATEGORY = PACKAGE + ".INTENT.CATEGORY";

    public static final String CPU = "cpu_onboot";

    public static final String GPU = "gpu_onboot";



    private static final HashMap<Class, String> sAssignments = new HashMap<>();

    static {
        sAssignments.put(CPUFragment.class, CPU);
        sAssignments.put(GPUFragment.class, GPU);

    }

    public static String getAssignment(Class fragment) {
        if (!sAssignments.containsKey(fragment)) {
            throw new RuntimeException("Assignment key does not exists: " + fragment.getSimpleName());
        }
        return sAssignments.get(fragment);
    }

    public static ApplyOnBootFragment newInstance(RecyclerViewFragment recyclerViewFragment) {
        Bundle args = new Bundle();
        args.putString(INTENT_CATEGORY, getAssignment(recyclerViewFragment.getClass()));
        ApplyOnBootFragment fragment = new ApplyOnBootFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_apply_on_boot, container, false);

            final String category = getArguments().getString(INTENT_CATEGORY);
            SwitchCompat switcher = rootView.findViewById(R.id.switcher);
            switcher.setChecked(AppSettings.getBoolean(category, false, getActivity()));
            switcher.setOnCheckedChangeListener((buttonView, isChecked) ->
                    AppSettings.saveBoolean(category, isChecked, getActivity()));
            return rootView;

    }

}
