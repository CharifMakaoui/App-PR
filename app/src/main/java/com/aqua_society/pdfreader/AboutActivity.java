package com.aqua_society.pdfreader;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

public class AboutActivity extends AppCompatActivity {

    WebView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        videoView = (WebView) findViewById(R.id.aboutView);
        try {
            StartAbout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void StartAbout() throws IOException {

        InputStream is = getAssets().open("index.html");
        int size = is.available();

        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        String str = new String(buffer);

        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.loadDataWithBaseURL("", str, "text/html", "UTF-8", "");
    }
}
