package com.spdata.em55.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;

import com.spdata.em55.R;
import com.spdata.em55.px.print.utils.ApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

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
            ApplicationContext.getInstance().exit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSoundPool();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.geomobile.hallremove");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        unregisterReceiver(receiver);
        if (sp != null) {
            sp.release();
        }
    }

    /**
     * 打开指定的Activity页面
     *
     * @param actClass Activity页面类
     */
    public void openAct(Class<?> actClass) {
        Intent intent = null;
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(this, actClass);
        startActivity(intent);
    }

    /**
     * 检测被接入的背夹具体型号
     * @return
     */
    public String readEm55() {
        String state = null;
        File file = new File("/sys/class/misc/aw9523/gpio");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            state = bufferedReader.readLine();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "readEm55state: " + state);
        return state;
    }

    /**
     * 获取当前应用程序的版本号
     */
    public String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号错误";
        }
    }

    private Map<Integer, Integer> mapSRC;
    private SoundPool sp; //声音池
    //初始化声音池
    private void initSoundPool() {
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mapSRC = new HashMap<Integer, Integer>();
        mapSRC.put(1, sp.load(this, R.raw.error, 0));
        mapSRC.put(2, sp.load(this, R.raw.welcome, 0));
        mapSRC.put(3, sp.load(this, R.raw.msg, 0));
    }

    /**
     * 播放声音池的声音
     */
    public void play(int sound, int number) {
        sp.play(mapSRC.get(sound),//播放的声音资源
                1.0f,//左声道，范围为0--1.0
                1.0f,//右声道，范围为0--1.0
                0, //优先级，0为最低优先级
                number,//循环次数,0为不循环
                0);//播放速率，0为正常速率
    }
}
