package com.cvte.util.http;

import com.cvte.util.http.error.ResponseError;

/**
 * @author Rodson
 * @description An interface for performing request.
 * @date 1/21/15
 * @since 1.0
 */
public interface Network {
    /**
     * Perform the specified request.
     * @param request Request to process
     * @return A {@link NetworkResponse} with data.
     * @throws com.cvte.util.http.error.ResponseError on errors
     */
    public NetworkResponse performRequest(Request<?> request) throws ResponseError;
}
