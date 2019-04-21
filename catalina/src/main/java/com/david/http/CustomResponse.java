package com.david.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: sun chao
 * Create at: 2019-04-21
 */
public class CustomResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest request;
    private static Map<Integer, HttpResponseStatus> statusMapping = new HashMap<>();

    static {
        statusMapping.put(200, HttpResponseStatus.OK);
        statusMapping.put(404, HttpResponseStatus.OK);
    }

    public CustomResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String outString, Integer status) {
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    statusMapping.get(status),
                    Unpooled.wrappedBuffer(outString.getBytes("UTF-8"))
            );
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/json");
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaders.Names.EXPIRES, 0);
            if (HttpHeaders.isKeepAlive(request)) {
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            ctx.flush();
        }
    }
}
