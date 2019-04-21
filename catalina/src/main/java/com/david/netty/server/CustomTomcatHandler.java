package com.david.netty.server;

import com.david.http.CustomRequest;
import com.david.http.CustomResponse;
import com.david.http.CustomServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.log4j.Logger;

import javax.config.CustomConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: sun chao
 * Create at: 2019-04-21
 */
public class CustomTomcatHandler extends ChannelInboundHandlerAdapter {

    private Logger LOG = Logger.getLogger(CustomTomcatHandler.class);

    private static final Map<Pattern, Class<?>> servletMapping = new HashMap<Pattern, Class<?>>();

    static {
        CustomConfig.load("web.properties");
        for (String key : CustomConfig.getKeys()) {
            if (key.startsWith("servlet")) {
                String name = key.replaceFirst("servlet.", "");
                //过滤掉命名的key
                if (name.indexOf(".") != -1) {
                    name = name.substring(0, name.indexOf("."));
                } else {
                    continue;
                }
                String pattern = CustomConfig.getValue("servlet." + name + ".urlPattern");
                pattern = pattern.replaceAll("\\*", ".*");
                String className = CustomConfig.getValue("servlet." + name + ".className");
                if (!servletMapping.containsKey(pattern)) {
                    try {
                        servletMapping.put(Pattern.compile(pattern), Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest r = (HttpRequest) msg;
            CustomRequest request = new CustomRequest(ctx, r);
            CustomResponse response = new CustomResponse(ctx, r);
            String uri = request.getUri();
            String method = request.getMethod();
            LOG.info(String.format("Uri:%s method %s", uri, method));
            boolean hasPattern = false;
            for (Map.Entry<Pattern, Class<?>> entry : servletMapping.entrySet()) {
                if (entry.getKey().matcher(uri).matches()) {
                    CustomServlet servlet = (CustomServlet) entry.getValue().newInstance();
                    if ("get".equalsIgnoreCase(method)) {
                        servlet.doGet(request, response);
                    } else {
                        servlet.doPost(request, response);
                    }
                    hasPattern = true;
                }
            }
            if (!hasPattern) {
                String out = String.format("404 NotFound URL%s for method %s", uri, method);
                response.write(out, 404);
                return;
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
