package com.verfiface.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.verfiface.baiduservice.BaiduService;
import com.verfiface.utils.Base64Util;
import com.verfiface.utils.OkHttpUtil;
import com.verfiface.widget.PopEditText;
import com.verfiface.widget.DefaultDialog;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerfifaceActivity extends AppCompatActivity{
    private DefaultDialog mDefaultDialog;
    private PopEditText popEditText;
    public Uri imageUriFromCamera;
    private AlertDialog alertDialog;
    /*AlertDialog单选列表中默认被选中的item下标*/
    private int checkedItemId=0;
    /*拍摄的照片*/
    private File picture;
    private Button b1;/*拍照*/
    private Button b2;/*相册*/
    private Button b3;/*人脸检测*/
    private Button b4;/*活体检测*/
    private Button b5;/*身份验证*/
    private Button b6;/*身份证识别*/
    private ImageView p1;/*图片预览*/

    /*系统相册路径*/
    private String path = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DCIM +File.separator+"Camera"+File.separator;

    /*加载动画*/
    ZLoadingDialog dialog = new ZLoadingDialog(VerfifaceActivity.this);
    ZLoadingDialog zLoadingDialog = dialog.setLoadingBuilder(Z_TYPE.CIRCLE)//设置类型
            .setLoadingColor(Color.BLACK)//颜色
            .setHintText("Loading...");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*设置对应的布局文件*/
        setContentView(R.layout.activity_verfiface);

        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);

        p1 = findViewById(R.id.p1);

        /**拍照*/
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = createImagePathFile(VerfifaceActivity.this);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                /*
                * 这里就是高版本需要注意的，需用使用FileProvider来获取Uri，同时需要注意getUriForFile
                * 方法第二个参数要与AndroidManifest.xml中provider的里面的属性authorities的值一致
                * */
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUriFromCamera = FileProvider.getUriForFile(VerfifaceActivity.this,
                        "com.verfiface.activity.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);

                startActivityForResult(intent, 100);
            }
        });

        /**打开相册*/
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** 打开相册*/
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, 101);
            }
        });

        /*调用百度的人脸检测接口*/
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*把当前展示的照片通过Base64转成字符串*/
                final String image = getStringImage();

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
                outFile("renLianData.txt",response);
                System.out.println("人脸识别响应信息 "+response);

                /*把响应信息传导TextActivity去展示出来*/
                Intent intent=new Intent(VerfifaceActivity.this,TextActivity.class);
                intent.putExtra("renLianData",response);
                startActivity(intent);
            }
        });

        /*调用活体检测*/
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*base64编码后的图片*/
                String image = getStringImage();

                /*构建请求参数,这里只使用一张图片*/
                Map<String,Object> map=new HashMap<>();
                ArrayList<Map> maps = new ArrayList<>(10);
                map.put("image",image);
                map.put("image_type","BASE64");
                map.put("face_field","age,beauty,expression,faceshape,gender,glasses,landmark,race,quality,facetype,parsing");
                maps.add(map);
                String param=JSON.toJSON(maps).toString();

                /*活体加测接口地址*/
                String url="https://aip.baidubce.com/rest/2.0/face/v3/faceverify?access_token="+BaiduService.access_token;

                /*发送请求*/
                String response = OkHttpUtil.postRequest(url, param);
                outFile("huoTiData.txt",response);

                /*把响应信息传导TextActivity去展示出来*/
                Intent intent=new Intent(VerfifaceActivity.this,TextActivity.class);
                intent.putExtra("huoTiData",response);
                startActivity(intent);
            }
        });

        /*身份验证*/
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditPopWin(view);
            }
        });

        /*身份证识别*/
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleListDialog();
            }
        });
    }

    /*弹出输入框，输入身份证号*/
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save_pop:
                    String name = popEditText.text_name.getText().toString().trim();
                    String idcarnum = popEditText.text_idcard.getText().toString().trim();

                    /*把当前展示的照片通过Base64转成字符串*/
                    final String image = getStringImage();

                    Map<String,Object> map=new HashMap<>();
                    map.put("image", image);
                    map.put("image_type","BASE64");
                    map.put("id_card_number",idcarnum);
                    map.put("name",name);
                    map.put("quality_control","NORMAL");
                    map.put("liveness_control","HIGH");

                    /*请求参数转成JSON*/
                    String param = JSON.toJSON(map).toString();

                    /*构建请求地址*/
                    String url="https://aip.baidubce.com/rest/2.0/face/v3/person/verify?access_token="+ BaiduService.access_token;

                    /*发送post请求*/
                    final String response = OkHttpUtil.postRequest(url, param);
                    outFile("renLianData.txt",response);
                    System.out.println("身份识别响应信息 "+response);
                    outFile("shenFenData.txt",response);

                    /*把响应信息传导TextActivity去展示出来*/
                    Intent intent=new Intent(VerfifaceActivity.this,TextActivity.class);
                    intent.putExtra("shenFenData",response);
                    startActivity(intent);

                    popEditText.dismiss();
                    break;
            }
        }
    };
    public void showEditPopWin(View view) {
        popEditText = new PopEditText(this,onClickListener);
        popEditText.showAtLocation(findViewById(R.id.p1), Gravity.CENTER, 0, 0);
    }

    /**处理拍照或打开相册之后的请求，拍照requestCode=100，打开相册requestCode=101*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        /**拍照*/
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            /*这里我们发送广播让MediaScanner 扫描我们制定的文件
            * 这样在系统的相册中我们就可以找到我们拍摄的照片了 */
            Uri uri = Uri.fromFile(picture);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            this.sendBroadcast(intent);

            /*在将刚才拍摄的照片展示到imageview上*/
            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
            p1.setImageBitmap(bitmap);
        }
        /**打开相册,选择图片，在imageview上预览*/
        if (resultCode == Activity.RESULT_OK && requestCode == 101){
            Uri selectedImage = data.getData();
            /*把从相册选择的图片赋值给picture*/
            try{
                picture = new File(new URI(selectedImage.toString()));
            }catch (Exception e){
                e.printStackTrace();
            }
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            p1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    /*创建图片的url*/
    public File createImagePathFile(Activity activity) {
        /*根据时间命名文件*/
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imageName="IMG_"+dateFormat.format(date)+".jpg";

        //文件目录可以根据自己的需要自行定义,这里设置成了系统相册的路径
        Uri imageFilePath;
        picture = new File(path,imageName );
        imageFilePath = Uri.fromFile(picture);
        return picture;
    }

    /*把当前展示的照片通过Base64转成字符串*/
    private String getStringImage(){
        p1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(p1.getDrawingCache());
        p1.setDrawingCacheEnabled(false);
        String image = Base64Util.bitmapToBase64(bitmap);
        return image;
    }

    /*弹出对话框，选择身份证正面还是反面*/
    public void showSingleListDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("选择身份证的正反面");
        builder.setIcon(android.R.drawable.btn_star);
        //为AlertDialog设置单选列表
        //setSingleChoiceItems(数据资源id,默认被选中的item下标[-1表示没有默认选中],点击item触发的监听事件)
        builder.setSingleChoiceItems(R.array.id_card_face, checkedItemId, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                checkedItemId=which;
                String id_card_side="front";
                switch (which)
                {
                    /*选正面*/case 0: id_card_side="front"; break;
                    /*选反面*/case 1: id_card_side="back";  break;
                }
                /*身份证图片,base64转码之后还要进行urlencode编码*/
                String strImage = getStringImage();

                Map<String, Object> map = new HashMap<>();
                map.put("id_card_side",id_card_side);
                try{
                    map.put("image",strImage);
                }catch (Exception e){
                    e.printStackTrace();
                }

                /*接口地址*/
                String url="https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token="+BaiduService.access_token;;

                /*发送请求*/
                String response = OkHttpUtil.postRequestForm(url,map);
                System.out.println("身份证识别信息：\n"+response);
                alertDialog.dismiss();//表示点击item后自动关闭整个Dialog,因为单选列表Dialog选择后是不会自动关闭的，因此需要此语句辅助关闭。

                /*把响应信息传导TextActivity去展示出来*/
                Intent intent=new Intent(VerfifaceActivity.this,TextActivity.class);

                if ("front".equals(id_card_side)){
                    intent.putExtra("shenFenZhengData_front",response);
                    outFile("shenFenZhengData_front.txt",response);
                }else if ("back".equals(id_card_side)){
                    intent.putExtra("shenFenZhengData_back",response);
                    outFile("shenFenZhengData_back.txt",response);
                }
                startActivity(intent);

            }
        });
        alertDialog=builder.create();
        alertDialog.show();
    }

    /**
     * 输出到文件
     * /data/data/com.my.app/cache
     */
    public void outFile(String fileName,String s) {
        String state = Environment.getExternalStorageState();//获取外部设备状态

        //检测外部设备是否可用
        if(!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "外部设备不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        //创建文件
        File sdCard = Environment.getExternalStorageDirectory();//获取外部设备的目录
        File file = new File(sdCard,fileName);//文件位置

        try (FileOutputStream fop = new FileOutputStream(file)) {
            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // get the content in bytes
            byte[] contentInBytes = s.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
