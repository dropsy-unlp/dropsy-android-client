package com.fuentesfernandez.dropsy.Activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.widget.VideoView;


import com.fuentesfernandez.dropsy.R;

public class StreamActivity extends AppCompatActivity {
    VideoView vidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vidView = (VideoView)findViewById(R.id.videoView);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String url = preferences.getString("stream_url","https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        Uri uri = Uri.parse(url);
        vidView.setVideoURI(uri);
        vidView.start();
        Toast.makeText(this, "Streaming from " + url, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vidView.stopPlayback();
    }


}
