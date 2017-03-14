package com.spdata.em55.px.print.print.demo.firstview;

import android.content.Intent;
import android.os.Bundle;
import android.serialport.DeviceControl;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spdata.em55.R;
import com.spdata.em55.base.BaseAct;
import com.spdata.em55.px.print.print.demo.secondview.PrintModeActivity;
import com.spdata.em55.px.print.utils.ApplicationContext;

import java.io.IOException;


public class ConnectAvtivity extends BaseAct {

    public int state;
    public Button con;
    public Spinner com;
    public TextView version;
    public ApplicationContext context;
    public boolean mBconnect = false;
    public static ConnectAvtivity mActivity;
    private DeviceControl devCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        modelJudgmen();
    }

    public void initUI() {
        setContentView(R.layout.activity_connect);
        mActivity = this;
        context = (ApplicationContext) getApplicationContext();
        context.setObject();
    }

    public void connect() {
        modelJudgmen();
        if (mBconnect) {
            context.getObject().CON_CloseDevices(context.getState());
            con.setText(R.string.button_btcon);
            mBconnect = false;
        } else {
            System.out.println("----RG---CON_ConnectDevices");
            if (state > 0) {
                Toast.makeText(ConnectAvtivity.this, R.string.mes_consuccess,
                        Toast.LENGTH_SHORT).show();

                mBconnect = true;
                Intent intent = new Intent(ConnectAvtivity.this,
                        PrintModeActivity.class);
                context.setState(state);
                context.setName("RG-E48");
                startActivity(intent);
            } else {
                Toast.makeText(ConnectAvtivity.this, R.string.mes_confail,
                        Toast.LENGTH_SHORT).show();
                mBconnect = false;
            }
        }
    }
    private void modelJudgmen() {
        state = context.getObject().CON_ConnectDevices("RG-E487",
                "/dev/ttyMT1:115200", 200);
        Toast.makeText(
                this,
                "" + android.os.Build.MODEL + " release:"
                        + android.os.Build.VERSION.RELEASE, Toast.LENGTH_LONG)
                .show();
        try {
            devCtrl = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 73,4);
            devCtrl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            devCtrl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
