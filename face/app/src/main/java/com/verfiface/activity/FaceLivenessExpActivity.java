package com.verfiface.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ui.FaceLivenessActivity;
import com.verfiface.baiduservice.BaiduService;
import com.verfiface.utils.OkHttpUtil;
import com.verfiface.widget.DefaultDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FaceLivenessExpActivity extends FaceLivenessActivity {

    private DefaultDialog mDefaultDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLivenessCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap) {
        super.onLivenessCompletion(status, message, base64ImageMap);
        if (status == FaceStatusEnum.OK && mIsCompletion) {
            showMessageDialog("活体检测", "检测成功");

            /*处理检测到的图片*/
            ArrayList<Map> maps = new ArrayList<>(10);
            Iterator<Map.Entry<String, String>> iterator = base64ImageMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                String image=next.getValue();
                Map<String,Object> map=new HashMap<>();
                map.put("image",image);
                map.put("image_type","BASE64");
                map.put("face_field","age,beauty,expression,faceshape,gender,glasses,landmark,race,quality,facetype,parsing");
                maps.add(map);
            }

            String param= JSON.toJSON(maps).toString();
            /*活体加测接口地址*/
            String url="https://aip.baidubce.com/rest/2.0/face/v3/faceverify?access_token="+ BaiduService.access_token;

                /*发送请求*/
            String response = OkHttpUtil.postRequest(url, param);
            System.out.println("活体检测响应信息："+response);
                /*把响应信息传导TextActivity去展示出来*/
            Intent intent=new Intent(FaceLivenessExpActivity.this,TextActivity.class);
            intent.putExtra("huoTiData",response);
            startActivity(intent);
        } else if (status == FaceStatusEnum.Error_DetectTimeout ||
                status == FaceStatusEnum.Error_LivenessTimeout ||
                status == FaceStatusEnum.Error_Timeout) {
            showMessageDialog("活体检测", "采集超时");
        }
    }

    private void showMessageDialog(String title, String message) {
        if (mDefaultDialog == null) {
            DefaultDialog.Builder builder = new DefaultDialog.Builder(this);
            builder.setTitle(title).
                    setMessage(message).
                    setNegativeButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDefaultDialog.dismiss();
                                    finish();
                                }
                            });
            mDefaultDialog = builder.create();
            mDefaultDialog.setCancelable(true);
        }
        mDefaultDialog.dismiss();
        mDefaultDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
    }

}
