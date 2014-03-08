package com.feigdev.glassgif;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Much of the content comes from here: http://www.vogella.com/tutorials/AndroidCamera/article.html
 */
public class PhotoFrag extends Fragment {
    private static final String TAG = "PhotoFrag";
    private Camera camera;
    private CameraPreview cameraPreview;
    private FrameLayout preview;
    private Handler handler = new Handler();

    public PhotoFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        preview = (FrameLayout) rootView.findViewById(R.id.preview);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

        new GlassPhotoDelay().execute();

    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
        preview.removeView(cameraPreview);

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    private void initCamera(){
        Log.d(TAG,"initCamera");

        // do we have a camera?
        if (!getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(TAG, "No camera on this device");
            return;
        }

        camera = Camera.open();

        // SO help: http://stackoverflow.com/a/19257078/974800
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFpsRange(30000, 30000);
        camera.setParameters(params);

        cameraPreview = new CameraPreview(getActivity(), camera);
        preview.addView(cameraPreview);

    }

    private void takePictures(){

        camera.takePicture(null, null,
                new PhotoHandler());

//        for (int i = 0; i < 5; i++) {
//            Log.d(TAG, "take picture " + i);
//            camera.takePicture(null, null,
//                    new PhotoHandler());
//        }

    }


    private class GlassPhotoDelay extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "GlassPhotoDelay");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            initCamera();
            takePictures();
        }
    }
}
