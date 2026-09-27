package com.yourname.ytwrapper;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.UUID;

public class LocalAnonymousServer extends NanoHTTPD {

    public LocalAnonymousServer(int port) throws IOException {
        super(port);
        // Start server; NanoHTTPD handles client sockets internally
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String guestToken = "VISITOR_" + UUID.randomUUID().toString().substring(0, 10);

        String htmlContent = "<!DOCTYPE html>" +
                "<html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>" +
                "<meta http-equiv='refresh' content='1;url=https://m.youtube.com/?app=m&persist_app=1'>" +
                "<title>Anonymous Gateway</title>" +
                "<style>" +
                "  body { background-color: #0f0f0f; color: #ffffff; font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }" +
                "  .loader { text-align: center; }" +
                "  .spinner { border: 4px solid #333; border-top: 4px solid #ff0000; border-radius: 50%; width: 36px; height: 36px; animation: spin 0.8s linear infinite; margin: 0 auto 15px auto; }" +
                "  @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }" +
                "</style>" +
                "<script>" +
                "  try {" +
                "    window.localStorage.clear();" +
                "    window.sessionStorage.clear();" +
                "    document.cookie = 'VISITOR_INFO1_LIVE=" + guestToken + "; path=/; domain=.youtube.com';" +
                "    document.cookie = 'PREF=f6=40000&f5=30; path=/; domain=.youtube.com';" +
                "  } catch(e) {}" +
                "  setTimeout(function() {" +
                "    window.location.href = 'https://m.youtube.com/?app=m&persist_app=1';" +
                "  }, 300);" +
                "</script>" +
                "</head>" +
                "<body>" +
                "  <div class='loader'>" +
                "    <div class='spinner'></div>" +
                "    <p style='font-size:14px; color:#aaa;'>Starting Anonymous Session...</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        Response response = newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        return response;
    }
}
