package com.tonni.notifx.Utils;

import java.util.ArrayList;

public class Error {
    private static ArrayList<String> error_array=new ArrayList<String>();

    public static String get_Error() {
        return error_array.get(0);
    }

    public static void set_Error_array(String error) {
        Error.error_array.add(0,error);
    }
}
