package org.odk.collect.android.activities;

public class userlog {
    public static String username = "";
    public static String token = "";
    public static String[] params;
    public static  String opcion;

    public static String getOpcion() {
        return opcion;
    }

    public static void setOpcion(String opcion) {
        userlog.opcion = opcion;
    }

    public static String getUsername() {
        return username;
    }

    public static String[] getParams() {
        return params;
    }

    public static void setParams(String[] params) {
        userlog.params = params;
    }

    public static void setUsername(String username) {
        userlog.username = username;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        userlog.token = token;
    }
}
