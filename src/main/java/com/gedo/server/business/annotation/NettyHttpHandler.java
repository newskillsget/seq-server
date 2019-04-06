
package com.gedo.server.business.annotation;

import java.lang.annotation.*;

/**
 * Created by Gedo on 2019/4/1.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyHttpHandler {

    String path() default "";

    String method() default "GET";

    //path和请求路径不需要完全匹配则false
    boolean equal() default true;
}
