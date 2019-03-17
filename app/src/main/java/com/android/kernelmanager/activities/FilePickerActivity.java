
package com.android.kernelmanager.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;

import com.android.kernelmanager.R;
import com.android.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.android.kernelmanager.utils.Utils;
import com.android.kernelmanager.utils.ViewUtils;
import com.android.kernelmanager.utils.root.RootFile;
import com.android.kernelmanager.views.dialog.Dialog;
import com.android.kernelmanager.views.recyclerview.DescriptionView;
import com.android.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;


public class FilePickerActivity extends BaseActivity {

    public static final String PATH_INTENT = "path";
    public static final String EXTENSION_INTENT = "extension";
    public static final String RESULT_INTENT = "result";

    private String mPath;
    private String mExtension;
    private FilePickerFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initToolBar();

        mPath = getIntent().getStringExtra(PATH_INTENT);
        mExtension = getIntent().getStringExtra(EXTENSION_INTENT);

        RootFile path = new RootFile(mPath);
        if (!path.exists() || !path.isDirectory()) {
            mPath = "/";
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mFragment
                = (FilePickerFragment) getFragment(), "fragment").commit();
    }

    private Fragment getFragment() {
        Fragment filePickerFragment = getSupportFragmentManager().findFragmentByTag("fragment");
        if (filePickerFragment == null) {
            filePickerFragment = FilePickerFragment.newInstance(mPath, mExtension);
        }
        return filePickerFragment;
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null && !mFragment.mPath.equals("/")) {
            mFragment.mPath = new RootFile(mFragment.mPath).getParentFile().toString();
            mFragment.reload();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        getSupportFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

    public static class FilePickerFragment extends RecyclerViewFragment {

        private String mPath;
        private String mExtension;
        private Drawable mDirImage;
        private Drawable mFileImage;
        private Dialog mPickDialog;

        @Override
        protected boolean showViewPager() {
            return false;
        }

        public static FilePickerFragment newInstance(String path, String extension) {
            Bundle args = new Bundle();
            args.putString(PATH_INTENT, path);
            args.putString(EXTENSION_INTENT, extension);
            FilePickerFragment fragment = new FilePickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void init() {
            super.init();
            if (mPath == null) {
                mPath = getArguments().getString(PATH_INTENT);
            }
            if (mExtension == null) {
                mExtension = getArguments().getString(EXTENSION_INTENT);
            }
            int accentColor = ViewUtils.getThemeAccentColor(getContext());
            if (mDirImage == null) {
                mDirImage = DrawableCompat.wrap(
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_dir));
                DrawableCompat.setTint(mDirImage, accentColor);
            }
            if (mFileImage == null) {
                mFileImage = DrawableCompat.wrap(
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_file));
                DrawableCompat.setTint(mFileImage, ViewUtils.getTextSecondaryColor(getContext()));
            }
            if (mPickDialog != null) {
                mPickDialog.show();
            }

            ActionBar actionBar;
            if ((actionBar = ((FilePickerActivity) getActivity()).getSupportActionBar()) != null) {
                actionBar.setTitle(mPath);
            }
        }

        @Override
        protected void addItems(List<RecyclerViewItem> items) {
            load(items);
        }

        @Override
        protected void postInit() {
            super.postInit();
            ActionBar actionBar;
            if ((actionBar = ((BaseActivity) getActivity()).getSupportActionBar()) != null) {
                actionBar.setTitle(mPath);
            }
        }

        private void reload() {
            clearItems();
            reload(new ReloadHandler());
        }

        private static class ReloadHandler
                extends RecyclerViewFragment.ReloadHandler<FilePickerFragment> {
            @Override
            public void onPostExecute(FilePickerFragment fragment, List<RecyclerViewItem> items) {
                super.onPostExecute(fragment, items);

                BaseActivity activity = (BaseActivity) fragment.getActivity();
                ActionBar actionBar;
                if (activity != null
                        && (actionBar = activity.getSupportActionBar()) != null) {
                    actionBar.setTitle(fragment.mPath);
                }
            }
        }

        @Override
        protected void load(List<RecyclerViewItem> items) {
            super.load(items);

            RootFile path = new RootFile(mPath).getRealPath();
            mPath = path.toString();

            if (!path.isDirectory()) {
                mPath = path.getParentFile().toString();
                reload();
                return;
            }
            List<RootFile> dirs = new ArrayList<>();
            List<RootFile> files = new ArrayList<>();
            for (RootFile file : path.listFiles()) {
                if (file.isDirectory()) {
                    dirs.add(file);
                } else {
                    files.add(file);
                }
            }

            final RootFile returnDir = path.getParentFile();
            if (returnDir.isDirectory()) {
                DescriptionView descriptionViewParent = new DescriptionView();
                descriptionViewParent.setSummary("..");
                descriptionViewParent.setDrawable(mDirImage);
                descriptionViewParent.setOnItemClickListener(item -> {
                    mPath = returnDir.toString();
                    reload();
                });

                items.add(descriptionViewParent);
            }

            for (final RootFile dir : dirs) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setSummary(dir.getName());
                descriptionView.setDrawable(mDirImage);
                descriptionView.setOnItemClickListener(item -> {
                    mPath = dir.toString();
                    reload();
                });

                items.add(descriptionView);
            }
            for (final RootFile file : files) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setSummary(file.getName());
                descriptionView.setDrawable(mFileImage);
                descriptionView.setOnItemClickListener(item -> {
                    if (mExtension != null && !mExtension.isEmpty() && file.getName() != null
                            && !file.getName().endsWith(mExtension)) {
                        Utils.toast(getString(R.string.wrong_extension, mExtension), getActivity());
                    } else {
                        mPickDialog =
                                ViewUtils.dialogBuilder(getString(R.string.select_question,
                                        file.getName()),
                                        (dialog, which) -> {
                                        },
                                        (dialog, which) -> {
                                            Intent intent = new Intent();
                                            intent.putExtra(RESULT_INTENT, file.toString());
                                            getActivity().setResult(0, intent);
                                            getActivity().finish();
                                        },
                                        dialog -> mPickDialog = null, getActivity());
                        mPickDialog.show();
                    }
                });

                items.add(descriptionView);
            }
        }
    }

}
