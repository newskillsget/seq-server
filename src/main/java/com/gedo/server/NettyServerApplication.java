package com.gedo.server;

import com.gedo.server.business.annotation.NettyHttpHandler;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Gedo on 2019/4/1.
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@ComponentScan(includeFilters = @ComponentScan.Filter(NettyHttpHandler.class))
public class NettyServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(NettyServerApplication.class).web(WebApplicationType.NONE).run(args);
    }

}
