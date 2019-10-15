package com.github.kolyall.adyen.curl;

public interface CurlConsumer {
    void consume(String tag, String msg);
}
