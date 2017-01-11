package com.spdata.em55.lr;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spdata.em55.R;
import com.spdata.em55.lr.view.WaitingBar;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CeJuActBack extends Activity implements View.OnClickListener {
    Button btnsend, btnzid, btnclear, btnselect;
    private ImageView imgtop, imgbottom;
    private TextView tvresult, textView2;
    //发送命令 控制机发：    AT1#
    byte[] senf_control = new byte[]{0x41, 0x54, 0x31, 0x23};
    //控制机发：自动    AT3#
    byte[] send_more_on = new byte[]{0x41, 0x54, 0x33, 0x23};
    //AT+#     开声音
    byte[] send_sound_on = new byte[]{0x41, 0x54, 0x2B, 0x23};
    //AT-#     关声音
    byte[] send_sound_down = new byte[]{0x41, 0x54, 0x2D, 0x23};
    //AT9#    不自动关机控制
    byte[] send_more_down = new byte[]{0x41, 0x54, 0x39, 0x23};
    //ATX#    停止
    byte[] send_down = new byte[]{0x41, 0x54, 0x58, 0x23};
    byte[] send = new byte[]{0x41, 0x54, 0x57, 0x23};
    private DeviceControl control;
    private SerialPort serialPort;
    private int fd;
    private boolean iszid = true, istop = true;
    private Timer timer;
    private ReadTimerTask readTimerTask;
    private static final int TIME_TO_READDATA = 500;
    private EditText editText;
    private byte[] newtemp;//距离数组
    private byte[] newtemp2;//校验字节数组
    float results = 0;
    private SoundPool sp; //声音池
    private Map<Integer, Integer> mapSRC;
    private ReadThreads readThreads;
    private final String TAG = "RedDATA";
    private WaitingBar bar;
    private byte[] temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ceju);
    }

    private class ReadTimerTask extends TimerTask {
        @Override
        public void run() {
            play(2, 0);
        }
    }

    @Override
    protected void onResume() {
        intit();
        initSoundPool();//初始化声音池
        super.onResume();
    }

    private void intit() {
        btnsend = (Button) findViewById(R.id.btn_send);
        btnzid = (Button) findViewById(R.id.btn_zid);
        btnclear = (Button) findViewById(R.id.btn_clear);
        editText = (EditText) findViewById(R.id.edv_age);
        imgbottom = (ImageView) findViewById(R.id.img_phone_b);
        imgtop = (ImageView) findViewById(R.id.imageView2);
        tvresult = (TextView) findViewById(R.id.tv_result);
        btnselect = (Button) findViewById(R.id.btn_select);
        bar = (WaitingBar) findViewById(R.id.waitingBar);
        textView2 = (TextView) findViewById(R.id.textView2);
        bar.setVisibility(View.GONE);
        btnselect.setOnClickListener(this);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                textView2.setText("接收数据中……");
//                bar.setVisibility(View.VISIBLE);
                serialPort.WriteSerialByte(fd, send);
                serialPort.WriteSerialByte(fd, senf_control);//发送测距命令
                play(2, 0);
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
        });
//        btnSingle.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    textView2.setText("接收数据中……");
//                    bar.setVisibility(View.VISIBLE);
//                    serialPort.WriteSerialByte(fd, send);
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    serialPort.WriteSerialByte(fd, cmd_single);//发送测距命令
//                    play(2, 0);
////                    readThreads = new ReadThreads();
////                    readThreads.start();//执行读串口
//                } else {
//                    readThreads.interrupt();
//
//                    textView2.setText("接收数据模式");
//                    bar.setVisibility(View.GONE);
//                }
//
//                return false;
//            }
//        });
        btnzid.setOnClickListener(this);
        btnclear.setOnClickListener(this);
        try {
            serialPort = new SerialPort();
            serialPort.OpenSerial(SerialPort.SERIAL_TTYMT1, 9600);//kt55串口
            control = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 73, 0);
            control.PowerOnDevice();
            fd = serialPort.getFd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zid:
                if (iszid) {
                    iszid = false;
                    btnzid.setText("停止测距");
                    textView2.setText("接收数据中……");
                    bar.setVisibility(View.VISIBLE);
                    serialPort.WriteSerialByte(fd, send);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serialPort.WriteSerialByte(fd, send_more_on);//发送自动测距命令
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serialPort.WriteSerialByte(fd, senf_control);

                    timer = new Timer();
                    readTimerTask = new ReadTimerTask();
                    timer.schedule(readTimerTask, 0, TIME_TO_READDATA);

                    readThreads = new ReadThreads();
                    readThreads.start();
                } else {
                    iszid = true;
                    btnzid.setText("自动测距");
                    serialPort.WriteSerialByte(fd, send_down);//再发一次停止
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serialPort.WriteSerialByte(fd, send_down);//再发一次停止
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serialPort.WriteSerialByte(fd, send_down);//再发一次停止
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serialPort.WriteSerialByte(fd, send_down);//再发一次停止
                    finshTime();
                }
                break;
            case R.id.btn_clear:
                editText.setText("");
                tvresult.setText("0.00 M ");
                break;
            case R.id.btn_select:
                if (istop) {
                    istop = false;
                    Toast.makeText(this, "底部开始计算", Toast.LENGTH_SHORT).show();
                    imgtop.setVisibility(View.GONE);
                    imgbottom.setVisibility(View.VISIBLE);
                } else {
                    istop = true;
                    Toast.makeText(this, "顶部开始计算", Toast.LENGTH_SHORT).show();
                    imgtop.setVisibility(View.VISIBLE);
                    imgbottom.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void finshTime() {
        textView2.setText("接收数据模式");
        bar.setVisibility(View.GONE);
        timer.cancel();
        readTimerTask.cancel();
        readThreads.interrupt();
    }

    android.os.Handler handler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            temp = (byte[]) msg.obj;
            Log.d(TAG, "handleMessage: ");
            newtemp = new byte[3];
            newtemp2 = new byte[1];
            if (temp.length >= 8) {
                System.arraycopy(temp, 3, newtemp, 0, 3);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                System.arraycopy(temp, 6, newtemp2, 0, 1);
            } else {
                String log="";
                for (byte x : temp) {
                    log+=String.format("0x%x", x);
                }
                Log.d(TAG, "erroR" + temp.length + "");
                Log.d(TAG, "length<8="+log);
                return;
            }
            receiveData();

        }
    };

    private void receiveData() {
        int number = byteArrayToInt(newtemp);
        if (istop) {
            results = (float) ((number - 650) / 10000.0);//从顶部开始计算
        } else {
            results = (float) ((number + 1000) / 10000.0);//从底部开始计算
        }
        //四舍五入
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String dff = df.format(results);
        if (result()) {
            editText.append(dff + "M\n");
            tvresult.setText(dff + "M");
//            Log.d(TAG, "edvRece=="+tvresult );
            Log.d(TAG, "ok  edvRece="+editText.getText().toString() );
            Log.d(TAG, "read ok"+tvresult.toString() );
        }else {
            String log="";
            for (byte x : temp) {
                log+=String.format("0x%x", x);
            }
            Log.d(TAG, log);
        }
    }

    private boolean result() {//校验数据
        boolean isresult = false;
        byte julinum = (byte) (newtemp[0] + newtemp[1] + newtemp[2]);
        if (julinum == newtemp2[0]) {
            isresult = true;
        }
        return isresult;
    }

    private class ReadThreads extends Thread {
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


    @Override
    protected void onDestroy() {
        try {
            control.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serialPort.CloseSerial(fd);
        if (sp != null) {
            sp.release();
        }
        if (readThreads != null) {
            readThreads.interrupt();
        }
        super.onDestroy();
    }

    //初始化声音池
    private void initSoundPool() {
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mapSRC = new HashMap<Integer, Integer>();
        mapSRC.put(2, sp.load(this, R.raw.welcome, 0));
    }

    /**
     * 播放声音池的声音
     */
    private void play(int sound, int number) {
        sp.play(mapSRC.get(sound),//播放的声音资源
                1.0f,//左声道，范围为0--1.0
                1.0f,//右声道，范围为0--1.0
                0, //优先级，0为最低优先级
                number,//循环次数,0为不循环
                0);//播放速率，0为正常速率
    }

    /**
     * byte[]转int
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < bytes.length; i++) {
            int shift = (bytes.length - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }
}
