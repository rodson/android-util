/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */
package com.cvte.util.http.request;

import com.cvte.util.http.Cache;
import com.cvte.util.http.DefaultRetryPolicy;
import com.cvte.util.http.NetworkResponse;
import com.cvte.util.http.Response;
import com.cvte.util.http.RetryPolicy;
import com.cvte.util.http.error.AuthFailureError;
import com.cvte.util.http.error.ResponseError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * @author Rodson
 * @description Base class for all network request.
 * @date 1/21/15
 * @since 1.0
 */
public abstract class Request<T> implements Comparable<Request<T>> {
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

    /** The retry policy for this request. */
    private RetryPolicy mRetryPolicy;

    /** Listener interface for errors. */
    private final Response.ErrorListener mErrorListener;

    /**
     * When a request can be retrieved from cache but must be refreshed from
     * the network, the cache entry will be stored here so that in the event of
     * a "Not Modified" response, we can be sure it hasn't been evicted from cache.
     */
    private Cache.Entry mCacheEntry = null;

    public Request(int method, String url, Response.ErrorListener listener) {
        mMethod = method;
        mUrl = url;
        mErrorListener = listener;
        setRetryPolicy(new DefaultRetryPolicy());
    }

    /**
     * Sets the retry policy for this request.
     *
     * @return This Request object to allow for chaining.
     */
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
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
     * Annotates this request with an entry retrieved for it from cache.
     * Used for cache coherency support.
     *
     * @return This Request object to allow for chaining.
     */
    public Request<?> setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    /**
     * Returns the annotated cache entry, or null if there isn't one.
     */
    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link com.cvte.util.http.error.AuthFailureError} as authentication may be requied to provide these values.
     *
     * @throws com.cvte.util.http.error.AuthFailureError In the event of auth failures.
     */
    public Map<String, String> getHeader() throws AuthFailureError {
        return Collections.emptyMap();
    }

    /**
     * Returns the retry policy that should be used  for this request.
     */
    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
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
        return mRetryPolicy.getCurrentTimeout();
    }

    /**
     * Subclasses must implement this to parse the raw network response
     * and return an appropriate response type. This method will be
     * called from a worker thread.  The response will not be delivered
     * if you return null.
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    abstract public Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * Subclasses can override this method to parse 'networkError' and return a more specific error.
     *
     * <p>The default implementation just returns the passed 'networkError'.</p>
     *
     * @param responseError the error retrieved from the network
     * @return an NetworkError augmented with additional information
     */
    public ResponseError parseNetworkError(ResponseError responseError) {
        return responseError;
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed
     * response to their listeners.  The given response is guaranteed to
     * be non-null; responses that fail to parse are not delivered.
     * @param response The parsed response returned by
     * {@link #parseNetworkResponse(NetworkResponse)}
     */
    abstract public void deliverResponse(T response);

    /**
     * Delivers error message to the ErrorListener that the Request was
     * initialized with.
     *
     * @param error Error details
     */
    public void deliverError(ResponseError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }

    @Override
    public int compareTo(Request<T> another) {
        return 0;
    }
}
