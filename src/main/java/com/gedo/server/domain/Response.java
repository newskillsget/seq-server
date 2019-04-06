package com.gedo.server.domain;

import com.google.gson.GsonBuilder;

/**
 * Created by Gedo on 2019/4/2.
 */
public final class Response<T> {
    private int code;
    private String message;
    private T data;


    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public String toJSONString(){
        return new GsonBuilder().create().toJson(this);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(ResultStatus.OK.value(), ResultStatus.OK.message(), data);
    }
    public static <T> Response<T> ok(String msg, T data) {
        return new Response<>(ResultStatus.OK.value(), msg, data);
    }

    public static  Response<Void> ok() {
        return ok(null);
    }

    public static Response fail(String msg) {
        return new Response<String>(ResultStatus.FAIL.value(), msg, null);
    }
}
