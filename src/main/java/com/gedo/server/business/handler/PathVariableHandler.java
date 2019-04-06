package com.gedo.server.business.handler;

import com.gedo.server.domain.Response;
import com.gedo.server.business.annotation.NettyHttpHandler;
import com.gedo.server.business.http.NettyHttpRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gedo on 2019/4/3.
 */
@NettyHttpHandler(path = "/path/param/",method = "POST",equal = false)
public class PathVariableHandler implements IFunctionHandler<List<HashMap<String,String>>> {
    @Override
    public Response<List<HashMap<String,String>>> handle(NettyHttpRequest request) {

        String id = request.getStringPathValue(3);
        request.contentText();
        List<HashMap<String,String>> list = new LinkedList<>();
        HashMap<String,String> data = new HashMap<>();
        data.put("id","0");
        data.put("test","test");
        list.add(data);
        return Response.ok(list);
    }
}
