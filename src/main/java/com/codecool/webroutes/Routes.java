package com.codecool.webroutes;

public class Routes {

    @WebRoute("/test")
    public static String test1(){return "test";}

    @WebRoute("/test2")
    public static String test2(){return "test2";}

    @WebRoute("/test/<id>")
    public static String test3(String id){return "test/" + id;}

}
