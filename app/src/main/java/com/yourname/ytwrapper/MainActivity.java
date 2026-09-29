package com.yourname.ytwrapper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;
    private LocalAnonymousServer proxyServer;

    private static final String CSS_STRIP_BLOAT = 
        "ytd-comments, #related, ytd-compact-autoplay-renderer, " +
        ".ytm-pivot-bar-renderer, yt-icon, #masthead-ad, " +
        "ytm-comments-entry-point-header-renderer { display: none !important; }";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Local Proxy
        try {
            proxyServer = new LocalAnonymousServer(8080);
            proxyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView = (WebView) findViewById(R.id.webview);

        // 2. Clear previous session state
        clearAnonymousSession();

        // 3. Configure Low-RAM WebSettings
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        
        // REMOVED: settings.setAppCacheEnabled(false); -> Method removed in API 33
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Render priority & hardware acceleration for KitKat
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        // 4. KaiOS User-Agent Spoofing
        settings.setUserAgentString("Mozilla/5.0 (Mobile; KaiOS/2.5; TV) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) KAIOS/2.5 Chrome/64.0.3282.144 Mobile Safari/537.36");

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                injectStyle(view, CSS_STRIP_BLOAT);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String resLockJs = "javascript:(function() {" +
                    "var v = document.querySelector('video');" +
                    "if(v) { v.style.width='100%'; v.style.height='100%'; }" +
                    "document.body.style.transform = 'translateZ(0)';" +
                    "})()";
                view.evaluateJavascript(resLockJs, null);
            }
        });

        webView.loadUrl("https://m.youtube.com");
    }

    private void clearAnonymousSession() {
        try {
            WebStorage.getInstance().deleteAllData();
            
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            
            if (webView != null) {
                webView.clearCache(true);
                webView.clearHistory();
                webView.clearFormData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectStyle(WebView view, String css) {
        String js = "javascript:(function() {" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = '" + css + "';" +
                "document.head.appendChild(style);" +
                "})()";
        view.loadUrl(js);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAnonymousSession();
        if (proxyServer != null) {
            proxyServer.stop();
        }
    }
}
