package com.verfiface.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.verfiface.baiduservice.Config;
import com.verfiface.baiduservice.ExampleApplication;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks{
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerCustomAdapter mCustomAdapter;
    private Dialog mDefaultDialog;

    private int[] mDataset = new int[]{
            R.string.main_item_face_live,
            R.string.main_item_face_detect,
            R.string.main_item_face_recognition
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 根据需求添加活体动作
        ExampleApplication.livenessList.clear();
        ExampleApplication.livenessList.add(LivenessTypeEnum.Eye);
        ExampleApplication.livenessList.add(LivenessTypeEnum.Mouth);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadUp);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadDown);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadLeft);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadRight);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadLeftOrRight);

        mLayoutManager = new LinearLayoutManager(this);
        mCustomAdapter = new RecyclerCustomAdapter(mDataset);
        this.findViewById(R.id.main_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startItemActivity(SettingsActivity.class);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);

        int scrollPosition = 0;
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setAdapter(mCustomAdapter);

        initLib();
    }

    /**
     * （1）调用FaceSDKManager.getIntance().initialize(context,Config.licenseID, Config.licenseFileName);初始化SDK.。
     */
    private void initLib() {
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(this, Config.licenseID, Config.licenseFileName);
//        setFaceConfig();
    }

    /*（2）初始化SDK参数*/
    private void setFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
        config.setLivenessTypeList(ExampleApplication.livenessList);
        config.setLivenessRandom(ExampleApplication.isLivenessRandom);
        config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
        config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
        config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
        config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL);
        config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
        config.setCheckFaceQuality(true);
        config.setLivenessRandomCount(2);
        config.setFaceDecodeNumberOfThreads(2);

        /*（3）*/
        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    private void startItemActivity(Class itemClass) {
        setFaceConfig();
        /*（4）开启预览。*/
        startActivity(new Intent(this, itemClass));
    }

    class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {
        final int[] itemDataSet;

        class ViewHolder extends RecyclerView.ViewHolder {

            public final View rv;
            public final TextView tv;

            public ViewHolder(View v) {
                super(v);
                rv = v;
                tv = (TextView) v.findViewById(R.id.item_main_text);
            }
        }

        public RecyclerCustomAdapter(int[] dataSet) {
            itemDataSet = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_main, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.tv.setText(itemDataSet[position]);
            viewHolder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (itemDataSet[position]) {
                        case R.string.main_item_face_live:
                            startItemActivity(FaceLivenessExpActivity.class);
                            break;
                        case R.string.main_item_face_detect:
                            startItemActivity(FaceDetectExpActivity.class);
                            break;
                        case R.string.main_item_face_recognition:
                            startItemActivity(VerfifaceActivity.class);
                            break;
                        default:
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemDataSet.length;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        /*多线程安全策略，允许在主线程中发起网络请求*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /**启动时就询问获取权限*/
       requestCodeQRCodePermissions();
    }

    /**
     * 申请权限，Android6.0及以后需要动态获取权限，仅在mainfest中配置不行了
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    /**授权被拒绝*/
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "需要打开相机和读取存储器的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    protected void showMessageDialog(String title, String message) {
        if (mDefaultDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title).
                    setMessage(message).
                    setNegativeButton("ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDefaultDialog.dismiss();
                                    MainActivity.this.finish();
                                }
                            });
            mDefaultDialog = builder.create();
            mDefaultDialog.setCancelable(true);
        }
        mDefaultDialog.dismiss();
        mDefaultDialog.show();
    }
}
