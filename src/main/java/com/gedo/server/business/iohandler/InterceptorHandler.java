package com.gedo.server.business.iohandler;

import com.gedo.server.business.http.NettyHttpResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;

/**
 * Created by Gedo on 2019/4/2.
 */
@ChannelHandler.Sharable
@Component
public class InterceptorHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        if (isPassed((FullHttpRequest) msg)) {
            context.fireChannelRead(msg);
            return;
        }

        ReferenceCountUtil.release(msg);
        context.writeAndFlush(NettyHttpResponse.set(HttpResponseStatus.UNAUTHORIZED)).addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isPassed(FullHttpRequest request) {
        return true;
    }
}
