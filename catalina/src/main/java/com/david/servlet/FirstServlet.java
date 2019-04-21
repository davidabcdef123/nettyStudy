package com.david.servlet;

import com.david.http.CustomRequest;
import com.david.http.CustomResponse;
import com.david.http.CustomServlet;

/**
 * @author: sun chao
 * Create at: 2019-04-21
 */
public class FirstServlet extends CustomServlet {

    @Override
    public void doGet(CustomRequest request, CustomResponse response) {
        doPost(request, response);
    }

    @Override
    public void doPost(CustomRequest request, CustomResponse response) {
        String param = "name";
        String str = request.getParameter(param);
        response.write(param + ":" + str, 200);
    }
}
