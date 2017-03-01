package com.spdata.em55.px.psam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.spdata.em55.R;
import com.spdata.em55.base.BaseAct;

import java.io.IOException;
import java.util.Timer;

import speedatacom.a3310libs.PsamManager;
import speedatacom.a3310libs.inf.IPsam;

import static speedatacom.a3310libs.realize.Psam3310Realize.POWER_ACTION;
import static speedatacom.a3310libs.realize.Psam3310Realize.POWER_RESULT;


public class PsamAct extends BaseAct implements View.OnClickListener {

    private Button btnPsam1, btnPsam2, btnGetRamdon, btnSendAdpu, btnClear;
    private EditText edvADPU;
    private TextView tvShowData;
    private SerialPort mSerialPort;
    private int fd;
    private int psamflag = 0;
    private DeviceControl mDeviceControl;
    private DeviceControl mDeviceControl2;
    private Context mContext;
    private Timer timer;
    private Button btnReSet;
    private Button btnPower;
    private TextView tvVerson;
    private String send_data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pasm);
        mContext = this;
        initUI();
        PowerOpenDev();
        //接受软上电
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(POWER_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initUI() {
        btnPsam1 = (Button) findViewById(R.id.btn1_active);
        btnPsam2 = (Button) findViewById(R.id.btn2_active);
        btnGetRamdon = (Button) findViewById(R.id.btn_get_ramdon);
        btnSendAdpu = (Button) findViewById(R.id.btn_send_adpu);
        btnReSet = (Button) findViewById(R.id.btn_reset);
        btnPower = (Button) findViewById(R.id.btn_power);
        btnClear = (Button) findViewById(R.id.btn_clear);
        tvVerson = (TextView) findViewById(R.id.tv_verson);
        btnPower.setOnClickListener(this);
        btnPsam1.setOnClickListener(this);
        btnPsam2.setOnClickListener(this);
        btnGetRamdon.setOnClickListener(this);
        btnSendAdpu.setOnClickListener(this);
        btnReSet.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        tvShowData = (TextView) findViewById(R.id.tv_show_message);
        tvShowData.setMovementMethod(ScrollingMovementMethod.getInstance());
        edvADPU = (EditText) findViewById(R.id.edv_adpu_cmd);
        edvADPU.setText("0084000008");
        //pin = { 0x00, 0x20, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00 };
        edvADPU.setText("0020000003");
        edvADPU.setText("80f0800008122a31632a3bafe0");
        edvADPU.setText("00A404000BA000000003454E45524759");
//        edvADPU.setText("80f002 00 01 02");
    }

    private void PowerOpenDev() {
        SystemClock.sleep(100);
        initDevice();
    }

    IPsam psam = PsamManager.getPsamIntance();
    boolean result;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(POWER_ACTION)) {
                result = intent.getBooleanExtra(POWER_RESULT, false);
                tvShowData.setText("Psam activite： " + result + "\n");
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btnPsam1) {
            psamflag = 1;
            psam.PsamPower(IPsam.PowerType.Psam1);
        } else if (v == btnPsam2) {
            psamflag = 2;
            psam.PsamPower(IPsam.PowerType.Psam2);
        } else if (v == btnGetRamdon) {
            if (psamflag == 1) {
                int len = psam.sendData(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                        0x08}, IPsam
                        .PowerType.Psam1);
                if (len >= 0) {
                    tvShowData.setText("Psam1 Send data：00 84 00 00 08\n");
                } else {
                    tvShowData.setText("Psam1 Send data failed\n");
                }
            } else if (psamflag == 2) {
                int len = psam.sendData(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                        0x08}, IPsam
                        .PowerType.Psam2);
                if (len >= 0) {
                    tvShowData.setText("Psam2 Send data：00 84 00 00 08\n");
                } else {
                    tvShowData.setText("Psam2 Send data failed\n");
                }
            }
        } else if (v == btnSendAdpu) {
            String temp_cmd = edvADPU.getText().toString();
            if ("".equals(temp_cmd) || temp_cmd.length() % 2 > 0 || temp_cmd.length() < 4) {
                Toast.makeText(mContext, "Please enter a valid instruction！", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            send_data = temp_cmd;
            if (psamflag == 1) {
                int len = psam.sendData(com.speedata.libutils.DataConversionUtils
                        .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam1);
                if (len >= 0)
                    tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
                else
                    tvShowData.setText("Psam1 Send data：failed");
            } else if (psamflag == 2) {
                int len = psam.sendData(com.speedata.libutils.DataConversionUtils
                        .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam2);
                if (len >= 0)
                    tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
                else
                    tvShowData.setText("Psam2 Send data：failed");
            }
        } else if (v == btnClear) {
            tvShowData.setText("");
        } else if (v == btnReSet) {
            psam.resetDev(DeviceControl.PowerType.EXPAND, 1);
            Toast.makeText(PsamAct.this,"reset ok",Toast.LENGTH_SHORT).show();
        } else if (v == btnPower) {
            PowerOpenDev();
        }
    }

    private int baurate = 115200;
    private String serialport = "ttyMT2";

    private void initDevice() {
        psam.initDev(serialport, baurate, DeviceControl.PowerType.MAIN_AND_EXPAND,
                this, 88, 2);
        psam.startReadThread(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            psam.stopReadThread();
            psam.releaseDev();
            unregisterReceiver(broadcastReceiver);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] temp_cmd = (byte[]) msg.obj;
            tvShowData.append("rece data:" + com.speedata.libutils.DataConversionUtils.byteArrayToString(temp_cmd)+"\n");
        }
    };
}
