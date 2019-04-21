package com.david.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @author: sun chao
 * Create at: 2019-04-21
 */
public class CustomRequest {
    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public CustomRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
        return decoderQuery.parameters();
    }

    public String getParameter(String name) {
        Map<String, List<String>> params = getParameters();
        List<String> param = params.get(name);
        if (null != param) {
            return param.get(0);
        } else {
            return null;
        }
    }


    public String getUri() {
        return request.getUri();
    }

    public String getMethod() {
        return request.getMethod().name();
    }


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }
}
