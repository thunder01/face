package com.verfiface.baiduservice;

import com.alibaba.fastjson.JSONObject;
import com.verfiface.utils.OkHttpUtil;

/**
 * 调用百度人脸识别
 * Created by jacks on 2018/5/15.
 */

public class BaiduService {
    private static final String APPID="11242090";
    private static final String APPKEY="bSWGxwP9VUGC9yPEymRQ1iRQ";
    private static final String SECRET_KEY="kZmUEOTthHyMQk4zwZtHxHvOeeMI944E";

    /*access_token 30天过期*/
    public static String access_token="24.e37f4f0fbf9ee89a4a744cd727e07a42.2592000.1528965699.282335-11242090";

    /*获取access_token*/
    public String getAccessToken(){
        /*构建请求地址，百度的*/
        String url="https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id="+APPKEY+"&client_secret="+SECRET_KEY;
        /*发送一个get请求*/
        String response = OkHttpUtil.getRequest(url);
        System.out.println("响应信息"+response);
        /*从响应信息中提取access_token*/
        JSONObject object = JSONObject.parseObject(response);
        if (object.containsKey("error")){
            return "error";
        }
        access_token=object.get("access_token").toString();

        return access_token;
    }

}
