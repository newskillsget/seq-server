package com.gedo.server.business.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * Created by Gedo on 2019/4/1.
 */
public class NettyHttpResponse extends DefaultFullHttpResponse {

    private static final PooledByteBufAllocator BYTE_BUF_ALLOCATOR = new PooledByteBufAllocator(false);

    private String content;

    private NettyHttpResponse(HttpResponseStatus status, ByteBuf buffer) {
        super(HttpVersion.HTTP_1_1, status, buffer);
        headers().set(CONTENT_TYPE, "application/json");
        headers().setInt(CONTENT_LENGTH, content().readableBytes());
        headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, RCS-ACCESS-TOKEN");
        headers().set(ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
    }

    public static FullHttpResponse set(HttpResponseStatus status) {
        if (HttpResponseStatus.UNAUTHORIZED == status) {
            return NettyHttpResponse.set(HttpResponseStatus.UNAUTHORIZED, "");
        }
        if (HttpResponseStatus.NOT_FOUND == status) {
            return NettyHttpResponse.set(HttpResponseStatus.NOT_FOUND, "");
        }
        if (HttpResponseStatus.METHOD_NOT_ALLOWED == status) {
            return NettyHttpResponse.set(HttpResponseStatus.METHOD_NOT_ALLOWED, "");
        }
        return NettyHttpResponse.set(HttpResponseStatus.OK, "");
    }

    public static FullHttpResponse makeError(Exception exception) {
        String message = exception.getClass().getName() + ":" + exception.getMessage();
        return NettyHttpResponse.set(HttpResponseStatus.INTERNAL_SERVER_ERROR, String.format("", message));
    }

    public static FullHttpResponse ok(String content) {
        return set(HttpResponseStatus.OK, content);
    }

    private static FullHttpResponse set(HttpResponseStatus status, String content) {
        byte[] body = content.getBytes();
        ByteBuf buffer = BYTE_BUF_ALLOCATOR.buffer(body.length);
        buffer.writeBytes(body);
        NettyHttpResponse response = new NettyHttpResponse(status, buffer);
        response.content = content;
        return response;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(protocolVersion().toString()).append(" ").append(status().toString()).append("\n");
        builder.append(CONTENT_TYPE).append(": ").append(headers().get(CONTENT_TYPE)).append("\n");
        builder.append(CONTENT_LENGTH).append(": ").append(headers().get(CONTENT_LENGTH)).append("\n");
        builder.append("content-body").append(": ").append(content).append("\n");
        return builder.toString();
    }
}
