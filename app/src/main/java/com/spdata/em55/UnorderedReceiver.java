package com.spdata.em55;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by suntianwei on 2016/12/27.
 */

public class UnorderedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
        String action = i.getAction();
        if (action.equals("com.geomobile.hallremove")) {
            Intent intent1 = new Intent();
            intent1.setClass(context, MenuAct.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
