package com.fuentesfernandez.dropsy.Fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.fuentesfernandez.dropsy.R;


public class StreamFragment extends Fragment {
    private VideoView vidView;
    private String sourceUrl;
    public StreamFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vidView = (VideoView) view.findViewById(R.id.videoView);
        playVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!vidView.isPlaying()){
            if (urlChanged()){
                playVideo();
            } else {
                vidView.resume();
            }
        }

    }

    private void playVideo(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String url = preferences.getString("stream_url","https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        Uri uri = Uri.parse(url);
        vidView.setVideoURI(uri);
        sourceUrl = url;
        vidView.start();
        Toast.makeText(getActivity(), "Streaming from " + url, Toast.LENGTH_LONG).show();
    }

    private boolean urlChanged(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String url = preferences.getString("stream_url","https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        return !sourceUrl.equals(url);
    }

    @Override
    public void onPause() {
        super.onPause();
        vidView.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        vidView.stopPlayback();
    }


}
