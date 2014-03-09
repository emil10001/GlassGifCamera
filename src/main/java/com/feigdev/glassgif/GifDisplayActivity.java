package com.feigdev.glassgif;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.File;

/**
 * Modified from original source
 * http://droid-blog.net/2011/10/17/tutorial-how-to-play-animated-gifs-in-android-part-3/
 */
public class GifDisplayActivity extends Activity {
    private static final String TAG = "GifDisplayActivity";
    private Point size;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        setContentView(new GifWebView(this));
    }

    public class GifWebView extends WebView {
        public GifWebView(Context context) {
            super(context);
            try {
                String gifFile = Uri.fromFile(new File(StaticManager.gifFile)).toString();
                String htmlContent = String.format("<html><body><img src=\"%s\" width=\"%s\" height=\"%s\"></body></html>",
                        new String[]{gifFile, "640","360" });
                // https://developer.android.com/reference/android/webkit/WebView.html#loadData(java.lang.String, java.lang.String, java.lang.String)
                loadDataWithBaseURL(gifFile, htmlContent, "text/html", "utf-8", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
