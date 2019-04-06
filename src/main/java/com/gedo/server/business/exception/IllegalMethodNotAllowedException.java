package com.gedo.server.business.exception;

/**
 * Created by Gedo on 2019/4/2.
 */
public class IllegalMethodNotAllowedException extends Exception {
    public IllegalMethodNotAllowedException() {
        super("METHOD NOT ALLOWED");
    }
}
