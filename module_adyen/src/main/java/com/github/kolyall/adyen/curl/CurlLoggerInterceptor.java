package com.github.kolyall.adyen.curl;

import com.github.kolyall.java.utils.PlatformLog;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


/**
 * Created by mohamedzakaria on 2/4/16.
 */
public class CurlLoggerInterceptor implements Interceptor {
    private final Charset UTF8 = Charset.forName("UTF-8");
    private String tag = null;
    private CurlPrinter mCurlPrinter;

    public CurlLoggerInterceptor(PlatformLog platformLog) {
        mCurlPrinter = new CurlPrinter(platformLog::d);
    }

    /**
     * Set logcat tag for curl lib to make it ease to filter curl logs only.
     *
     * @param tag
     */
    public CurlLoggerInterceptor(String tag, PlatformLog platformLog) {
        this.tag = tag;
        mCurlPrinter = new CurlPrinter(platformLog::d);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        printRequest(request);
        return chain.proceed(request);
    }

    public String printRequest(Request request) throws IOException {
        StringBuilder builder = new StringBuilder("");
        // add cURL command
        builder.append("curl");
        builder.append(" -X");
        // add method
        builder.append(" " + request.method().toUpperCase());

        // add request URL
        builder.append(String.format(" \"%s\"", request.url().toString()));

        // adding headers
        builder.append(" ");
        for (String headerName : request.headers().names()) {
            builder.append(String.format("-H \"%s: %s\" ", headerName, request.headers().get(headerName)));
        }

        // adding request body
        RequestBody requestBody = request.body();
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                builder.append(String.format("-H \"%s: %s\" ", "Content-Type", request.body().contentType().toString()));
                charset = contentType.charset(UTF8);
                builder.append(String.format(" -d '%s'", buffer.readString(charset)));
            }
        }

        builder.append(" -L");

        String curl = builder.toString();
        mCurlPrinter.print(tag, request.url().toString(), curl);
        return curl;
    }

}
