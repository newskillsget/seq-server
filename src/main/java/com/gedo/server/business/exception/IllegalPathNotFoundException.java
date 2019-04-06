
package com.gedo.server.business.exception;

/**
 * Created by Gedo on 2019/4/2.
 */
public class IllegalPathNotFoundException extends Exception {
    public IllegalPathNotFoundException() {
        super("PATH NOT FOUND");
    }
}
