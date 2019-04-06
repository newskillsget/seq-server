package com.gedo.server.business.handler;
import com.gedo.server.domain.Response;
import com.gedo.server.business.annotation.NettyHttpHandler;
import com.gedo.server.business.http.NettyHttpRequest;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Gedo on 2019/4/3.
 */
@NettyHttpHandler(path = "/request/body",method = "POST")
public class RequestBodyHandler implements IFunctionHandler<String> {
    @Override
    public Response<String> handle(NettyHttpRequest request) {
        Gson gs = new Gson();
        String json = request.contentText();
        gs.fromJson(json, Map.class);
        return Response.ok(json);
    }
}
