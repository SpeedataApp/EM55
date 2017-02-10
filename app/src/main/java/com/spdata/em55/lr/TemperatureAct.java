package com.spdata.em55.lr;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spdata.em55.R;
import com.spdata.em55.base.BaseAct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureAct extends BaseAct implements View.OnClickListener {

    //private static final String DEVFILE_PATH = "/proc/driver/captwo";
    private static final String HUMFILE_PATH = "/sys/class/misc/si7020/hum";
    private static final String TEMFILE_PATH = "/sys/class/misc/si7020/tem";
    private TextView Temperature;
    private TextView Humidity;
    private Button btnstart;
    private Button btnstop;
    private PowerManager pM = null;
    private WakeLock wK = null;
    private Timer timer = null;
    private String Hum = "0";
    private String Tem = "0";
    private BufferedReader read_Hum;
    private BufferedReader read_Tem;
    private boolean isRunning = false;
    File HumName;
    File TemName;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wsd);
        initUI();
    }

    @Override
    public void onClick(View v) {
        if (v == btnstart) {
            isRunning = true;
            startTimerTask();
        } else if (v == btnstop) {
            isRunning = false;
            Humidity.setText("%");
            Temperature.setText("℃");
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what > 0) {
                float[] buf = (float[]) msg.obj;
                Humidity.setText(String.valueOf(buf[0]) + "%");
                Temperature.setText(String.valueOf(buf[1]) + "℃");
                Log.d("######", "Hum:" + String.valueOf(buf[0]) + "Tem:" + String.valueOf(buf[1]));
            }
        }
    };

    private void initUI() {
        Temperature = (TextView) findViewById(R.id.Tem_value);
        Humidity = (TextView) findViewById(R.id.Hum_value);
        btnstart = (Button) findViewById(R.id.buttonstart);
        btnstop = (Button) findViewById(R.id.buttonstop);
        btnstart.setOnClickListener(this);
        btnstop.setOnClickListener(this);
        Temperature.setTextColor(Color.BLACK);
        Humidity.setTextColor(Color.BLACK);

        HumName = new File(HUMFILE_PATH);
        TemName = new File(TEMFILE_PATH);

        pM = (PowerManager) getSystemService(POWER_SERVICE);
        if (pM != null) {
            wK = pM.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockpsam");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wK != null) {
            wK.acquire();
        }
        if (isRunning) {
            isRunning = true;
            startTimerTask();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startTimerTask() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        read_Hum = new BufferedReader(new FileReader(HumName));
                        Hum = read_Hum.readLine();
                        read_Tem = new BufferedReader(new FileReader(TemName));
                        Tem = read_Tem.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("#######", Hum + Tem);
                    float[] data = new float[2];
                    data[0] = Float.parseFloat(Hum);
                    data[1] = Float.parseFloat(Tem) / 100;
                    Message message = new Message();
                    message.what = 1;
                    message.obj = data;
                    handler.sendMessage(message);
                }
            }, 0, 1000);
        }
    }
}