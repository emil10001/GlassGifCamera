package com.feigdev.glassgif;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * http://www.vogella.com/tutorials/AndroidCamera/article.html
 */
public class PhotoHandler implements Camera.PictureCallback {
    private static final String TAG = "PhotoHandler";

    static {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists())
            pictureFileDir.mkdirs();

        Log.d(TAG, pictureFileDir.toString() + " exists? " + pictureFileDir.exists());
    }

    public PhotoHandler() {
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFileDir = getDir();
        if (!pictureFileDir.exists())
            pictureFileDir.mkdirs();

        if (!pictureFileDir.exists()) {
            Log.d(TAG, "Couldn't make directory");
            return;
        }


        String photoFile = "Picture_" + System.currentTimeMillis() + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Log.d(TAG, "New Image saved:" + photoFile);
        } catch (Exception error) {
            Log.d(TAG, "File" + filename + "not saved: "
                    + error.getMessage());
        }
    }

    private static File getDir() {
        String sdDir = Environment.getExternalStorageDirectory().toString();
        return new File(sdDir, "GlassGif");
    }
}
