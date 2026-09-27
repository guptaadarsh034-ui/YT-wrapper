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

        if (webView != null) {
            WebSettings settings = webView.getSettings();

            // Web functionality
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);

            // RAM optimizations for 512MB RAM
            settings.setGeolocationEnabled(false);
            settings.setSaveFormData(false);
            settings.setDatabaseEnabled(false);
            settings.setSupportMultipleWindows(false);
            settings.setLoadsImagesAutomatically(true);

            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("https://m.youtube.com");
        }
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
