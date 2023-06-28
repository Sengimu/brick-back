package com.brick.yggdrasilserver.common;

import com.alibaba.fastjson2.JSON;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class R extends ResponseEntity<String> {

    public R(Object o) {
        super(JSON.toJSONString(o), HttpStatusCode.valueOf(200));
    }

    public R(int code, Object o) {
        super(JSON.toJSONString(o), HttpStatusCode.valueOf(code));
    }

    public R(int code, String error, String errorMessage) {
        super(JSON.toJSONString(new ErrorResponse(error, errorMessage)), HttpStatusCode.valueOf(code));
    }

    public R(int code, String error, String errorMessage, String cause) {
        super(JSON.toJSONString(new ErrorResponse(error, errorMessage, cause)), HttpStatusCode.valueOf(code));
    }
}
