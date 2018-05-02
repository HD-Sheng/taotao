package com.sheng.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sheng.annotation.MyController;
import com.sheng.annotation.MyRequestMapping;
import com.sheng.annotation.MyRequestParam;

@MyController
@MyRequestMapping(value = "/test")
public class TestController {
    
    @MyRequestMapping("/dotest")
    public void doTest(HttpServletRequest request, HttpServletResponse response,
            @MyRequestParam(value="param")String param) {
        System.out.println(param);
        try {
            response.getWriter().write("method doTest success,param = " + param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
