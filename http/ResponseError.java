/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */
package com.cvte.util.http;

/**
 * @author Rodson
 * @description Exception style class encapsulating http response errors
 * @date 1/21/15
 * @since 1.0
 */
public class ResponseError extends Exception {
    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public ResponseError() {
        networkResponse = null;
    }

    public ResponseError(NetworkResponse response) {
        networkResponse = response;
    }

    public ResponseError(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public ResponseError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public ResponseError(Throwable cause) {
        super(cause);
        networkResponse = null;
    }

    public void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }

    public long getNetworkTimeMs() {
        return networkTimeMs;
    }
}
