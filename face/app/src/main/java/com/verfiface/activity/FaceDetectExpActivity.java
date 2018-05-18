package com.verfiface.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ui.FaceDetectActivity;
import com.verfiface.baiduservice.BaiduService;
import com.verfiface.utils.OkHttpUtil;
import com.verfiface.widget.DefaultDialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FaceDetectExpActivity extends FaceDetectActivity {

    private DefaultDialog mDefaultDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*（8） 实现IDetectStrategyCallback的onDetectCompletion并处理结果。其中base64ImageMap为存放最佳人脸和每个活体动作的图片。
            可以查看起父类FaceLivenessActivity的saveImage和base64ToImage方法，获取对于的bitamap。*/
    @Override
    public void onDetectCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap) {
        super.onDetectCompletion(status, message, base64ImageMap);
        if (status == FaceStatusEnum.OK && mIsCompletion) {
            showMessageDialog("人脸图像采集", "采集成功");

            /*处理采集到的图像,key为bestImage0 value为bitmap值*/
            Iterator<Map.Entry<String, String>> iterator = base64ImageMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                final String image = next.getValue();
                Map<String,Object> map=new HashMap<>();
                map.put("image", image);
                map.put("image_type","BASE64");
                map.put("face_field","age,beauty,expression,faceshape,gender,glasses,landmark,race,quality,facetype,parsing");
                map.put("max_face_num",1);
                map.put("face_type","LIVE");

                /*请求参数转成JSON*/
                String param = JSON.toJSON(map).toString();

                /*构建请求地址*/
                String url="https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token="+ BaiduService.access_token;

                /*发送post请求*/
                final String response = OkHttpUtil.postRequest(url, param);
                System.out.println("人脸识别响应信息 "+response);
                /*把响应信息传导TextActivity去展示出来*/
                Intent intent=new Intent(FaceDetectExpActivity.this,TextActivity.class);
                intent.putExtra("renLianData",response);
                startActivity(intent);
            }


        } else if (status == FaceStatusEnum.Error_DetectTimeout ||
                status == FaceStatusEnum.Error_LivenessTimeout ||
                status == FaceStatusEnum.Error_Timeout) {
            showMessageDialog("人脸图像采集", "采集超时");
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
