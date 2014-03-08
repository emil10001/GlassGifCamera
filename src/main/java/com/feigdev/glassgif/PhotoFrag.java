package com.feigdev.glassgif;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import com.google.android.glass.sample.camera.CameraPreview;

import java.io.IOException;

/**
 * Much of the content comes from here: http://www.vogella.com/tutorials/AndroidCamera/article.html
 */
public class PhotoFrag extends Fragment {
    private static final String TAG = "PhotoFrag";
    private Camera camera;
    private CameraPreview cameraPreview;
    private SurfaceView preview;
    private SurfaceHolder holder;

    public PhotoFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        preview = (SurfaceView) rootView.findViewById(R.id.preview);
        preview.getHolder().addCallback(mSurfaceHolderCallback);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        new GlassPhotoDelay().execute();

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    private void initCamera() {
        Log.d(TAG, "initCamera");

        // do we have a camera?
        if (!getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(TAG, "No camera on this device");
            return;
        }

        camera = Camera.open();

        /**
         * The camera preview on Glass needs certain special parameters to run properly
         * SO help: http://stackoverflow.com/a/19257078/974800
         */
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFpsRange(30000, 30000);
        camera.setParameters(params);

        cameraPreview = new CameraPreview(getActivity());
        cameraPreview.setCamera(camera);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();

    }

    private void takePictures() {
        Log.d(TAG, "takePictures");

        camera.takePicture(null, null,
                new PhotoHandler());

//        for (int i = 0; i < 5; i++) {
//            Log.d(TAG, "take picture " + i);
//            camera.takePicture(null, null,
//                    new PhotoHandler());
//        }

    }


    /**
     * There is currently a race condition where using a voice command to launch,
     * then trying to grab the camera will fail, because the microphone is still locked
     * <p/>
     * http://stackoverflow.com/a/20154537/974800
     * https://code.google.com/p/google-glass-api/issues/detail?id=259
     */
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


    private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder hldr) {
            holder = hldr;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Nothing to do here.
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // Nothing to do here.
        }
    };


}
