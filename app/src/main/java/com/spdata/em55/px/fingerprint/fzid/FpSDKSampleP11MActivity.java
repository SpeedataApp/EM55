package com.spdata.em55.px.fingerprint.fzid;

//------------------------------------------------------------------------------------------

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.IDWORLD.LAPI;
import com.spdata.em55.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

//------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------

public class FpSDKSampleP11MActivity extends Activity {
    //------------------------------------------------------------------------------------------
    private Button btnOpen;
    private Button btnClose;
    private Button btnGetImage;
    private Button btnOnVideo;
    private Button btnGetImageQuality;
    private Button btnCreateTemp;
    private Button btnMatchTemp;
    private TextView tvMessage;
    private TextView tvTemp1;
    private ImageView viewFinger;
    private RadioButton radio1;
    private RadioButton radio2;
    //------------------------------------------------------------------------------------------
    private LAPI m_cLAPI;
    private int m_hDevice = 0;
    private int m_tempNo = 1;
    private byte[] m_image = new byte[LAPI.WIDTH * LAPI.HEIGHT];
    private byte[] m_itemplate_1 = new byte[512];
    private byte[] m_itemplate_2 = new byte[LAPI.FPINFO_STD_MAX_SIZE];
    private boolean bContinue = false;
    //------------------------------------------------------------------------------------------
    public static final int MSG_SHOW_TEXT = 101;
    public static final int MSG_SHOW_IMAGE = 102;
    public static final int MSG_VIEW_TEMPLATE_1 = 103;
    public static final int MSG_VIEW_TEMPLATE_2 = 104;
    public static final int MSG_ID_ENABLED = 403;
    //------------------------------------------------------------------------------------------


    @Override
    protected void onDestroy() {
        CLOSE_DEVICE();
        super.onDestroy();
        try {
            DeviceControl deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 63, 6);
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fzidfinger_layout);

        btnOpen = (Button) findViewById(R.id.btnOpenDevice);
        btnClose = (Button) findViewById(R.id.btnCloseDevice);
        btnGetImage = (Button) findViewById(R.id.btnGetImage);
        btnOnVideo = (Button) findViewById(R.id.btnOnVideo);
        btnGetImageQuality = (Button) findViewById(R.id.btnGetImageQuality);
        btnCreateTemp = (Button) findViewById(R.id.btnCreateTemp);
        btnMatchTemp = (Button) findViewById(R.id.btnMatchTemp);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvTemp1 = (TextView) findViewById(R.id.tvTemp);
        viewFinger = (ImageView) findViewById(R.id.ivImageViewer);
        radio1 = (RadioButton) findViewById(R.id.firstTemp);
        radio2 = (RadioButton) findViewById(R.id.secondTemp);
        EnableAllButtons(true, false);

        m_cLAPI = new LAPI(this);

        btnOpen.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    public void run() {
                        OPEN_DEVICE();
                    }
                };
                Thread s = new Thread(r);
                s.start();
            }
        });

        btnClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    public void run() {
                        CLOSE_DEVICE();
                    }
                };
                Thread s = new Thread(r);
                s.start();
            }
        });

        btnGetImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GET_IMAGE();
            }
        });

        btnOnVideo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (bContinue) {
                    btnOnVideo.setText("Video");
                    bContinue = false;
                    return;
                }
                btnOnVideo.setText("Stop");
                bContinue = true;
                Runnable r = new Runnable() {
                    public void run() {
                        ON_VIDEO();
                    }
                };
                Thread s = new Thread(r);
                s.start();
            }
        });

        btnGetImageQuality.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    public void run() {
                        GET_IMAGE_QUALITY();
                    }
                };
                Thread s = new Thread(r);
                s.start();
            }
        });

        radio1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                m_tempNo = 1;
            }
        });

        radio2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                m_tempNo = 2;
            }
        });

        btnCreateTemp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    public void run() {
                        CREATE_TEMP();
                    }
                };
                Thread s = new Thread(r);
                s.start();
            }
        });

        btnMatchTemp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                COMPARE_TEMPS();
            }
        });

    }

    //------------------------------------------------------------------------------------------
    public int getrwusbdevices() {
        //public static final int VID = 0x0483;
        //public static final int PID = 0x5710;
        // get FileDescriptor by Android USB Host API
        UsbManager mUsbManager = (UsbManager) this
                .getSystemService(Context.USB_SERVICE);
        final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        BroadcastReceiver mUsbReceiver = null;
        this.registerReceiver(mUsbReceiver, filter);
        int fd = -1;
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("1111111",
                    device.getDeviceName() + " "
                            + Integer.toHexString(device.getVendorId()) + " "
                            + Integer.toHexString(device.getProductId()));
            if ((device.getVendorId() == 0x0483)
                    && (0x5710 == device.getProductId())) {
                return 1;
            }

        }
        return 0;
    }


    protected void OPEN_DEVICE() {
        String msg;
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOpenDevice, 0));

        if (getrwusbdevices() != 1) {
            msg = "Find device Is Fail!";
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOpenDevice, 1));
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
            return;
        }
        m_hDevice = m_cLAPI.OpenDeviceEx();
        if (m_hDevice == 0) {
            msg = "Can't open device !";
            EnableAllButtons(true, false);
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOpenDevice, 1));
        } else {
            msg = "OpenDevice() = OK";
            EnableAllButtons(false, true);
        }
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
    }

    //------------------------------------------------------------------------------------------
    protected void CLOSE_DEVICE() {
        String msg;
        if (m_hDevice == 0) return;
        m_cLAPI.CloseDeviceEx(m_hDevice);
        m_hDevice = 0;
//		unregisterReceiver(this);
        msg = "CloseDevice() = OK";
        EnableAllButtons(true, false);
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
    }

    //------------------------------------------------------------------------------------------
    protected void GET_IMAGE() {
        int ret;
        String msg;
        ret = m_cLAPI.GetImage(m_hDevice, m_image);
        if (ret != LAPI.TRUE) msg = "Can't get image !";
        else {
            msg = "GetImage() = OK";
        }
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_IMAGE, LAPI.WIDTH, LAPI.HEIGHT, m_image));
    }

    //------------------------------------------------------------------------------------------
    protected void ON_VIDEO() {
        EnableAllButtons(false, false);
        btnOnVideo.setEnabled(true);
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOnVideo, 1));
        int tempNo = 0;
        int ret, i;
        int score;
        int qr;
        String msg;
        while (bContinue) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (tempNo > 1) tempNo = 0;

            ret = m_cLAPI.GetImage(m_hDevice, m_image);
            if (ret != LAPI.TRUE) {
                msg = "Can't get image !";
                m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
                continue;
            } else {
                msg = "GetImage() = OK";
            }

            ret = m_cLAPI.IsPressFinger(m_hDevice, m_image);
            if (ret == 0) {
                msg = "IsPressFinger() = 0";
                m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
                continue;
            }

            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_IMAGE, LAPI.WIDTH, LAPI.HEIGHT, m_image));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            qr = m_cLAPI.GetImageQuality(m_hDevice, m_image);
            msg = String.format("GetImageQuality() = %d", qr);
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
            if (qr < 50) {
                continue;
            }
            if (tempNo == 1)
                ret = m_cLAPI.CreateTemplate(m_hDevice, m_image, m_itemplate_1);
            else
                ret = m_cLAPI.CreateTemplate(m_hDevice, m_image, m_itemplate_2);
            if (ret == 0) msg = "Can't create template !";
            else msg = String.format("CreateTemplate%d() = OK", tempNo);
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
            msg = "";
            for (i = 0; i < 512; i++) {
                msg += String.format("%02x", m_itemplate_1[i]);
            }
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_VIEW_TEMPLATE_1, 0, 0, msg));

            if (tempNo == 1) {
                score = m_cLAPI.CompareTemplates(m_hDevice, m_itemplate_1, m_itemplate_2);
                msg = String.format("CompareTemplates() = %d", score);
                m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tempNo++;
//			int ret = m_cLAPI.GetImage(m_hDevice, m_image);
//			if (ret != LAPI.TRUE) {
//				m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, "Can't get image !").sendToTarget();
//				bContinue = false;
//				break;
//			}

            //m_fEvent.obtainMessage(MSG_SHOW_IMAGE, LAPI.WIDTH, LAPI.HEIGHT, m_image).sendToTarget();


        }
        EnableAllButtons(false, true);
    }

    //------------------------------------------------------------------------------------------
    protected void GET_IMAGE_QUALITY() {
        int qr;
        String msg = "";
        qr = m_cLAPI.GetImageQuality(m_hDevice, m_image);
        msg = String.format("GetImageQuality() = %d", qr);
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
    }

    //------------------------------------------------------------------------------------------
    protected void CREATE_TEMP() {
        int i, ret;
        String msg;
        ret = m_cLAPI.IsPressFinger(m_hDevice, m_image);
        if (ret == 0) {
            msg = "IsPressFinger() = 0";
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
            return;
        }

        if (m_tempNo == 1)
            ret = m_cLAPI.CreateTemplate(m_hDevice, m_image, m_itemplate_1);
        else
            ret = m_cLAPI.CreateTemplate(m_hDevice, m_image, m_itemplate_2);
        if (ret == 0) msg = "Can't create template !";
        else msg = String.format("CreateTemplate%d() = OK", m_tempNo);
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
        msg = "";

        if (m_tempNo == 1) {
            for (i = 0; i < 512; i++) {
                msg += String.format("%02x", m_itemplate_1[i]);
            }
        } else {
            for (i = 0; i < LAPI.FPINFO_STD_MAX_SIZE; i++) {
                msg += String.format("%02x", m_itemplate_2[i]);
            }
        }
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_VIEW_TEMPLATE_1, 0, 0, msg));
    }

    //------------------------------------------------------------------------------------------
    protected void COMPARE_TEMPS() {
        int score;
        String msg;
        score = m_cLAPI.CompareTemplates(m_hDevice, m_itemplate_1, m_itemplate_2);
        msg = String.format("CompareTemplates() = %d", score);
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_SHOW_TEXT, 0, 0, msg));
    }

    //------------------------------------------------------------------------------------------
    public boolean SaveAsFile(String filename, byte[] buffer, int len) {
        boolean ret = true;
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        File Dir = new File(extStorageDirectory, "Android");
        File file = new File(Dir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer, 0, len);
            out.close();
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    //------------------------------------------------------------------------------------------
    public long LoadAsFile(String filename, byte[] buffer) {
        long ret = 0;
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        File Dir = new File(extStorageDirectory, "Android");
        File file = new File(Dir, filename);
        ret = file.length();
        try {
            FileInputStream out = new FileInputStream(file);
            out.read(buffer);
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

    //------------------------------------------------------------------------------------------
    public void EnableAllButtons(boolean bOpen, boolean bOther) {
        int iOther;
        if (bOpen)
            m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOpenDevice, 1));
        else m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOpenDevice, 0));
        if (bOther) iOther = 1;
        else iOther = 0;
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnCloseDevice, iOther));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnGetImage, iOther));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnOnVideo, iOther));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnGetImageQuality, iOther));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnCreateTemp, iOther));
        m_fEvent.sendMessage(m_fEvent.obtainMessage(MSG_ID_ENABLED, R.id.btnMatchTemp, iOther));
    }

    //------------------------------------------------------------------------------------------
    private final Handler m_fEvent = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_TEXT:
                    tvMessage.setText((String) msg.obj);
                    break;
                case MSG_VIEW_TEMPLATE_1:
                    tvTemp1.setText((String) msg.obj);
                    break;
                case MSG_ID_ENABLED:
                    Button btn = (Button) findViewById(msg.arg1);
                    if (msg.arg2 != 0) btn.setEnabled(true);
                    else btn.setEnabled(false);
                    break;
                case MSG_SHOW_IMAGE:
                    ShowFingerBitmap((byte[]) msg.obj, msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    //------------------------------------------------------------------------------------------
    private void ShowFingerBitmap(byte[] image, int width, int height) {
        if (width == 0) return;
        if (height == 0) return;

        int[] RGBbits = new int[width * height];
        viewFinger.invalidate();
        for (int i = 0; i < width * height; i++) {
            int v;
            if (image != null) v = image[i] & 0xff;
            else v = 0;
            RGBbits[i] = Color.rgb(v, v, v);
        }
        Bitmap bmp = Bitmap.createBitmap(RGBbits, width, height, Config.RGB_565);
        viewFinger.setImageBitmap(bmp);
    }

    /**
     * 将RAW格式的图像转换为bmp格式的图像
     *
     * @param fpRaw
     * @return
     */
    public byte[] Raw2Bmp(byte[] fpRaw) {
        byte[] bmpbuf = new byte[256 * 360 + 1078];
        byte head[] = {
                0x42, 0x4d, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x36, 0x04, 0x00, 0x00, 0x28, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x01, 0x00, 0x08, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        };
        head[18] = 0;
        head[19] = 1;
        head[20] = 0;
        head[21] = 0;

        head[22] = 104;
        head[23] = 1;
        head[24] = 0;
        head[25] = 0;

        int i = 0;
        byte j = 0;
        for (i = 0; i < 54; i++) {
            bmpbuf[i] = head[i];
        }
        for (i = 54; i < 1078; i = i + 4) {
            bmpbuf[i] = bmpbuf[i + 1] = bmpbuf[i + 2] = j;
            bmpbuf[i + 3] = 0;
            j++;
        }
        for (i = 0; i < 256 * 360; i++) {
            bmpbuf[i + 1078] = fpRaw[i];
        }
        return bmpbuf;
    }

    public int makebmp(byte[] fpRaw, String path) {
        try {

            File file;
            FileOutputStream out;
            //   String path = "/mnt/sdcard/ZAZFinger12.bmp";
            file = new File(path);
            file.createNewFile();
            out = new FileOutputStream(file);
            byte[] bmpbuf = Raw2Bmp(fpRaw);
            out.write(bmpbuf);
            bmpbuf = null;
            out.close();
            return 1;
        } catch (IOException e) {
            Log.e("*** makebmp ***", "makebmp is error");
            return 0;
        }
    }

    public byte[] getbmp(String path, int length) {
        byte[] bs = new byte[length];

        try {
            File file;
            FileInputStream in;
            //String path = "/mnt/sdcard/ZAZFinger12.bmp";
            file = new File(path);
            in = new FileInputStream(file);
            in.skip(1078);

            in.read(bs);
        } catch (IOException e) {
            Log.d("*** getbmp ***", "makebmp is error");

        }

        try {

            File file;
            FileOutputStream out;
            //   String path = "/mnt/sdcard/ZAZFinger12.bmp";
            file = new File("/mnt/sdcard/test.raw");
            file.createNewFile();
            out = new FileOutputStream(file);
            out.write(bs);
            out.close();
        } catch (IOException e) {
            Log.e("*** makebmp ***", "makebmp is error");
        }

        return bs;
    }
}