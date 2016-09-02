package com.cisco.jsapi;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cisco on 16/9/1.
 */
public final class JSApiBridge {

    private static JSApiBridge sJSApiBridge = new JSApiBridge();

    private Method mMethod;
    private Object mProxyClassInstance;

    private JSApiBridge() {}

    /**
     * enter to work flow
     * @param uri
     */
    public static void work(String uri) {
        try {
            JSParamObject jpo = sJSApiBridge.parse(uri);
            sJSApiBridge.callProxyClass(jpo);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("JSApiBridge uri parse fail!");
        }
    }

    /**
     * parse uri
     * @param uri
     * @throws UnsupportedEncodingException
     */
    private JSParamObject parse(String uri) throws UnsupportedEncodingException {
        if(uri == null) {
            System.out.println("uri is null!");
            return null;
        }

        String decodeUri = URLDecoder.decode(uri, "utf-8");
        Pattern pattern = Pattern.compile("(^native.*//api/)(.*)(\\?p=)(.*)");
        Matcher matcher = pattern.matcher(decodeUri);
        if(!matcher.matches() || matcher.groupCount() != 4) {
            System.out.println("uri is invalid!");
            return null;
        }

        String method = matcher.group(2);
        String params = matcher.group(4);
        return new JSParamObject(method, params);
    }

    /**
     * get proxy class
     * @param jpo
     * @return
     */
    private boolean callProxyClass(JSParamObject jpo) {
        if(jpo == null) {
            return false;
        }

        try {
            if(mProxyClassInstance == null || mMethod == null) {
                String reflectClass = JSApiConfig.PACKAGE_NAME + "." + JSApiConfig.CLASS_NAME;
                Class<?> proxyClass = Class.forName(reflectClass);
                mProxyClassInstance = proxyClass.newInstance();
                mMethod = proxyClass.getDeclaredMethod(JSApiConfig.METHOD_NAME, new Class[]{String.class, String.class});
            }
            mMethod.invoke(mProxyClassInstance, jpo.method, jpo.params);
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("JSApiBridge ClassNotFoundException");
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.out.println("JSApiBridge InstantiationException");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("JSApiBridge IllegalAccessException");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("JSApiBridge NoSuchMethodException");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("JSApiBridge InvocationTargetException");
        }

        return false;
    }

    /**
     * param object
     */
    class JSParamObject {
        String method;
        String params;

        public JSParamObject(String method, String params) {
            this.method = method;
            this.params = params;
        }
    }

}
