package com.example.cisco.sourcecode;

import com.cisco.jsapi.JSApi;
import com.cisco.jsapi.JSApiError;

/**
 * Created by cisco on 16/9/1.
 */
public class JSApiNative {

    @JSApi
    public static void show(String message) {
        System.out.println("show"+message);
    }

    @JSApi
    public static void toast(String message) {
        System.out.println("toast"+message);
    }

    @JSApiError
    public static void invalidApi(String message) {
        System.out.println("invalidApi"+message);
    }

}
