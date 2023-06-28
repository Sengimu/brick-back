package com.brick.yggdrasilserver.common;

import java.util.HashMap;

public class ErrorResponse extends HashMap<String, Object> {

    public static final String ERROR_TAG = "error";
    public static final String ERROR_MESSAGE_TAG = "errorMessage";
    public static final String CAUSE_TAG = "cause";

    public ErrorResponse(String error, String errorMessage) {
        super.put(ERROR_TAG, error);
        super.put(ERROR_MESSAGE_TAG, errorMessage);
    }

    public ErrorResponse(String error, String errorMessage, String cause) {
        super.put(ERROR_TAG, error);
        super.put(ERROR_MESSAGE_TAG, errorMessage);
        super.put(CAUSE_TAG, cause);
    }


}
