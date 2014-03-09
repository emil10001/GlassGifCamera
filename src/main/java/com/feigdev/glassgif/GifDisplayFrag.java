package com.feigdev.glassgif;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.feigdev.reusableandroidutils.graphics.GifWebView;

public class GifDisplayFrag extends Fragment {
    private static final String TAG = "GifDisplayFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return new GifWebView(getActivity(), StaticManager.gifFile, "480","360");
    }
}
