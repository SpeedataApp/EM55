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

import java.util.Timer;

import speedatacom.humitures.HumitureManager;
import speedatacom.humitures.IHumiture;

public class TemperatureAct extends BaseAct implements View.OnClickListener {
    private TextView Temperature;
    private TextView Humidity;
    private Button btnstart;
    private Button btnstop;
    private PowerManager pM = null;
    private WakeLock wK = null;
    private Timer timer = null;
    private boolean isRunning = false;
    IHumiture humitureManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wsd);
        initUI();
        humitureManager = HumitureManager.getHumitureIntance();
    }

    @Override
    public void onClick(View v) {
        if (v == btnstart) {
            isRunning = true;
            humitureManager.startTimerTask(handler);
        } else if (v == btnstop) {
            isRunning = false;
            Humidity.setText("%");
            Temperature.setText("℃");
            humitureManager.stopTimerTask();
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
            humitureManager.startTimerTask(handler);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        humitureManager.stopTimerTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        humitureManager.stopTimerTask();
    }
}