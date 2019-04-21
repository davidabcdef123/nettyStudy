package com.david.servlet;

import com.alibaba.fastjson.JSON;
import com.david.http.CustomRequest;
import com.david.http.CustomResponse;
import com.david.http.CustomServlet;

/**
 * @author: sun chao
 * Create at: 2019-04-21
 */
public class SecondServlet extends CustomServlet {

    @Override
    public void doGet(CustomRequest request, CustomResponse response) {
        doPost(request, response);
    }

    @Override
    public void doPost(CustomRequest request, CustomResponse response) {
        String str = JSON.toJSONString(request.getParameters(), true);
        response.write(str, 200);
    }
}
