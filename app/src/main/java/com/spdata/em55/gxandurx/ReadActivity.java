package com.spdata.em55.gxandurx;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.spdata.em55.R;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.speedata.libid2.utils.DataConversionUtils;

import java.io.IOException;
import java.text.DecimalFormat;

import static android.serialport.SerialPort.SERIAL_TTYMT1;


public class ReadActivity extends Activity implements View.OnClickListener {
    SerialPort serialPort;
    DeviceControl control;
    TextView tvmubian, tvhuanjing, tvMAX, tvMIN, btnMAX, btnMIN, btnStart;
    int fd;
    private Thread thread;
    private final String TAG = "ReadActivity";
    float min = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read);
        ApplicationContext.getInstance().addActivity(ReadActivity.this);
        try {
            serialPort = new SerialPort();
            serialPort.OpenSerial(SERIAL_TTYMT1, 9600);
            fd = serialPort.getFd();
            control = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND,73,2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        tvmubian = (TextView) findViewById(R.id.tv_mubiao);
        tvhuanjing = (TextView) findViewById(R.id.tv_huanjing);
        tvMAX = (TextView) findViewById(R.id.tv_max);
        tvMIN = (TextView) findViewById(R.id.tv_min);
        btnMAX = (TextView) findViewById(R.id.btn_max);
        btnMIN = (TextView) findViewById(R.id.btn_min);
        btnStart = (TextView) findViewById(R.id.btn_start);
//        btnStart.setOnClickListener(this);
        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        control.PowerOnDevice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    thread = new ReadThread();
                    thread.start();    //手指按下时触发不停的发送消息
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        control.PowerOffDevice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    thread.interrupt();
                }
                return false;

            }
        });
        btnMIN.setOnClickListener(this);
        btnMAX.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handle message");
            switch (msg.what) {
                case 0:
                    byte[] temp = (byte[]) msg.obj;
                    byte[] mubiaotemp = new byte[2];
                    byte[] huanjingtemp = new byte[2];
                    for (int i = 0; i < temp.length; i += 5) {
                        if (temp[i] == 76) {//目标温度
                            byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                            byte ww = temp[i + 3];
                            if (sum == ww) {

                                System.arraycopy(temp, i + 1, mubiaotemp, 0, 2);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                float mubiao = (float) DataConversionUtils.byteArrayToInt(mubiaotemp);
                                float ss = mubiao / 16;
                                float m = (float) (ss - 273.15);
                                String mresult=saveDecimals(m);
                                if (m>100){
                                    tvmubian.setTextColor(Color.RED);
                                }else {
                                    tvmubian.setTextColor(Color.WHITE);
                                }
                                tvmubian.setText(mresult + "℃");
                                if (m > min) {
                                    tvMAX.setText(mresult);
                                } else {
                                    tvMIN.setText(mresult);
                                }
                                min = m;
                            } else {
                                Log.e(TAG, "check error mubiao  " + DataConversionUtils.byteArrayToStringLog(temp, temp.length));
                                return;
                            }
                        } else if (temp[i] == 102) {//环境温度
                            byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                            if (sum == temp[i + 3]) {
                                System.arraycopy(temp, i + 1, huanjingtemp, 0, 2);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                float huanjing = (float) DataConversionUtils.byteArrayToInt(huanjingtemp);
                                float ss = huanjing / 16;
                                float h = (float) (ss - 273.15);
                                String hresult=saveDecimals(h);
                                tvhuanjing.setText(hresult + "℃");
                            } else {
                                Log.e(TAG, "check error huanjing");
                                return;
                            }
                        }
                    }
                    break;
            }
        }
    };

    public String saveDecimals(float f) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String dff = df.format(f);
        return dff;
    }

    @Override
    public void onClick(View v) {
        if (v == btnMAX) {


        } else if (v == btnMIN) {


        } else if (v == btnStart) {
//            control.PowerOnDevice("94");
            thread = new ReadThread();
            thread.start();
        }

    }


    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted())
                try {
                    byte[] temp1 = serialPort.ReadSerial(fd, 2048);
                    if (temp1 != null) {
                        Message msg = new Message();
                        msg.obj = temp1;
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            control.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            control.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
