package com.cvte.util.http;

import android.os.Handler;
import android.os.Looper;

import com.cvte.util.http.request.Request;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Rodson
 * @description A http request sender.
 * @date 1/21/15
 * @since 1.0
 */
public class HttpSender {
    /** Network interface for performing request. */
    private final Network mNetwork;

    /** The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue =
            new PriorityBlockingQueue<Request<?>>();

    /** The network dispatch */
    private NetworkDispatcher mNetworkDispatcher;

    private static HttpSender sInstance;

    public static HttpSender getInstance() {
        if (sInstance == null) {
            sInstance = new HttpSender();
            sInstance.start();
        }
        return sInstance;
    }

    private HttpSender() {
        HttpStack stack = new HttpStack();
        mNetwork = new BasicNetwork(stack);
    }

    /**
     * Start the thread used to dispatch network request.
     */
    public void start() {
        if (mNetworkDispatcher == null) {
            mNetworkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork,
                    new ExecutorDelivery(new Handler(Looper.getMainLooper())));
            mNetworkDispatcher.start();
        }
    }

    /**
     * Stop the thread used to dipatch network request.
     */
    public void stop() {
        if (mNetworkDispatcher != null) {
            mNetworkDispatcher.quit();
            mNetworkDispatcher = null;
        }
    }

    public void send(Request<?> request) {
        mNetworkQueue.add(request);
    }
}
