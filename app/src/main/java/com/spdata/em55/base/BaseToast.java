package com.spdata.em55.base;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 张明_ on 2017/8/24.
 */

public class BaseToast {
    private Toast toast = null;

    public void showShortToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
