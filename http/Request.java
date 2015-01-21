/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */
package com.cvte.util.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

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
     * Supported request method
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

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
     * return the method for this request.
     */
    public int getMethod() {
        return mMethod;
    }

    /**
     * Returns a Map of parameter to used for a POST or PUT request. Can throw
     * {@link AuthFailureError} as authentication may be required to provide these values.
     *
     * <p>Note that you can directly override {@link #getBody()} for custom data</p>
     *
     * @throws AuthFailureError inthe event of auth failure
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    /**
     * Returns which encoding should be used when converting POST or PUT parameters returned by
     * {@link #getParams()} into a raw POST or PUT body.
     */
    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    /**
     * Returns the content type of the POST and PUT body.
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format</p>
     * @throws AuthFailureError
     */
    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts params into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
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
