package com.feigdev.glassgif;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.feigdev.reusableandroidutils.ImageTools;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ejf3 on 3/8/14.
 */
public class GlassGifService extends Service {
    private static final String TAG = "GlassGifService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new BackgroundGif().execute();
        return super.onStartCommand(intent,flags,startId);
    }

    private class BackgroundGif extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "GlassPhotoDelay");
            String gifFile = PhotoHandler.getDir() + File.separator + System.currentTimeMillis() + ".gif";
            ImageTools.makeGif(StaticManager.listOfFiles, gifFile);
            return gifFile;
        }

        @Override
        protected void onPostExecute(String params) {
            Log.d(TAG, "creating card with params: " + params);
            if (null == params)
                return;

            // http://stackoverflow.com/a/21843601/974800
            Uri imgUri = Uri.fromFile(new File(params));

            StaticManager.gifFile = params;

            // create card
            Card gifCard = new Card(getApplicationContext());
            if (null != imgUri) {
                gifCard.addImage(imgUri);
                gifCard.setImageLayout(Card.ImageLayout.FULL);
                gifCard.setText(StaticManager.gifFile);
            } else
                gifCard.setText("failed to get image uri");

            // menus currently not supported
            // https://code.google.com/p/google-glass-api/issues/detail?id=320

            TimelineManager tlm = TimelineManager.from(getApplicationContext());
            tlm.insert(gifCard);
            Log.d(TAG, "inserted into timeline!");

            startActivity(new Intent(getApplicationContext(), GifDisplayActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            stopSelf();
        }
    }



}