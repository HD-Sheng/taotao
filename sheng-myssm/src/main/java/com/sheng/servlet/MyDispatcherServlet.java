package com.sheng.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sheng.annotation.MyController;
import com.sheng.annotation.MyRequestMapping;

public class MyDispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = -7440074974968057031L;

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<String>();

    private Map<String, Object> ioc = new HashMap<String, Object>();

    private Map<String, Method> handlerMapping = new HashMap<String, Method>();

    private Map<String, Object> controllerMap = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        // 2.初始化所有相关联的类，扫描用户设定的包下面所有的类
        doScanner(properties.getProperty("scanPackage"));

        // 3.拿到扫描到的类，通过反射机制，实例化，并且放到ioc容器中（k-v, beanName-bean） beanName默认首字母小写
        doInstance();

        // 4.初始化HandlerMapping(将url和method对应上)
        initHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        try {
            // 处理请求
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500-Server Exception!");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (handlerMapping.isEmpty()) {
            return;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 NOT FOUND");
            return;
        }

        Method method = this.handlerMapping.get(url);

        // 获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        // 保存参数值
        Object[] paramValues = new Object[parameterTypes.length];

        // 方法的参数列表
        for (int i = 0; i < parameterTypes.length; i++) {
            // 根据参数名称,做某些处理
            String requestParam = parameterTypes[i].getSimpleName();
            if ("HttpServletRequest".equals(requestParam)) {
                paramValues[i] = req;
                continue;
            }

            if ("HttpServletResponse".equals(requestParam)) {
                paramValues[i] = resp;
                continue;
            }

            if ("String".equals(requestParam)) {
                for (Entry<String, String[]> param : parameterMap.entrySet()) {
                    System.out.println("param==============" + Arrays.toString(param.getValue()));
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }

        // 利用反射机制来调用
        try {
            method.invoke(this.controllerMap.get(url), paramValues);// 第一个参数是method对应的实例，在ioc容器中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLoadConfig(String initParameter) {
        // 把web.xml中的contextConfigLocation对应的value值的文件加载到流中
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(initParameter);
        try {
            // 用Properties文件加载文件里面的内容
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void doScanner(String packageName) {
        // 把所有的 . 替换成/
        ClassLoader classLoader = this.getClass().getClassLoader();
        String resource = "/" + packageName.replaceAll("\\.", "/");
        URL url = classLoader.getResource(resource);
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }

    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        try {
            for (Entry<String, Object> entry : ioc.entrySet()) {
                Class<? extends Object> clazz = entry.getValue().getClass();
                if (!clazz.isAnnotationPresent(MyController.class)) {
                    continue;
                }
                // 拼url时，是controller头上的url拼上方法上的url
                String baseUrl = "";
                if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                    baseUrl = annotation.value();
                }

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                        continue;
                    }
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                    String url = annotation.value();

                    url = (baseUrl + "/" + url).replaceAll("/+", "/");
                    handlerMapping.put(url, method);
                    controllerMap.put(url, clazz.newInstance());
                    System.out.println(url + "," + method);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> cla = Class.forName(className);
                if (cla.isAnnotationPresent(MyController.class)) {
                    ioc.put(toLowerFirstWord(cla.getSimpleName()), cla.newInstance());
                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private String toLowerFirstWord(String simpleName) {
        char[] charArray = simpleName.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }
}
