package com.feigdev.glassgif;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements GifFlowControl {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onResume(){
        super.onResume();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new PhotoFrag())
                .commit();
    }

    @Override
    public void startBuild(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new GifBuilderFrag())
                .commit();
    }

    @Override
    public void startDisplay(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new GifDisplayFrag())
                .commit();
    }

}
