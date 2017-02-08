package com.spdata.em55.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by brxu on 2017/1/11.
 */

public class BaseAct extends Activity {
    /**
     * com.geomobile.hallremove
     * 监听背夹离开主机的广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.geomobile.hallremove");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        unregisterReceiver(receiver);
    }
    /**
     * 打开指定的Activity页面
     *
     * @param actClass Activity页面类
     */
    public void openAct( Class<?> actClass) {
        Intent intent=null;
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(this, actClass);
       startActivity(intent);
    }
}
