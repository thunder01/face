package com.verfiface.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.verfiface.activity.R;
import com.verfiface.utils.XEditUtils;

/**
 * 弹出框组件，用来输入信息
 * Created by jacks on 2018/5/18.
 */
public class PopEditText extends PopupWindow {
    private Context mContext;
    private View view;
    private Button btn_save_pop;
    public EditText text_idcard;
    public EditText text_name;
    private XEditUtils xEditUtils=new XEditUtils();
    public static final String LETTER_NUMBER = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
            "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
    public PopEditText(Activity mContext, View.OnClickListener itemsOnClick) {
        this.mContext = mContext;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.input_idcardnum, null);

        text_idcard = (EditText) view.findViewById(R.id.text_idcard);
        text_name = view.findViewById(R.id.text_name);

        btn_save_pop = (Button) view.findViewById(R.id.btn_save_pop);

        /*设置正则过滤*/
       /* xEditUtils.set(text_idcard, LETTER_NUMBER, "请输入正确的身份证号码");*/

        // 设置按钮监听
        btn_save_pop.setOnClickListener(itemsOnClick);
        // 设置外部可点击
        this.setOutsideTouchable(true);

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);

        // 设置弹出窗体的宽和高
       /*
       * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
       * 对象,这样这可以以同样的方式改变这个Activity的属性.
       */
        Window dialogWindow = mContext.getWindow();

        WindowManager m = mContext.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        this.setHeight((int)(d.getHeight()*0.4));
        this.setWidth((int) (d.getWidth() * 0.8));

        // 设置弹出窗体可点击
        this.setFocusable(true);

    }
}
