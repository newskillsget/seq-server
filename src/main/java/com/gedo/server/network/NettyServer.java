package com.gedo.server.network;

import com.gedo.server.business.iohandler.InterceptorHandler;
import com.gedo.server.business.iohandler.FilterLogginglHandler;
import com.gedo.server.business.iohandler.HttpServerHandler;
import com.gedo.server.utils.Base64Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
/**
 * Created by Gedo on 2019/4/1.
 */
@Configuration
public class NettyServer implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Value("${server.port}")
    private int port;

    @Resource
    private InterceptorHandler interceptorHandler;

    @Resource
    private HttpServerHandler httpServerHandler;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(NioChannelOption.TCP_NODELAY, true)
                .childOption(NioChannelOption.SO_REUSEADDR, true)
                .childOption(NioChannelOption.SO_KEEPALIVE, false)
                .childOption(NioChannelOption.SO_RCVBUF, 2048)
                .childOption(NioChannelOption.SO_SNDBUF, 2048)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("codec", new HttpServerCodec());
                        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                        ch.pipeline().addLast("logging", new FilterLogginglHandler());
                        ch.pipeline().addLast("interceptor", interceptorHandler);
                        ch.pipeline().addLast("Handler", httpServerHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind(port).syncUninterruptibly().addListener(future -> {
            String logBanner = Base64Util.getLogo();
            LOGGER.info("\n{}", logBanner);
        });
        channelFuture.channel().closeFuture().addListener(future -> {
            LOGGER.info("Netty Http Server Start Shutdown ............");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });
    }

}