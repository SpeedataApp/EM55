package com.spdata.em55.px.pasm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.spdata.em55.px.pasm.utils.DataConversionUtils;
import com.spdata.em55.px.pasm.utils.MyLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;


public class PasmAct extends Activity implements View.OnClickListener {

    private Button btn1Activite, btn2Activite, btnGetRomdan, btnSendAdpu, btnClear;
    private EditText edvADPU;
    private TextView tvShowData;
    private SerialPort mSerialPort;
    private int fd;
    private int psamflag = 0;
    private DeviceControl mDeviceControl;
    private DeviceControl mDeviceControl2;
    private MyLogger logger = MyLogger.jLog();
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
        mSerialPort = new SerialPort();
        try {
            mSerialPort.OpenSerial(SerialPort.SERIAL_TTYMT2,115200);
            fd = mSerialPort.getFd();
            logger.d("--onCreate--open-serial=" + fd);
        } catch (SecurityException e) {
            Toast.makeText(this, "No serial port authority, forced exit!", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            Toast.makeText(this, "The serial port is not found, forced exit！", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
            System.exit(0);
        }
        mContext = this;
        initUI();

    }

    private void startTask() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new ReadTask(), 1000, 300);
        }
    }

    private SharedPreferences sharedPreferences;

    private void initUI() {
        setContentView(R.layout.act_pasm);
        btn1Activite = (Button) findViewById(R.id.btn1_active);
        btn2Activite = (Button) findViewById(R.id.btn2_active);
        btnGetRomdan = (Button) findViewById(R.id.btn_get_ramdon);
        btnSendAdpu = (Button) findViewById(R.id.btn_send_adpu);
        btnReSet = (Button) findViewById(R.id.btn_reset);
        btnPower = (Button) findViewById(R.id.btn_power);
        btnClear = (Button) findViewById(R.id.btn_clear);
        tvVerson = (TextView) findViewById(R.id.tv_verson);

        btnPower.setOnClickListener(this);
        btn1Activite.setOnClickListener(this);
        btn2Activite.setOnClickListener(this);
        btnGetRomdan.setOnClickListener(this);
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
        try {
            mDeviceControl2 = new DeviceControl(DeviceControl.PowerType.EXPAND,6);
            mDeviceControl=new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND,88,2);
            mDeviceControl.PowerOnDevice();
            mDeviceControl2.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
            mDeviceControl = null;
            return;
        }
        startTask();
    }
    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号错误";
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn1Activite) {//pasm1
            psamflag = 1;
            mSerialPort.WriteSerialByte(fd, getPowerCmd());
            send_data = DataConversionUtils.byteArrayToString(getPowerCmd());
            if (psamflag == 1)
                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
            else if (psamflag == 2)
                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
        } else if (v == btn2Activite) {//pasm2
            psamflag = 2;
            mSerialPort.WriteSerialByte(fd, getPowerCmd());
            send_data = DataConversionUtils.byteArrayToString(getPowerCmd());
            if (psamflag == 1)
                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
            else if (psamflag == 2)
                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
        } else if (v == btnGetRomdan) {
            mSerialPort.WriteSerialByte(fd, getRomdan());
            send_data = DataConversionUtils.byteArrayToString(getRomdan());
            if (psamflag == 1)
                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
            else if (psamflag == 2)
                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
        } else if (v == btnSendAdpu) {
            String temp_cmd = edvADPU.getText().toString();
            if ("".equals(temp_cmd) || temp_cmd.length() % 2 > 0 || temp_cmd.length() < 4) {
                Toast.makeText(mContext, "Please enter a valid instruction！", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            mSerialPort.WriteSerialByte(fd, adpuPackage(DataConversionUtils.HexString2Bytes
                    (temp_cmd)));
            send_data = temp_cmd;
            if (psamflag == 1)
                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
            else if (psamflag == 2)
                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
        } else if (v == btnClear) {
            tvShowData.setText("");
        } else if (v == btnReSet) {
            //复位
            try {
                DeviceControl  mDeviceControl = new DeviceControl(DeviceControl.PowerType.EXPAND,1);
                mDeviceControl.PowerOnDevice();
                mDeviceControl.PowerOffDevice();
                mDeviceControl.PowerOnDevice();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "reset failed", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(mContext, "reset ok", Toast.LENGTH_SHORT).show();
        } else if (v == btnPower) {
//            Toast.makeText(mContext,"power",Toast.LENGTH_SHORT).show();
            PowerOpenDev();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseDev();
        stopTask();
    }

    private void stopTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void releaseDev() {
        stopTask();
        mSerialPort.CloseSerial(fd);
        mSerialPort = null;
        try {
            mDeviceControl.PowerOffDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDeviceControl = null;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] temp_cmd = (byte[]) msg.obj;
            byte[] byte_len = new byte[2];
            byte_len[0] = temp_cmd[3];
            byte_len[1] = temp_cmd[2];
            int len = DataConversionUtils.byteArrayToInt(byte_len);
            if (len <= 6) {
                tvShowData.append("error:" + DataConversionUtils.byteArrayToString(temp_cmd));
                return;
            }
            byte[] temp_data = new byte[len - 6];
            logger.d("---len=" + len + temp_cmd[0] + "  " + temp_cmd[1] + "  " + temp_cmd.length);
            String data;// = DataConversionUtils.byteArrayToString(temp_cmd);

            tvShowData.append("Rx data：\n" + DataConversionUtils.byteArrayToString(temp_cmd) +
                    "\n\n");
//            tvShowData.append("命令头：" + DataConversionUtils.byteArrayToString(new
//                    byte[]{temp_cmd[0], temp_cmd[1]}) + "\n");
//            tvShowData.append("长度字：" + DataConversionUtils.byteArrayToString(new
//                    byte[]{temp_cmd[2], temp_cmd[3]}) + "-->" + DataConversionUtils
//                    .byteArrayToInt(new byte[]{temp_cmd[3], temp_cmd[2]}) + "\n");
//            tvShowData.append("设备标识：" + DataConversionUtils.byteArrayToString(new
//                    byte[]{temp_cmd[4], temp_cmd[5]}) + "\n");
//            tvShowData.append("命令码：" + DataConversionUtils.byteArrayToString(new
//                    byte[]{temp_cmd[6], temp_cmd[7]}) + "\n");
//            tvShowData.append("状态字：" + DataConversionUtils.byteArrayToString(new
//                    byte[]{temp_cmd[8]}));
//            if (temp_cmd[8] == 0) {
//                tvShowData.append("-->成功\n");
//            }
            tvShowData.append("Unpack data：" + "\n");
            for (int i = 0; i < len - 6; i++) {
                temp_data[i] = temp_cmd[i + 9];
            }
            if (len <= 6) {
                return;
            }
            tvShowData.append(DataConversionUtils.byteArrayToString(temp_data));//读到数据显示
//            String temp = tvShowData.getText().toString();
//            if (temp.length() < 10000) {
//                tvShowData.append(data + "\n");
//            } else {
//                tvShowData.setText(data);
//            }
        }
    };

    /**
     * 读串口
     */
    private class ReadTask extends TimerTask {
        public void run() {
            try {
                byte[] temp1 = mSerialPort.ReadSerial(fd, 1024);
                if (temp1 != null) {
                    logger.d("----read--ok---" + DataConversionUtils.byteArrayToStringLog(temp1,
                            temp1.length));
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = temp1;
                    handler.sendMessage(msg);
                    SystemClock.sleep(1000);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打包adpu指令
     * @param cmd
     * @return
     */
    private byte[] adpuPackage(byte[] cmd) {
        byte[] result = new byte[cmd.length + 9];
        result[0] = (byte) 0xaa;
        result[1] = (byte) 0xbb;
        result[2] = (byte) (cmd.length + 5);
        result[3] = 0x00;
        result[4] = 0x00;
        result[5] = 0x00;
        if (psamflag == 1)
            result[6] = 0x13;
        else if (psamflag == 2)
            result[6] = 0x23;
        result[7] = 0x06;
        result[result.length - 1] = 0x51;
        System.arraycopy(cmd, 0, result, 8, cmd.length);
        return result;
    }

    private byte[] getRomdan() {
        //0084000008
        //aabb 0A00 0000 2306 00A4040012 51
        //aabb 0a00 0000 2306 0084000008 51
        //aabb 0500 0000 1306 0084000008 51
        byte[] cmd = new byte[]{(byte) 0xaa, (byte) 0xbb, 0x0a, 0x00, 0x00, 0x00, 0x13, 0x06,
                0x00, (byte)
                0x84, 0x00, 0x00, 0x08, 0x51};
        if (psamflag == 1)
            cmd[6] = 0x13;
        else if (psamflag == 2)
            cmd[6] = 0x23;

        return cmd;
    }

    private byte[] getPowerCmd() {
        //aabb05000000110651
        //aabb05000000120651
        //  //IC卡复位3V
        // aabb05000000110651
        //IC卡复位5V
        //aabb05000000120651
        byte[] cmd = new byte[]{(byte) 0xaa, (byte) 0xbb, 0x05, 0x00, 0x00, 0x00, 0x11, (byte)
                0x06, 0x51};
        if (psamflag == 1)
            cmd[6] = 0x11;
        else if (psamflag == 2)
            cmd[6] = 0x21;
        return cmd;
    }
}
