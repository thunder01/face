package com.verfiface.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 使用OkHttp3发送请求
 * Created by jacks on 2018/5/15.
 */

public class OkHttpUtil {
    /**
     * get请求
     * @param url
     * @return
     */
    public static String getRequest(String url){
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        final Request request = new Request.Builder()
                .url(url)//请求的url
                .get()//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
                .build(); //构建一个请求Request对象

        //同步请求
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送post请求application/json
     * @param url 请求地址
     * @param json 传递的json数据
     * @return
     */
    public static String postRequest(String url,String json){
        //创建OkHttpClient对象
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        okhttp3.RequestBody requestBody= okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        final Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .build();

        //创建/Call
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送post请求application/x-www-form-urlencoded
     * @param url 请求地址
     * @param map 表单数据
     * @return
     */
    public static String postRequestForm(String url, Map<String,Object> map){
        //创建OkHttpClient对象
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        //post方式提交的数据
        FormBody.Builder builder=new FormBody.Builder();
        if (map!=null){
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                builder.add(next.getKey(), next.getValue().toString());
            }
        }

        FormBody formBody=builder.build();

        final Request request = new Request.Builder()
                .url(url)//请求的url
                .post(formBody)
                .build();

        //创建/Call
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
