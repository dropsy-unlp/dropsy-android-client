package com.fuentesfernandez.dropsy.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        Uri uri = Uri.parse("https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        vidView.setVideoURI(uri);
        vidView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vidView.stopPlayback();
    }


}
