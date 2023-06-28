package com.brick.yggdrasilserver.common;

import java.util.HashMap;

/**
 * 操作消息提醒
 */
public class AjaxResult extends HashMap<String, Object> {

    public static final String CODE_TAG = "code";
    public static final String MSG_TAG = "msg";
    public static final String DATA_TAG = "data";

    public AjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        super.put(DATA_TAG, data);
    }

    public static AjaxResult success() {
        return new AjaxResult(1, "操作成功");
    }

    public static AjaxResult success(String msg) {
        return new AjaxResult(1, msg);
    }

    public static AjaxResult success(Object data) {
        return new AjaxResult(1, "操作成功", data);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(1, msg, data);
    }

    public static AjaxResult error() {
        return new AjaxResult(-1, "操作失败");
    }

    public static AjaxResult error(String msg) {
        return new AjaxResult(-1, msg);
    }
}