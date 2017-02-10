package com.spdata;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.spdata.em55.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {
    EditText eee;
    private DeviceControl control;
    private SerialPort mSerialPort;
    int fd;
    //发送命令 单次测距：    AT1#
    byte[] cmd_single = new byte[]{0x41, 0x54, 0x31, 0x23};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initDevice();
        startTask();
        if (rea==null){
            rea= new Read();
            rea.start();
        }
    }

    private void initDevice() {
        eee = (EditText) findViewById(R.id.eee);
        try {
            mSerialPort = new SerialPort();
            mSerialPort.OpenSerial(SerialPort.SERIAL_TTYMT1, 9600);//kt55串口
            control = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 73, 0);
            control.PowerOnDevice();
            fd = mSerialPort.getFd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            String log = "";
            for (byte x : data) {
                log += String.format("0x%x", x);
            }
            if (data.length>4){
                eee.setTextColor(Color.BLUE);
                eee.append(log);
            }else {
                eee.setTextColor(Color.RED);
                eee.append(log);
            }


        }
    };
    Timer timer = null;

    private void startTask() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mSerialPort.WriteSerialByte(fd, cmd_single);
//                    SystemClock.sleep(500);
//                    try {
//                        byte[] bytes = mSerialPort.ReadSerial(mSerialPort.getFd(), 1024);
//                        if (bytes != null) {
//                            String log = "";
//                            for (byte x : bytes) {
//                                log += String.format("0x%x", x);
//                            }
//                            Log.e("read_cmd", "run: " + log);
//                            Message msg = new Message();
//                            msg.obj = bytes;
//                            handler.sendMessage(msg);
//                        }
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                }
            }, 0, 1000);
        }
    }

    Read rea = null;

    /**
     * 读串口线程
     */
    private class Read extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    SystemClock.sleep(200);
                    byte[] bytes = mSerialPort.ReadSerial(mSerialPort.getFd(), 2048);
                    if (bytes != null) {
                        String log = "";
                        for (byte x : bytes) {
                            log += String.format("0x%x", x);
                        }
                        Log.e("read_cmd", "Read_length=" + log);
                        Message msg = new Message();
                        msg.obj = bytes;
                        handler.sendMessage(msg);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rea != null) {
            rea.interrupt();
            rea = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
