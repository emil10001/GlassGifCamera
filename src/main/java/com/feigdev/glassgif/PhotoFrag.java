package com.feigdev.glassgif;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
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
    private int cameraId = 0;
    private CameraPreview cameraPreview;
    private FrameLayout preview;

    public PhotoFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        cameraId = findFrontFacingCamera();
        preview = (FrameLayout) rootView.findViewById(R.id.preview);

        return rootView;
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            Log.d(TAG, "Camera found");
            cameraId = i;
            break;
        }
        return cameraId;
    }

    @Override
    public void onResume() {
        super.onResume();

        // do we have a camera?
        if (!getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(TAG, "No camera on this device");
            return;
        }

        if (cameraId < 0) {
            Log.d(TAG, "No front facing camera found.");
            return;
        }

        camera = Camera.open(cameraId);

        // SO help: http://stackoverflow.com/a/19257078/974800
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFpsRange(30000, 30000);
        camera.setParameters(params);

        cameraPreview = new CameraPreview(getActivity(), camera);
        preview.addView(cameraPreview);


//        camera.takePicture(null, null,
//                new PhotoHandler());

//        for (int i = 0; i < 5; i++) {
//            Log.d(TAG, "take picture " + i);
//            camera.takePicture(null, null,
//                    new PhotoHandler());
//        }

    }

    @Override
    public void onPause() {
        preview.removeView(cameraPreview);

        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }
}
