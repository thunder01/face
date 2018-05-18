package com.verfiface.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 对输入框的内容进行正则过滤
 * Created by jacks on 2018/5/18.
 */
public class XEditUtils {
    public void set(final EditText et, final String regular, final String msg) {
        et.addTextChangedListener(new TextWatcher() {
            String before = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                before = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(regular)) {
                    et.setText(before);
                    et.setSelection(et.getText().toString().length());
                    if (s.toString().matches(regular))
                    Toast.makeText(et.getContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}
