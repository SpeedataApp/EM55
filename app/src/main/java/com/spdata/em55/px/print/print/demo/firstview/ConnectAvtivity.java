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
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.ReadBean;

import java.io.IOException;
import java.util.List;


public class ConnectAvtivity extends BaseAct {

    public int state;
    public Button con;
    public Spinner com;
    public TextView version;
    public ApplicationContext context;
    public boolean mBconnect = false;
    public static ConnectAvtivity mActivity;

    private ReadBean mRead;
    private DeviceControl deviceControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.getInstance().addActivity(ConnectAvtivity.this);
        setContentView(R.layout.activity_connect);
        initUi();
        connect();
    }

    private void initUi() {
        mActivity = this;
        // 多个页面之间数据共享
        context = (ApplicationContext) getApplicationContext();
        context.setObject();
        version = (TextView) findViewById(R.id.text_version);
        version.setText("V " + context.getObject().CON_QueryVersion());
    }


    public void connect() {

        // 读取配置文件
        modelJudgmen();
        if (mBconnect) {
            context.getObject().CON_CloseDevices(context.getState());

            con.setText(R.string.button_btcon);// "连接"
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
        boolean configFileExists = ConfigUtils.isConfigFileExists();
        mRead = ConfigUtils.readConfig(this);
        ReadBean.PrintBean print = mRead.getPrint();
        String powerType = print.getPowerType();
        int braut = print.getBraut();
        List<Integer> gpio = print.getGpio();
        String serialPort = print.getSerialPort();
        state = context.getObject().CON_ConnectDevices("RG-E487",
                serialPort + ":" + braut + ":1:1", 200);
        int[] intArray = new int[gpio.size()];
        String intArrayStr="";
        for (int i = 0; i < gpio.size(); i++) {
            intArray[i] = gpio.get(i);
            intArrayStr=intArrayStr+intArray[i]+" ";
        }
        try {
            deviceControl = new DeviceControl(powerType,intArray);
//            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 73,4);
            deviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedXmlUtil.getInstance(this).write("PrintConfig",
                "配置文件："+configFileExists+";"+serialPort+";"+braut+";"+intArrayStr);
    }

    // 程序退出时需要关闭电源 省电
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
