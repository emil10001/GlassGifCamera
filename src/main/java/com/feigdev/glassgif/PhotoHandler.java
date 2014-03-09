package com.feigdev.glassgif;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.feigdev.reusableandroidutils.ImageTools;

import java.io.File;
import java.io.FileOutputStream;

/**
 * http://www.vogella.com/tutorials/AndroidCamera/article.html
 */
public class PhotoHandler implements Camera.PictureCallback {
    private static final String TAG = "PhotoHandler";
    private PhotoLooper photoLooper;

    static {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists())
            pictureFileDir.mkdirs();

        Log.d(TAG, pictureFileDir.toString() + " exists? " + pictureFileDir.exists());
    }

    public PhotoHandler(PhotoLooper photoLooper) {
        super();
        this.photoLooper = photoLooper;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(TAG, "onPictureTaken");
        new PersistData().execute(data);
    }

    private class PersistData extends AsyncTask<byte[], Void, String> {

        @Override
        protected String doInBackground(byte[]... params) {
            Log.d(TAG, "PersistData");
            File pictureFileDir = getDir();
            if (!pictureFileDir.exists())
                pictureFileDir.mkdirs();

            if (!pictureFileDir.exists()) {
                Log.e(TAG, "Couldn't make directory");
                return null;
            }

            if (null == params || null == params[0])
                return null;

            String photoFile = "Picture_" + System.currentTimeMillis() + ".jpg";

            String filename = pictureFileDir.getPath() + File.separator + photoFile;

            File pictureFile = new File(filename);

            byte[] smallerData = ImageTools.bitmapToByteArray(ImageTools.shrinkBitmap(params[0]));

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(smallerData);
                fos.close();
                Log.d(TAG, "New Image saved:" + photoFile);
            } catch (Exception error) {
                Log.e(TAG, "File" + filename + "not saved: ", error);
            }

            return filename;
        }

        @Override
        protected void onPostExecute(String params) {
            photoLooper.retakePicture(params);
        }
    }



    public static File getDir() {
        String sdDir = Environment.getExternalStorageDirectory().toString();
        return new File(sdDir, "GlassGif");
    }
}
