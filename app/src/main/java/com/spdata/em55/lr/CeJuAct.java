package com.spdata.em55.lr;

import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.spdata.em55.R;
import com.spdata.em55.base.BaseAct;
import com.spdata.em55.lr.view.WaitingBar;
import com.spdata.em55.px.pasm.utils.DataConversionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brxu on 2017/1/11.
 */

public class CeJuAct extends BaseAct implements View.OnClickListener {

    //控制机发：自动    AT3#
//    byte[] cmd_repetition = new byte[]{0x41, 0x54, 0x33, 0x23};
    //ATX#    停止

    //发送命令 单次测距：    AT1#
    byte[] cmd_single = new byte[]{0x41, 0x54, 0x31, 0x23};

    //发送命令 连续测距：    AT3#+AT1#
    byte[] cmd_repetition = new byte[]{0x41, 0x54, 0x33, 0x23, 0x41, 0x54, 0x31, 0x23};

    // 停止测距 ATX#
    byte[] cmd_stop = new byte[]{0x41, 0x54, 0x58, 0x23};

    byte[] cmd_stop_sigle = new byte[]{0x41, 0x54, 0x31, 0x23, 0x41, 0x54, 0x58, 0x23};

    byte[] send = new byte[]{0x41, 0x54, 0x47, 0x23};//ATG# 初始化设备


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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ceju);
        initUI();
        initDevice();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
                isFirst = true;
                mSerialPort.clearPortBuf(fd);
                mSerialPort.WriteSerialByte(fd, cmd_single);//发送测距命令
            }
        });
        btnAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开始连续测距
                    isSecond = true;
                    mSerialPort.clearPortBuf(fd);
                    mSerialPort.WriteSerialByte(fd, cmd_repetition);

//                    mSerialPort.WriteSerialByte(fd, cmd_single);
                    tvStatus.setText("接收数据中……");
                    bar.setVisibility(View.VISIBLE);

                } else {
                    //Stop
                    isThird = true;
                    mSerialPort.clearPortBuf(fd);
                    mSerialPort.WriteSerialByte(fd, cmd_stop);
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
            for (String result : results) {
                String temp = edvRecord.getText().toString();
                play(2, 0);
                if (Arrays.equals(data, ee)) {
                    edvRecord.setText(result+"err\n");
                }
                if (temp.length() > 10) {
                    edvRecord.setText(result + "M\n");

                } else {
                    edvRecord.append(result + "M\n");
                }
                tvResult.setText(result + "M");
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
    private boolean isFirst = false;
    boolean isThird = false;
    boolean isSecond = false;
    boolean isFourthly = true;
    byte aa[] = {65, 84, 49, 35};//AT1#
    byte bb[] = {65, 84, 51, 35};//AT3#
    byte cc[] = {65, 84, 88, 35};//atx#
    byte dd[] = {65, 84, 51, 35, 65, 84, 49, 35};
    byte ee[] = {65, 84, 69, 35};//ate# jiq机器发生错误
    byte ff[] = {65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71,
            35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71,
            35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71,
            35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71,
            35, 65, 84, 71, 35, 65, 84, 71, 35, 65, 84, 71, 35};//atG#  仪器上电复位后,会发ATG# 19次，请上位机收到ATG# 后，才能认为数据有效

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CeJuAct Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


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
                        String log = "";
                        for (byte x : bytes) {
                            log += String.format("0x%x", x);
                        }
                        Log.d(TAG, "Read_length=" + log);
                        if (isFourthly) {
                            if (!Arrays.equals(bytes, ff)) {
                                for (int i = 0; i < 19; i++) {

                                    mSerialPort.WriteSerialByte(fd, send);
                                }
                            } else {
                                 /**/
                                isFourthly = false;
                            }
                        }

                        if (isFirst) {
                            if (Arrays.equals(bytes, aa)) {
                                isFirst = false;
                            } else {
                                mSerialPort.WriteSerialByte(fd, cmd_single);//发送测距命令
                            }
                        }
                        if (isSecond) {
                            if (Arrays.equals(bytes, dd)) {
                                isSecond = false;
                            } else {
                                Log.d(TAG, "zidong=");
                                mSerialPort.clearPortBuf(fd);
                                mSerialPort.WriteSerialByte(fd, cmd_repetition);
                            }
                        }
                        if (isThird) {
                            if (Arrays.equals(bytes, cc)) {
                                isThird = false;
                            } else {
                                SystemClock.sleep(50);
                                mSerialPort.clearPortBuf(fd);
                                mSerialPort.WriteSerialByte(fd, cmd_stop);
                                Log.d(TAG, "stop=");
                            }
                        }

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
