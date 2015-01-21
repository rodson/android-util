package com.cvte.util.http;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.cvte.util.http.error.ResponseError;
import com.cvte.util.http.request.Request;

/**
 * @author Rodson
 * @description A http request sender.
 * @date 1/21/15
 * @since 1.0
 */
public class HttpSender {
    /** Network interface for performing request. */
    private final Network mNetwork;

    private static HttpSender sInstance;

    public static HttpSender getInstance() {
        if (sInstance == null) {
            sInstance = new HttpSender();
        }
        return sInstance;
    }

    private HttpSender() {
        HttpStack stack = new HttpStack();
        mNetwork = new BasicNetwork(stack);
    }

    public void send(Request<?> request) {
        new AsyncHttpRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
    }

    private class AsyncHttpRequestTask extends AsyncTask<Request<?>, Integer, Response> {
        private Request<?> mRequest;
        private ResponseError mResponseError;
        private long mStartTimeMs;

        @Override
        protected void onPreExecute() {
            mStartTimeMs = SystemClock.elapsedRealtime();
        }

        @Override
        protected Response doInBackground(Request<?>... requests) {
            mRequest = requests[0];
            try {
                NetworkResponse networkResponse = mNetwork.performRequest(mRequest);
                Response<?> response = mRequest.parseNetworkResponse(networkResponse);
                return response;
            } catch (ResponseError responseError) {
                mResponseError = mRequest.parseNetworkError(responseError);
            } catch (Exception e) {
                ResponseError responseError = new ResponseError(e);
                responseError.setNetworkTimeMs(SystemClock.elapsedRealtime() - mStartTimeMs);
                mResponseError = responseError;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            Request request = mRequest;

            if (mResponseError != null) {
                request.deliverError(mResponseError);
            }

            if (response != null) {
                request.deliverResponse(response.result);
            }

        }
    }
}
