package com.feigdev.glassgif;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.feigdev.reusableandroidutils.PlatformUtils;
import com.feigdev.reusableandroidutils.SimpleFileUtils;
import com.feigdev.reusableandroidutils.graphics.ImageTools;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ejf3 on 3/8/14.
 */
public class GifBuilderFrag extends Fragment {
    private static final String TAG = "GifBuilderFrag";
    private GifFlowControl flowControl;
    private ImageView curImg;
    private ProgressBar progressBar;
    private AsyncTask updater;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        flowControl = (GifFlowControl) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.builder, container, false);

        curImg = (ImageView) rootView.findViewById(R.id.cur_img);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new BackgroundGif().execute();
        updater = new ProgressUpdater().execute();
    }

    @Override
    public void onPause() {
        updater.cancel(true);
        super.onPause();
    }


    private class ProgressUpdater extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int count = 0;
            try {
                while (true) {
                    publishProgress((count++ % 100));
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

    }

    private class BackgroundGif extends AsyncTask<Void, Bitmap, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "GlassPhotoDelay");
            String gifFile = SimpleFileUtils.getDir() + File.separator + System.currentTimeMillis() + ".gif";

            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
            for (String filename : StaticManager.listOfFiles) {
                File f = new File(filename);
                if (!f.exists())
                    continue;
                Bitmap b = ImageTools.generatePic(filename);
                bitmaps.add(b);
                publishProgress(b);
            }
            byte[] bytes = ImageTools.generateGIF(bitmaps);
            SimpleFileUtils.write(gifFile, bytes);
            String newGifFile = SimpleFileUtils.getSdDir() + File.separator
                    + "DCIM" + File.separator + "Camera" + File.separator
                    + System.currentTimeMillis() + ".gif";
            try {
                SimpleFileUtils.copy(gifFile, newGifFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String filename : StaticManager.listOfFiles) {
                File f = new File(filename);
                if (f.exists())
                    f.delete();
            }

            return gifFile;
        }

        protected void onProgressUpdate(Bitmap... progress) {
            curImg.setImageBitmap(progress[0]);
        }

        @Override
        protected void onPostExecute(String params) {
            Log.d(TAG, "creating card with params: " + params);
            if (PlatformUtils.isGlass()) {
                if (null == params)
                    return;

                // http://stackoverflow.com/a/21843601/974800
                Uri imgUri = Uri.fromFile(new File(params));

                StaticManager.gifFile = params;

                // create card
                Card gifCard = new Card(getActivity());
                if (null != imgUri) {
                    gifCard.addImage(imgUri);
                    gifCard.setImageLayout(Card.ImageLayout.FULL);
                    gifCard.setText(StaticManager.gifFile);
                } else
                    gifCard.setText("failed to get image uri");

                // menus currently not supported
                // https://code.google.com/p/google-glass-api/issues/detail?id=320

                TimelineManager tlm = TimelineManager.from(getActivity());
                tlm.insert(gifCard);
                Log.d(TAG, "inserted into timeline!");
            }

            flowControl.startDisplay();
        }
    }


}