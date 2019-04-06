package com.gedo.server.business.handler;

import com.gedo.server.business.service.SeqService;
import com.gedo.server.domain.Response;
import com.gedo.server.business.annotation.NettyHttpHandler;
import com.gedo.server.business.http.NettyHttpRequest;
import com.gedo.server.domain.SeqReq;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Gedo on 2019/4/3.
 */
@NettyHttpHandler(path = "/test")
public class TestHandler implements IFunctionHandler<String> {
    @Autowired
    SeqService seqService;

    @Override
    public Response<String> handle(NettyHttpRequest request) {
        SeqReq seqReq = new SeqReq();
        seqReq.setAppId("1234");
        Long nextNum = seqService.getNextNum(seqReq);
        return Response.ok(nextNum.toString());
    }
}
