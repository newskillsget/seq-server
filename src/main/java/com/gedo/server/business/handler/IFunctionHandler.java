
package com.gedo.server.business.handler;

/**
 * Created by Gedo on 2019/4/3.
 */
import com.gedo.server.domain.Response;
import com.gedo.server.business.http.NettyHttpRequest;

public interface IFunctionHandler<T> {
    Response<T> handle(NettyHttpRequest request);
}
