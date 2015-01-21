/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */
package com.cvte.util.http;

/**
 * @author Rodson
 * @description Base class for all network request.
 * @date 1/21/15
 * @since 1.0
 */
public class Request<T> implements Comparable<Request<T>> {
    private static final int DEFAULT_TIMEOUT_MS = 2500;

    /**
     * Default encoding for POST or PUT parameters.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Request method of this request. Currently supports GET, POST.
     */
    private final int mMethod;

    /**
     * URL of this request.
     */
    private final String mUrl;

    public Request(int method, String url) {
        mMethod = method;
        mUrl = url;
    }

    /**
     * Returns the URL of this request.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the socket timeout in milliseconds.
     */
    public final int getTimeoutMs() {
        return DEFAULT_TIMEOUT_MS;
    }

    @Override
    public int compareTo(Request<T> another) {
        return 0;
    }
}
