package com.spdata.em55.lr;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.spdata.em55.base.BaseAct;
import com.spdata.em55.lr.view.WaitingBar;

import java.io.IOException;
import java.util.List;

import speedatacom.mylibrary.DistanceManage;
import speedatacom.mylibrary.IDistance;


/**
 * Created by brxu on 2017/1/11.
 */

public class DistanceAct extends BaseAct implements View.OnClickListener {
    Button btnSingle, btnClear, btnSwitch;
    private ToggleButton btnAuto;
    private ImageView imgtop, imgbottom;
    private TextView tvResult, tvStatus;
    private boolean isTop = true;
    private EditText edvRecord;

    private final String TAG = "RedDATA";
    private WaitingBar bar;
    IDistance distanceManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ceju);
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDevice();
    }

    private int flag = 0;
    private void initDevice(){
        distanceManage= DistanceManage.getDistanceIntance();
        distanceManage.initDevice(this, SerialPort.SERIAL_TTYMT1, 9600, DeviceControl.PowerType.MAIN_AND_EXPAND, 73, 0);
        distanceManage.startReadThread(handler);
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
                if (flag == 0) {
                    flag = 1;
                    distanceManage.senCmd(IDistance.CmdType.cmdsingle);
                } else if (flag == 1) {
                    distanceManage.senCmd(IDistance.CmdType.cmdsingle);
                    flag = 0;
                }
            }
        });
        btnAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开始连续测距
                    btnSingle.setEnabled(false);
                    tvStatus.setText("接收数据中……");
                    Log.d(TAG, "btn_start=");
                    distanceManage.senCmd(IDistance.CmdType.cmdrepetition);
                    bar.setVisibility(View.VISIBLE);
                } else {
                    //Stop
                    btnSingle.setEnabled(true);
                    bar.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "btn_stop=");
                    distanceManage.senCmd(IDistance.CmdType.cmdstop);
                    tvStatus.setText("");
                }
            }
        });
        btnClear.setOnClickListener(this);
    }
    /**
     * 数据处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            List<String> results = null;
            try {
                results = distanceManage.parseData(data,isTop);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (results==null){
                edvRecord.setText("err\n");
                 btnAuto.setChecked(false);
                return;
            }
            for (String result : results) {
                String temp = edvRecord.getText().toString();
                play(2, 0);
                if (temp.length() > 20) {
                    edvRecord.setText(result + "M\n");

                } else {
                    edvRecord.append(result + "M\n");
                }
                tvResult.setText(result + "M");

            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        try {
            distanceManage.releaseDev();
            distanceManage.stopReadThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
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
}
