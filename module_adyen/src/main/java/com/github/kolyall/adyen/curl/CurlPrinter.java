package com.github.kolyall.adyen.curl;


import androidx.annotation.Nullable;

/**
 * Created by mohamedzakaria on 6/7/16.
 */
public class CurlPrinter {
    /**
     * Drawing toolbox
     */
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static String sTag = "CURL";
    private final CurlConsumer consumer;

    public CurlPrinter(CurlConsumer consumer) {
        this.consumer = consumer;
    }

    public void print(@Nullable String tag, String url, String msg) {
        // setting tag if not null
        if (tag != null)
            sTag = tag;

        StringBuilder logMsg = new StringBuilder("\n");
        logMsg.append("\n");
        logMsg.append("URL: " + url);
        log(logMsg.toString());
        logMsg = new StringBuilder("\n");
        logMsg.append(sTag);
        logMsg.append("\n");
        logMsg.append(SINGLE_DIVIDER);
        logMsg.append("\n");
        logMsg.append(msg);
        logMsg.append(" ");
        logMsg.append(" \n");
        logMsg.append(SINGLE_DIVIDER);
        logMsg.append(" \n ");
        log(logMsg.toString());
    }

    private void log(String msg) {
        consumer.consume(sTag, msg);
    }
}
