package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH = "com.bignerdranch.android.criminalintent.image_path";

    public static ImageFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }
}
