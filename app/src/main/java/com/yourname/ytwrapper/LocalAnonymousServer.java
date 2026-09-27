package com.yourname.ytwrapper;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.UUID;

public class LocalAnonymousServer extends NanoHTTPD {

    public LocalAnonymousServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String guestToken = "VISITOR_" + UUID.randomUUID().toString().substring(0, 8);

        String html = "<html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<script>" +
                "  document.cookie = 'VISITOR_INFO1_LIVE=" + guestToken + "; path=/; domain=.youtube.com';" +
                "  document.cookie = 'PREF=f6=40000&f5=30; path=/; domain=.youtube.com';" +
                "  window.location.href = 'https://m.youtube.com/?app=m&persist_app=1';" +
                "</script></head>" +
                "<body style='background:#000;color:#fff;text-align:center;padding-top:20%;font-family:sans-serif;'>" +
                "<h3>Loading UltraLite YouTube...</h3></body></html>";

        return newFixedLengthResponse(Response.Status.OK, "text/html", html);
    }
}
