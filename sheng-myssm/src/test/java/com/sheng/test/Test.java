package com.sheng.test;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) {
        Object obj = null;
        String a = String.valueOf(obj);
        out.println(a);
//        String b = String.valueOf(null);
//        System.out.println(b);
        Integer ii = 1000;
        String c = String.valueOf(ii);
        out.println(c);
        ThreadLocal<String> t = new ThreadLocal<>();
        t.remove();
        
        List<String> list = new ArrayList<>();
        
        out.print("dd");
        
        List<String> list2 = new ArrayList<>();
        
    }
}
