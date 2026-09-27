package com.yourname.ytwrapper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import java.io.IOException;

public class MainActivity extends Activity {

    private WebView webView;
    private LocalAnonymousServer localServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Start internal anonymous session server on phone
        try {
            localServer = new LocalAnonymousServer(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }

        webView = findViewById(R.id.webview);
        setupNavigationButtons();
        configureUltraLiteSettings();

        // 2. Load through internal anonymous proxy
        webView.loadUrl("http://127.0.0.1:8080");
    }

    private void setupNavigationButtons() {
        Button btnBack = findViewById(R.id.btn_back);
        Button btnHome = findViewById(R.id.btn_home);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null) {
                    webView.loadUrl("http://127.0.0.1:8080");
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null) {
                    webView.reload();
                }
            }
        });
    }

    private void configureUltraLiteSettings() {
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(false); // Eliminates local storage bloat
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDatabaseEnabled(false);
        settings.setGeolocationEnabled(false);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        // Force software rendering layer to keep RAM strictly below ~20MB
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Block sign-in redirects completely to protect anonymity
                if (url.contains("accounts.google.com") || url.contains("facebook.com")) {
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Prompt Dalvik/ART garbage collector to free unused heap memory
                System.gc();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // A. Strip ads, promotional slots, and popup banners via CSS DOM purging
                String hideAdsScript = "javascript:(function() { " +
                        "var ads = document.querySelectorAll('ad-slot, .ytp-ad-overlay-container, #masthead-ad, .ytp-ad-message-container, ytd-promoted-sparkles-web-renderer');" +
                        "for(var i=0; i<ads.length; i++) { if(ads[i]) ads[i].parentNode.removeChild(ads[i]); }" +
                        "})()";
                view.loadUrl(hideAdsScript);

                // B. Force Video Quality Lock (Forces 240p / 360p resolution to stop buffering on slow phones)
                String forceLowResScript = "javascript:(function() { " +
                        "var player = document.querySelector('video');" +
                        "if(player) {" +
                        "  player.addEventListener('play', function() {" +
                        "    try {" +
                        "      var ytPlayer = document.getElementById('movie_player');" +
                        "      if(ytPlayer && ytPlayer.setPlaybackQualityRange) {" +
                        "        ytPlayer.setPlaybackQualityRange('small', 'medium');" + // 'small'=240p, 'medium'=360p
                        "      }" +
                        "    } catch(e) {}" +
                        "  });" +
                        "}" +
                        "})()";
                view.loadUrl(forceLowResScript);
            }
        });
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
        // Stop local proxy server
        if (localServer != null) {
            localServer.stop();
        }

        // Clean up memory and auto-wipe session cookies on exit
        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().removeAllCookies(null);
            } else {
                CookieManager.getInstance().removeAllCookie();
            }
            webView.destroy();
        }

        System.gc();
        super.onDestroy();
    }
}
