package com.cvte.util.http;

import com.cvte.util.http.error.ResponseError;
import com.cvte.util.http.request.Request;

import android.os.Process;
import android.os.SystemClock;

import java.util.concurrent.BlockingQueue;

/**
 * @author Rodson
 * @description Provides a thread for performing network dispatch from a queue of request.
 * @date 1/23/15
 * @since 1.0
 */
public class NetworkDispatcher extends Thread {

    /** The queue of requests to service. */
    private final BlockingQueue<Request<?>> mQueue;

    /** The network interface for processing requests. */
    private final Network mNetwork;

    /** For posting responses and errors */
    private final ResponseDelivery mDelivery;

    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread. You must call {@link #start()}
     * in order to begin processing.
     * @param queue Queue of incoming requests for triage
     * @param network Network interface to use for performing requests
     * @param delivery Delivery interface to use for posting responses
     */
    public NetworkDispatcher(BlockingQueue<Request<?>> queue,
                             Network network, ResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mDelivery = delivery;
    }

    /**
     * Forces the dispatcher to quit immediately. If any requests are still in
     * the queue, they are not guranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while(true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            Request<?> request;
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it wat time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                Response<?> response = request.parseNetworkResponse(networkResponse);
                mDelivery.postResponse(request, response);
            } catch (ResponseError responseError) {
                responseError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                parseAndDeliverNetworkError(request, responseError);
            } catch (Exception e) {
                ResponseError responseError = new ResponseError(e);
                responseError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                mDelivery.postError(request, responseError);
            }
        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, ResponseError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
