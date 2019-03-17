
package com.android.kernelmanager.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.kernelmanager.R;
import com.android.kernelmanager.fragments.BaseFragment;
import com.android.kernelmanager.utils.AppSettings;
import com.android.kernelmanager.utils.Utils;
import com.android.kernelmanager.utils.ViewUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;



public class BannerResizerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.DONATED) {
            Utils.toast("nice try", this);
            return;
        }
        setContentView(R.layout.activity_fragments);

        initToolBar();

        getSupportActionBar().setTitle(getString(R.string.banner_resizer));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(),
                BannerResizerFragment.class.getSimpleName()).commit();
        findViewById(R.id.content_frame).setPadding(0,
                Math.round(ViewUtils.getActionBarSize(this)), 0, 0);
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(BannerResizerFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new BannerResizerFragment();
        }
        return fragment;
    }

    public static class BannerResizerFragment extends BaseFragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_banner_resizer, container, false);

            final int minHeight = Math.round(getResources().getDimension(R.dimen.banner_min_height));
            int defaultHeight = Math.round(getResources().getDimension(R.dimen.banner_default_height));
            int maxHeight = Math.round(getResources().getDimension(R.dimen.banner_max_height));

            final View banner = rootView.findViewById(R.id.banner_view);
            final int px = AppSettings.getBannerSize(getActivity());
            setHeight(banner, px);

            final TextView text = rootView.findViewById(R.id.seekbar_text);
            text.setText(Utils.strFormat("%d" + getString(R.string.px), px));

            final DiscreteSeekBar seekBar = rootView.findViewById(R.id.seekbar);
            seekBar.setMax(maxHeight - minHeight);
            seekBar.setProgress(px - minHeight);
            seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    text.setText(Utils.strFormat("%d" + getString(R.string.px), value + minHeight));
                    setHeight(banner, value + minHeight);
                }

                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                }
            });

            rootView.findViewById(R.id.cancel).setOnClickListener(v
                    -> seekBar.setProgress(px - minHeight));

            rootView.findViewById(R.id.restore).setOnClickListener(v
                    -> seekBar.setProgress(defaultHeight - minHeight));

            rootView.findViewById(R.id.done).setOnClickListener(v -> {
                AppSettings.saveBannerSize(seekBar.getProgress() + minHeight, getActivity());
                getActivity().finish();
            });

            return rootView;
        }

        private int getAdjustedSize(int px) {
            return Math.round(px / 2.8f);
        }

        private void setHeight(View banner, int px) {
            ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
            layoutParams.height = getAdjustedSize(px);
            banner.requestLayout();
        }

    }

}
