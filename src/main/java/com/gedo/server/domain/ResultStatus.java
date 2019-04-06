package com.gedo.server.domain;

/**
 * Created by Gedo on 2019/4/2.
 */
public enum ResultStatus {


    OK(200, "ok"),


    FAIL(500, "FAIL"),


    FORMAT_ERROR(1002, "");

    private final int value;
    private final String message;

    ResultStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return this.value;
    }

    /**
     * Return the reason phrase of this status code.
     */
    public String message() {
        return message;
    }
}
