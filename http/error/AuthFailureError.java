/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */
package com.cvte.util.http.error;

import android.content.Intent;

import com.cvte.util.http.NetworkResponse;

/**
 * @author Rodson
 * @description Error indicating that there was an authentication failure when performing a request.
 * @date 1/21/15
 * @since 1.0
 */
public class AuthFailureError extends ResponseError {
    /** An intent that can be used to resolve this exception. (Brings up the password dialog */
    private Intent mResolutionIntent;

    public AuthFailureError() {
    }

    public AuthFailureError(Intent intent) {
        mResolutionIntent = intent;
    }

    public AuthFailureError(NetworkResponse response) {
        super(response);
    }

    public AuthFailureError(String message) {
        super(message);
    }

    public AuthFailureError(String message, Exception reason) {
        super(message, reason);
    }

    public Intent getResolutionIntent() {
        return mResolutionIntent;
    }

    @Override
    public String getMessage() {
        if (mResolutionIntent != null) {
            return "User needs to (re)enter credentials.";
        }
        return super.getMessage();
    }
}

