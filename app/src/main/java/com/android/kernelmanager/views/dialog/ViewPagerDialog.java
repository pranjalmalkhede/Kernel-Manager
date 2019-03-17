
package com.android.kernelmanager.views.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.kernelmanager.R;
import com.android.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;



public class ViewPagerDialog extends DialogFragment {

    public static ViewPagerDialog newInstance(int height, List<Fragment> fragments) {
        ViewPagerDialog fragment = new ViewPagerDialog();
        fragment.mHeight = height;
        fragment.mFragments = fragments;
        return fragment;
    }

    private int mHeight;
    private List<Fragment> mFragments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager_view, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.viewpager);
        CirclePageIndicator indicator = rootView.findViewById(R.id.indicator);
        viewPager.setAdapter(new RecyclerViewFragment.ViewPagerAdapter(getChildFragmentManager(), mFragments));
        indicator.setViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = mHeight;
                view.requestLayout();
            }
        });
    }
}
