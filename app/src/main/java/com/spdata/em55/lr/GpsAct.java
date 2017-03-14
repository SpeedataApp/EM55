package com.spdata.em55.lr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spdata.em55.R;
import com.spdata.em55.base.BaseAct;

import java.io.IOException;

public class GpsAct extends BaseAct implements View.OnClickListener {
    TextView textView;
    Button button, btngpsApp;
    SerialPort serialPort;
    DeviceControl deviceControl;
    int fd = 0;
    private readeThread thread;
    private PackageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gps);
        button = (Button) findViewById(R.id.btn_fire);
        btngpsApp = (Button) findViewById(R.id.btn_gps);
        textView = (TextView) findViewById(R.id.text_info);
        button.setOnClickListener(this);
        btngpsApp.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        manager = getPackageManager();
        serialPort = new SerialPort();
        try {
            serialPort.OpenSerial(SerialPort.SERIAL_TTYMT2, 9600);
            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 88, 1);
            fd = serialPort.getFd();
            deviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void intentApp(String packages) {
        Intent intent = new Intent();
        intent = manager.getLaunchIntentForPackage(packages);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            thread = new readeThread();
            thread.start();
        } else if (v == btngpsApp) {
            intentApp("com.androits.gps.test.pro");
        }

    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] temp = (byte[]) msg.obj;
            if (temp == null) {
            } else {
                String s = byteArrayToAscii(temp);
                textView.setText(s);

            }
        }
    };

    private class readeThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    fd = serialPort.getFd();

                    byte[] temp1 = serialPort.ReadSerial(fd, 1024);
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
    }

    public static String byteArrayToAscii(byte[] cmds) {
        int tRecvCount = cmds.length;
        StringBuffer tStringBuf = new StringBuffer();
        String nRcvString;
        char[] tChars = new char[tRecvCount];
        for (int i = 0; i < tRecvCount; i++) {
            tChars[i] = (char) cmds[i];
        }
        tStringBuf.append(tChars);
        nRcvString = tStringBuf.toString(); // nRcvString从tBytes转成了String类型的"123"
        return nRcvString;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (thread != null) {
            thread.interrupt();
        }
        try {
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            thread.interrupt();
        }
        try {
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
