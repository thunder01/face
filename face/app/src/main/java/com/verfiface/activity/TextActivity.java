package com.verfiface.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 本页面就一个TextView 用来展示人脸识别的结果
 * Created by jacks on 2018/5/15.
 */

public class TextActivity extends AppCompatActivity {
    private TextView t1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*设置对应的布局文件*/
        setContentView(R.layout.activity_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        t1=findViewById(R.id.t1);
        /*文本可以滑动*/
        t1.setMovementMethod(ScrollingMovementMethod.getInstance());

        Intent intent=getIntent();

        /*解析并展示人脸检测数据*/
        String renLianData = intent.getStringExtra("renLianData");
        if (renLianData!=null){
            String msg = jieXiRenLian(renLianData);
            t1.setText(msg);
            t1.setTextSize(16);
        }

        /*解析并展示活体检测数据*/
        String huoTiData = intent.getStringExtra("huoTiData");
        if (huoTiData!=null){
            String msg = jieXiHuoTi(huoTiData);
            t1.setText(msg);
            t1.setTextSize(16);
        }

        /*解析身份识别响应信息*/
        String shenFenData = intent.getStringExtra("shenFenData");
        if (shenFenData!=null){
            String msg = jieXiShenFen(shenFenData);
            t1.setText(msg);
            t1.setTextSize(16);
        }

        /*解析身份证正面信息*/
        String shenFenZhengData_front = intent.getStringExtra("shenFenZhengData_front");
        if (shenFenZhengData_front!=null){
            String msg = jieXiShenFenZhengData_front(shenFenZhengData_front);
            t1.setText(msg);
            t1.setTextSize(16);
        }

        /*解析身份证反面信息*/
        String shenFenZhengData_back = intent.getStringExtra("shenFenZhengData_back");
        if (shenFenZhengData_back!=null){
            String msg = jieXiShenFenZhengData_back(shenFenZhengData_back);
            t1.setText(msg);
            t1.setTextSize(16);
        }
    }

    private String jieXiRenLian(String data){
        /*解析返回数据*/
        String str="";
        /*解析json数据很可能会出现异常*/
        try {
            JSONObject object = JSON.parseObject(data);
            int error_code = (int) object.get("error_code");

            if (error_code != 0) {
                return str = "没人检测到人脸";
            }

            JSONObject result = object.getJSONObject("result");
            Object face_num = result.get("face_num");
            str = str + "人脸数量: \n" + face_num + "\n\n";

            JSONArray face_list = result.getJSONArray("face_list");
            JSONObject face0 = face_list.getJSONObject(0);

            Object face_token = face0.get("face_token");
            str = str + "人脸标识: \n" + face_token + "\n\n";

            /*{"left":177.5385437,"top":461.8887024,"width":574,"height":594,"rotation":1}*/
            JSONObject location = face0.getJSONObject("location");

            Object face_probability = face0.get("face_probability");
            str = str + "人脸置信度，范围【0~1】，代表这是一张人脸的概率，0最小、1最大。: \n" + face_probability + "\n\n";

            /*{"yaw":-1.250745893,"pitch":-2.49588275,"roll":1.761174917}*/
            JSONObject angel = face0.getJSONObject("angle");

            Object age = face0.get("age");
            str = str + "年龄: \n" + age + "\n\n";

            Object beauty = face0.get("beauty");
            str = str + "美丑打分: \n" + beauty + "\n\n";

            /*{"type":"none","probability":1}*/
            JSONObject expression = face0.getJSONObject("expression");
            str = str + "表情 none:不笑；smile:微笑；laugh:大笑：\n" + expression + "\n\n";

            /*{"type":"oval","probability":0.5363594294}*/
            JSONObject face_shape = face0.getJSONObject("face_shape");
            Object type = face_shape.get("type");
            str = str + "脸型\ntype:square: 正方形 triangle:三角形 oval: 椭圆 heart: 心形 round: 圆形：\n" + type + "\n\n";

            /*{"type":"male","probability":0.999961257}*/
            JSONObject gender = face0.getJSONObject("gender");
            str = str + "性别: \n" + gender + "\n\n";

            /*{"type":"none","probability":0.9999989271}*/
            JSONObject glasses = face0.getJSONObject("glasses");
            str = str + "none:无眼镜，common:普通眼镜，sun:墨镜: \n" + glasses + "\n\n";

            /*4个关键点位置，左眼中心、右眼中心、鼻尖、嘴中心。face_field包含landmark时返回
            [{"x":329.117157,"y":584.6544189},{"x":593.7182617,"y":593.8199463},{"x":460.861084,"y":711.6518555},{"x":455.0184326,"y":873.7109985}]*/
            JSONArray landmark = face0.getJSONArray("landmark");

            /*72个特征点位置 face_field包含landmark时返回
            [{"x":173.9103699,"y":565.697876},{"x":174.9132996,"y":661.1331787},{"x":186.464447,"y":756.6004639},
            {"x":207.1963043,"y":852.5240479},{"x":262.1552429,"y":951.5587158},{"x":349.7450562,"y":1034.456787},
            {"x":444.6045227,"y":1065.809448},{"x":542.9985962,"y":1043.843628},{"x":635.1504517,"y":968.9255371},
            {"x":699.9869385,"y":874.2775879},{"x":728.1074219,"y":778.6029053},{"x":742.6005249,"y":682.2059326},
            {"x":747.5059814,"y":587.9814453},{"x":266.1724243,"y":583.5981445},{"x":296.6925659,"y":574.7520142},
            {"x":325.6982727,"y":572.9078369},{"x":354.3930054,"y":576.5432129},{"x":380.990387,"y":595.2526855},
            {"x":353.5166016,"y":600.2901611},{"x":324.2706299,"y":602.5368652},{"x":294.0435791,"y":596.4384155},
            {"x":329.117157,"y":584.6544189},{"x":225.0798187,"y":504.8059082},{"x":267.7462463,"y":470.2713013},
            {"x":317.8563843,"y":470.495575},{"x":364.4022217,"y":481.1980286},{"x":407.4625549,"y":515.6776733},
            {"x":508.1792603,"y":878.2960205},{"x":453.0608826,"y":878.6826172},{"x":401.0121155,"y":875.6092529}]*/
            JSONArray landmark72 = face0.getJSONArray("landmark72");

            /*{"type":"yellow","probability":1}*/
            JSONObject race = face0.getJSONObject("race");
            str = str + "yellow: 黄种人 white: 白种人 black:黑种人 arabs: 阿拉伯人: \n" + race + "\n\n";

            /*人脸质量信息
            {"occlusion":{"left_eye":0,"right_eye":0,"nose":0,"mouth":0,"left_cheek":0.02147852071,"right_cheek":0.04817444086,"chin_contour":0.004912907723},
            "blur":3.380278812e-8,"illumination":114,"completeness":1}*/
            JSONObject quality = face0.getJSONObject("quality");

            /*{"type":"human","probability":0.9997002482}*/
            JSONObject face_type = face0.getJSONObject("face_type");
            str = str + "人脸类型human: 真实人脸 cartoon: 卡通人脸： \n" + face_type + "\n\n";

            Object parsing_info = face0.get("parsing_info");
        }catch (Exception e){
            str="出异常了：\n"+e.getMessage();
        }
        return  str;
    }

    /*解析活体检测数据*/
    private String jieXiHuoTi(String data){
        String str="";
        /*解析JSON数据*/
        try{
            JSONObject object = JSON.parseObject(data);
            int error_code = (int)object.get("error_code");
            if (error_code!=0){
                return str=str+object.get("error_msg");
            }

            /*活体检测的结果*/
            JSONObject result = object.getJSONObject("result");
            Object face_liveness = result.get("face_liveness");
            str=str+"活体分数值:\n"+face_liveness+"\n\n";

            JSONObject thresholds = result.getJSONObject("thresholds");
            str=str+"阈值数据:\n"+thresholds+"\n\n";

            JSONArray face_list = result.getJSONArray("face_list");
            JSONObject face0 = face_list.getJSONObject(0);

            Object face_token = face0.get("face_token");
            str = str + "人脸标识: \n" + face_token + "\n\n";

            /*{"left":177.5385437,"top":461.8887024,"width":574,"height":594,"rotation":1}*/
            JSONObject location = face0.getJSONObject("location");

            Object face_probability = face0.get("face_probability");
            str = str + "人脸置信度，范围【0~1】，代表这是一张人脸的概率，0最小、1最大。: \n" + face_probability + "\n\n";

            /*{"yaw":-1.250745893,"pitch":-2.49588275,"roll":1.761174917}*/
            JSONObject angel = face0.getJSONObject("angle");

            Object age = face0.get("age");
            str = str + "年龄: \n" + age + "\n\n";

            Object beauty = face0.get("beauty");
            str = str + "美丑打分: \n" + beauty + "\n\n";

            /*{"type":"none","probability":1}*/
            JSONObject expression = face0.getJSONObject("expression");
            str = str + "表情 none:不笑；smile:微笑；laugh:大笑：\n" + expression + "\n\n";

            /*{"type":"oval","probability":0.5363594294}*/
            JSONObject face_shape = face0.getJSONObject("face_shape");
            Object type = face_shape.get("type");
            str = str + "脸型\ntype:square: 正方形 triangle:三角形 oval: 椭圆 heart: 心形 round: 圆形：\n" + type + "\n\n";

            /*{"type":"male","probability":0.999961257}*/
            JSONObject gender = face0.getJSONObject("gender");
            str = str + "性别: \n" + gender + "\n\n";

            /*{"type":"none","probability":0.9999989271}*/
            JSONObject glasses = face0.getJSONObject("glasses");
            str = str + "none:无眼镜，common:普通眼镜，sun:墨镜: \n" + glasses + "\n\n";

            /*4个关键点位置，左眼中心、右眼中心、鼻尖、嘴中心。face_field包含landmark时返回
            [{"x":329.117157,"y":584.6544189},{"x":593.7182617,"y":593.8199463},{"x":460.861084,"y":711.6518555},{"x":455.0184326,"y":873.7109985}]*/
            JSONArray landmark = face0.getJSONArray("landmark");

            /*72个特征点位置 face_field包含landmark时返回
            [{"x":173.9103699,"y":565.697876},{"x":174.9132996,"y":661.1331787},{"x":186.464447,"y":756.6004639},
            {"x":207.1963043,"y":852.5240479},{"x":262.1552429,"y":951.5587158},{"x":349.7450562,"y":1034.456787},
            {"x":444.6045227,"y":1065.809448},{"x":542.9985962,"y":1043.843628},{"x":635.1504517,"y":968.9255371},
            {"x":699.9869385,"y":874.2775879},{"x":728.1074219,"y":778.6029053},{"x":742.6005249,"y":682.2059326},
            {"x":747.5059814,"y":587.9814453},{"x":266.1724243,"y":583.5981445},{"x":296.6925659,"y":574.7520142},
            {"x":325.6982727,"y":572.9078369},{"x":354.3930054,"y":576.5432129},{"x":380.990387,"y":595.2526855},
            {"x":353.5166016,"y":600.2901611},{"x":324.2706299,"y":602.5368652},{"x":294.0435791,"y":596.4384155},
            {"x":329.117157,"y":584.6544189},{"x":225.0798187,"y":504.8059082},{"x":267.7462463,"y":470.2713013},
            {"x":317.8563843,"y":470.495575},{"x":364.4022217,"y":481.1980286},{"x":407.4625549,"y":515.6776733},
            {"x":508.1792603,"y":878.2960205},{"x":453.0608826,"y":878.6826172},{"x":401.0121155,"y":875.6092529}]*/
            JSONArray landmark72 = face0.getJSONArray("landmark72");

            /*{"type":"yellow","probability":1}*/
            JSONObject race = face0.getJSONObject("race");
            str = str + "yellow: 黄种人 white: 白种人 black:黑种人 arabs: 阿拉伯人: \n" + race + "\n\n";

            /*人脸质量信息
            {"occlusion":{"left_eye":0,"right_eye":0,"nose":0,"mouth":0,"left_cheek":0.02147852071,"right_cheek":0.04817444086,"chin_contour":0.004912907723},
            "blur":3.380278812e-8,"illumination":114,"completeness":1}*/
            JSONObject quality = face0.getJSONObject("quality");

            /*{"type":"human","probability":0.9997002482}*/
            JSONObject face_type = face0.getJSONObject("face_type");
            str = str + "人脸类型human: 真实人脸 cartoon: 卡通人脸： \n" + face_type + "\n\n";
        }catch (Exception e){
            str="出现异常:\n"+e.getMessage();
        }
        return str;
    }

    /*解析身份识别信息*/
    private String jieXiShenFen(String data){
        return data;
    }

    /*解析身份证识别信息*/
    private String jieXiShenFenZhengData_front(String data){
        return data;
    }

    /*解析身份证识别信息*/
    private String jieXiShenFenZhengData_back(String data){
        return data;
    }
}
