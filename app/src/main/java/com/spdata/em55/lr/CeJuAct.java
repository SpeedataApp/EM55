package com.spdata.em55.lr;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spdata.em55.R;
import com.spdata.em55.lr.view.WaitingBar;
import com.spdata.em55.px.pasm.utils.DataConversionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brxu on 2017/1/11.
 */

public class CeJuAct extends Activity implements View.OnClickListener {

    //控制机发：自动    AT3#
//    byte[] send_more_on = new byte[]{0x41, 0x54, 0x33, 0x23};
    //ATX#    停止

    //发送命令 单次测距：    AT1#
    byte[] cmd_single = new byte[]{0x41, 0x54, 0x31, 0x23};

    //发送命令 连续测距：    AT3#AT1#
    byte[] cmd_repetition = new byte[]{0x41, 0x54, 0x33, 0x23, 0x41, 0x54, 0x31, 0x23};

    // 停止测距 ATX#
    byte[] cmd_stop = new byte[]{0x41, 0x54, 0x58, 0x23};

    byte[] cmd_stop_sigle = new byte[]{0x41, 0x54, 0x31, 0x23,0x41, 0x54, 0x58, 0x23};

    byte[] send = new byte[]{0x41, 0x54, 0x57, 0x23};//ATW#


    Button btnSingle, btnClear, btnSwitch;
    private ToggleButton btnAuto;
    private ImageView imgtop, imgbottom;
    private TextView tvResult, tvStatus;

    private DeviceControl control;
    private SerialPort mSerialPort;
    private int fd;
    private boolean isTop = true;
    private EditText edvRecord;
    float results = 0;
    private SoundPool sp; //声音池

    private final String TAG = "RedDATA";
    private WaitingBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ceju);
        initUI();
        initDevice();
    }

    private void initUI() {
        btnSingle = (Button) findViewById(R.id.btn_send);
        btnAuto = (ToggleButton) findViewById(R.id.btn_zid);
        btnClear = (Button) findViewById(R.id.btn_clear);
        edvRecord = (EditText) findViewById(R.id.edv_age);
        imgbottom = (ImageView) findViewById(R.id.img_phone_b);
        imgtop = (ImageView) findViewById(R.id.imageView2);
        tvResult = (TextView) findViewById(R.id.tv_result);
        btnSwitch = (Button) findViewById(R.id.btn_select);
        bar = (WaitingBar) findViewById(R.id.waitingBar);
        tvStatus = (TextView) findViewById(R.id.textView2);
        bar.setVisibility(View.GONE);
        btnSwitch.setOnClickListener(this);
        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mSerialPort.WriteSerialByte(fd, send);//
                mSerialPort.WriteSerialByte(fd, cmd_single);//发送测距命令
//                play(2, 0);
            }
        });
        btnAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开始连续测距
//                    mSerialPort.WriteSerialByte(fd, cmd_repetition);
                    tvStatus.setText("接收数据中……");
                    bar.setVisibility(View.VISIBLE);
                } else {
                    //Stop
//                    mSerialPort.WriteSerialByte(fd, cmd_stop);
//                    mSerialPort.WriteSerialByte(fd, cmd_stop);
//                    mSerialPort.WriteSerialByte(fd, cmd_stop);
//                    mSerialPort.WriteSerialByte(fd, cmd_stop);
                    bar.setVisibility(View.INVISIBLE);
                    tvStatus.setText("");
                }
            }
        });
        btnClear.setOnClickListener(this);
    }

    private void initDevice() {
        try {
            mSerialPort = new SerialPort();
            mSerialPort.OpenSerial(SerialPort.SERIAL_TTYMT1, 9600);//kt55串口
            control = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 73, 0);
            control.PowerOnDevice();
            fd = mSerialPort.getFd();
            if (readSerialThread == null) {
                readSerialThread = new ReadSerialThread();
            }
            readSerialThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initSoundPool();
    }

    /**
     * 数据处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            List<String> results = parseData(data);
//            if (!btnAuto.isChecked()) {
//                Log.e(TAG, "====cmd_stop error");
//                mSerialPort.WriteSerialByte(fd, cmd_stop_sigle);
//            }
            for (String result : results) {
                String temp = edvRecord.getText().toString();
                play(2, 0);
                if (temp.length() > 5) {
                    edvRecord.setText(result + "\n");

                } else {
                    edvRecord.append(result + "\n");
                }
                tvResult.setText(result);
            }
        }
    };

    /**
     * 计算长度
     *
     * @param data
     * @return
     */
    private String convertValue(byte[] data) {
        int number = byteArrayToInt(data);
        if (isTop) {
            results = (float) ((number - 650) / 10000.0);//从顶部开始计算
        } else {
            results = (float) ((number + 1000) / 10000.0);//从底部开始计算
        }
        //四舍五入
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String dff = df.format(results);
        return dff;
    }

    /**
     * 解析串口原始数据
     *
     * @param data
     * @return
     */
    private List<String> parseData(byte[] data) {
        List<String> result = new ArrayList<>();
        if (data.length < 8) {
            Log.e(TAG, "====parseData len error" + DataConversionUtils.byteArrayToStringLog(data,
                    data.length));
            return result;
        }
        for (int i = 0; i < data.length; i++) {
            try {
                if ((byte) data[i] == 0x041 && (byte) data[i + 7] == 0x023) {
                    byte[] temp = new byte[8];

                    System.arraycopy(data, i, temp, 0, 8);
                    //加法和
                    byte sum = (byte) (temp[3] + temp[4] + temp[5]);
                    byte[] values = new byte[3];
                    System.arraycopy(temp, 3, values, 0, 3);
                    //判断校验
                    if (sum == temp[6]) {
                        String object = convertValue(values);
                        result.add(object);
                        Log.d(TAG, "====parseData add" + object);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    ReadSerialThread readSerialThread = new ReadSerialThread();

    private int nodataCount = 0;

    /**
     * 读串口线程
     */
    private class ReadSerialThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] bytes = mSerialPort.ReadSerial(mSerialPort.getFd(), 1024);
                    if (bytes != null) {
                        Message msg = new Message();
                        msg.obj = bytes;
                        handler.sendMessage(msg);
                    }
//                    else if (bytes == null && btnAuto.isChecked()) {
//                        //连续测量后  没有收到数据  需再次发连续测量指令
//                        Log.e(TAG, "====cmd_repetition error");
//                        nodataCount++;
//                        if (nodataCount >= 3) {
//                            nodataCount = 0;
//                            mSerialPort.WriteSerialByte(fd, cmd_repetition);
//                            Log.e(TAG, "====cmd_repetition resend");
//                        }
//                    }
                    if(btnAuto.isChecked()){
                        mSerialPort.WriteSerialByte(fd, cmd_single);
                    }
                    SystemClock.sleep(50);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (readSerialThread != null) {
                readSerialThread.interrupt();
                readSerialThread = null;
            }
            mSerialPort.CloseSerial(fd);
            control.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sp != null) {
            sp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                edvRecord.setText("");
                tvResult.setText("0.00 M ");
                break;
            case R.id.btn_select:
                if (isTop) {
                    isTop = false;
                    Toast.makeText(this, "底部开始计算", Toast.LENGTH_SHORT).show();
                    imgtop.setVisibility(View.GONE);
                    imgbottom.setVisibility(View.VISIBLE);
                } else {
                    isTop = true;
                    Toast.makeText(this, "顶部开始计算", Toast.LENGTH_SHORT).show();
                    imgtop.setVisibility(View.VISIBLE);
                    imgbottom.setVisibility(View.GONE);
                }
                break;
        }
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

    private Map<Integer, Integer> mapSRC;

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
}
