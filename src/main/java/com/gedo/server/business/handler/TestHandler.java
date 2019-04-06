package com.gedo.server.business.handler;

import com.gedo.server.domain.Response;
import com.gedo.server.business.annotation.NettyHttpHandler;
import com.gedo.server.business.http.NettyHttpRequest;
/**
 * Created by Gedo on 2019/4/3.
 */
@NettyHttpHandler(path = "/test")
public class TestHandler implements IFunctionHandler<String> {

    @Override
    public Response<String> handle(NettyHttpRequest request) {

         return Response.ok("business test");
    }
}
