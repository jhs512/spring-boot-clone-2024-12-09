package com.ll;

import com.ll.framework.ioc.ApplicationContext;
import com.ll.framework.web.HttpServer;


public class App {
    public static ApplicationContext CONTEXT;

    static {
        ApplicationContext context = new ApplicationContext("com.ll");
        context.init();
        CONTEXT = context;
    }

    public static void run() {
        HttpServer httpServer = CONTEXT.genBean("httpServer");
        httpServer.start(8080);
    }
}
