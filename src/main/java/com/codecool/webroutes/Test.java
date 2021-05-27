package com.codecool.webroutes;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    public static Map<String, Method> RoutesAndMethods = new HashMap<>();

    public static void main(String[] args) throws Exception {
        startUp();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/test2", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static void startUp() {
        try {
            for (Method m : Class.forName("com.codecool.webroutes.Routes").getMethods()) {
                if (m.isAnnotationPresent(WebRoute.class)) {
                    try {
                        RoutesAndMethods.put(m.getAnnotation(WebRoute.class).value(), m);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getResponseForRoute(String route) {
        String response = null;
        try {
            for (Map.Entry<String, Method> pair : RoutesAndMethods.entrySet()) {
                if (pair.getKey().equals(route)) {
                    response = (String) pair.getValue().invoke(null);
                } else if (pair.getKey().equals("/test/<id>")) {
                    String[] test = route.split("/", 3);
                    response = (String) pair.getValue().invoke(null, test[test.length - 1]);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = getResponseForRoute(t.getRequestURI().toString());
            ;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}