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

public class MainActivity extends Activity {

    private WebView webView;
    private LocalAnonymousServer localServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Try launching local server safely
        try {
            localServer = new LocalAnonymousServer(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView = findViewById(R.id.webview);
        
        if (webView != null) {
            setupNavigationButtons();
            configureUltraLiteSettings();
            
            // Fallback load directly if local server failed
            if (localServer != null) {
                webView.loadUrl("http://127.0.0.1:8080");
            } else {
                webView.loadUrl("https://m.youtube.com");
            }
        }
    }

    private void setupNavigationButtons() {
        Button btnBack = findViewById(R.id.btn_back);
        Button btnHome = findViewById(R.id.btn_home);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView != null && webView.canGoBack()) {
                        webView.goBack();
                    }
                }
            });
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView != null) {
                        if (localServer != null) {
                            webView.loadUrl("http://127.0.0.1:8080");
                        } else {
                            webView.loadUrl("https://m.youtube.com");
                        }
                    }
                }
            });
        }

        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView != null) {
                        webView.reload();
                    }
                }
            });
        }
    }

    private void configureUltraLiteSettings() {
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDatabaseEnabled(false);
        settings.setGeolocationEnabled(false);
        settings.setSaveFormData(false);

        // Hardware acceleration for KitKat video performance
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Cookie handling for KitKat vs Modern Android
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("accounts.google.com") || url.contains("facebook.com")) {
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String hideAdsScript = "javascript:(function() { " +
                        "var ads = document.querySelectorAll('ad-slot, .ytp-ad-overlay-container, #masthead-ad, .ytp-ad-message-container, ytd-promoted-sparkles-web-renderer');" +
                        "for(var i=0; i<ads.length; i++) { if(ads[i]) ads[i].parentNode.removeChild(ads[i]); }" +
                        "})()";
                view.loadUrl(hideAdsScript);

                String forceLowResScript = "javascript:(function() { " +
                        "var player = document.querySelector('video');" +
                        "if(player) {" +
                        "  player.addEventListener('play', function() {" +
                        "    try {" +
                        "      var ytPlayer = document.getElementById('movie_player');" +
                        "      if(ytPlayer && ytPlayer.setPlaybackQualityRange) {" +
                        "        ytPlayer.setPlaybackQualityRange('small', 'medium');" +
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
        if (localServer != null) {
            try {
                localServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
        }

        super.onDestroy();
    }
}
