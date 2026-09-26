package com.yourname.ytwrapper;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();

        // Enable JavaScript and basic Web features
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // Low-RAM 512MB optimizations
        settings.setGeolocationEnabled(false);
        settings.setSaveFormData(false);
        settings.setDatabaseEnabled(false);
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://m.youtube.com");
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.clearHistory();
            webView.clearCache(false);
            webView.destroy();
        }
        super.onDestroy();
    }
}
