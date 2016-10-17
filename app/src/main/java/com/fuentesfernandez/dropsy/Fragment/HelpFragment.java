package com.fuentesfernandez.dropsy.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import com.fuentesfernandez.dropsy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {
    private WebView webView;

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView) view.findViewById(R.id.help_view);
        webView.loadUrl("https://dropsy-unlp.github.io/dropsy-activity-guide/");
        webView.setWebViewClient(new WebViewClient());
    }


}
