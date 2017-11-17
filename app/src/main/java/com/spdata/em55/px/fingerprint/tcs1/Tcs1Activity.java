package com.spdata.em55.px.fingerprint.tcs1;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.spdata.em55.R;

import java.io.IOException;

public class Tcs1Activity extends Activity implements View.OnClickListener {
    private static int DEV_ADDR = 0xffffffff;
    private static int IMG_SIZE = 0;//同参数：（0:256x288 1:256x360）
    private String TAG = "060M";
    com.za.finger.ZAandroid a6 = null;
    private ToggleButton btnopen;
    private Button btncomparison;
    private Button btnCharacteristic;
    private Button btngetimg;
    private Button btnregister;
    private Button btnsearch;
    private Button btnclear;
    private Button btnupchar;
    private Button btndown;
    private ImageView imageFp;
    private TextView mtvMessage;
    long ssart = System.currentTimeMillis();
    long ssend = System.currentTimeMillis();


    private int testcount = 0;
    private int fpcharbuf = 1;
    private Handler objHandler_fp;
    byte[] pTempletbase = new byte[2304];

    private int iPageID = 0;
    private boolean fpflag = false;
    private boolean fpcharflag = false;
    private boolean fpcomparison = false;
    private boolean fperoll = false;
    private boolean fpsearch = false;
    private boolean isfpon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcs1);
        initUI();
        objHandler_fp = new Handler();
    }


    private void initUI() {
        final OpenDevice openDevice = new OpenDevice();
        mtvMessage = (TextView) findViewById(R.id.textView2);
        imageFp = (ImageView) findViewById(R.id.image_fp);
        btnopen = (ToggleButton) findViewById(R.id.btnopen);
        btnopen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Runnable r = new Runnable() {
                        public void run() {
                            int stada = openDevice.OpenDev(Tcs1Activity.this, a6, DEV_ADDR, IMG_SIZE, 0);
                            Message message = new Message();
                            message.obj = stada;
                            m_fEvent.sendMessage(message);
                        }
                    };
                    Thread s = new Thread(r);
                    s.start();
                } else {
                    int status = a6.ZAZCloseDeviceEx();
                    Log.e(TAG, " close status: " + status);
                    if (status == 0) {
                        mtvMessage.setText("设备关闭");
                    } else {
                        a6.ZAZCloseDeviceEx();
                    }
                }
            }
        });
        btncomparison = (Button) findViewById(R.id.but_comparison);//对比
        btnCharacteristic = (Button) findViewById(R.id.btn_characteristic);//获取特征
        btngetimg = (Button) findViewById(R.id.btngetimg);//图片
        btnregister = (Button) findViewById(R.id.btn_register);//登记
        btnsearch = (Button) findViewById(R.id.btnsearch);//搜索
        btnclear = (Button) findViewById(R.id.btn_clear);//清除指纹
        btnupchar = (Button) findViewById(R.id.btnupchar); //上传特征
        btndown = (Button) findViewById(R.id.btndown);//下载特征
        btndown.setOnClickListener(this);
        btnregister.setOnClickListener(this);
        btnsearch.setOnClickListener(this);
        btngetimg.setOnClickListener(this);
        btnclear.setOnClickListener(this);
        btnupchar.setOnClickListener(this);
        btncomparison.setOnClickListener(this);
        btnCharacteristic.setOnClickListener(this);
        a6 = new com.za.finger.ZAandroid();
    }

    private final Handler m_fEvent = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int isfinsh = (int) msg.obj;
            switch (isfinsh) {
                case 0:
                    mtvMessage.setText(getResources().getString(R.string.opensuccess_str));
                    break;
                case 1:
                    mtvMessage.setText(getResources().getString(R.string.openfail_str));
                    break;
                case 2:
                    mtvMessage.setText(getResources().getString(R.string.usbfail_str));
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btngetimg:
                setflag(true);
                SystemClock.sleep(200);
                fpflag = false;
                removeCallbacks();
                readsfpimg();
                break;
            case R.id.btn_clear:
                setflag(true);
                int Rnet = a6.ZAZEmpty(DEV_ADDR);
                String temp = getResources().getString(R.string.equitsuccess_str) + "\r\n";
                iPageID = 0;
                mtvMessage.setText(temp);
                break;
            case R.id.btndown:
                byte[] pTemplet = new byte[512];
                int[] iTempletLength = new int[1];
                iTempletLength[0] = 512;
                ssart = System.currentTimeMillis();
                int nRet = a6.ZAZDownChar(DEV_ADDR, a6.CHAR_BUFFER_A, pTemplet, iTempletLength[0]);
                ssend = System.currentTimeMillis();
                long timecount = 0;
                timecount = (ssend - ssart);
                if (nRet == a6.PS_OK) {
                    temp = getResources().getString(R.string.downcharsuccess_str) + timecount;
                    // temp +=charToHexString(pTemplet);
                    mtvMessage.setText(temp);
                } else {
                    temp = "下载失败  nRet = " + nRet + "   " + timecount;
                    // temp +=charToHexString(pTemplet);
                    mtvMessage.setText(temp);

                }
                break;
            case R.id.btnupchar://搜索指纹
                byte[] pTemplets = new byte[512];
                int[] iTempletLengths = new int[1];
                ssart = System.currentTimeMillis();
                int nRets = a6.ZAZUpChar(DEV_ADDR, a6.CHAR_BUFFER_A, pTemplets, iTempletLengths);
                ssend = System.currentTimeMillis();
                long timecounts = 0;
                timecounts = (ssend - ssart);
                if (nRets == a6.PS_OK) {
                    temp = getResources().getString(R.string.upcharsuccess_str) + timecounts;
                    // temp +=charToHexString(pTemplet);
                    mtvMessage.setText(temp);
                }
                break;
            case R.id.but_comparison:
                setflag(true);
                SystemClock.sleep(300);
                fpcomparison = false;
                removeCallbacks();
                readsfpmatch();
                break;
            case R.id.btn_characteristic:
                setflag(true);
                SystemClock.sleep(300);
                fpcharflag = false;
                removeCallbacks();
                readsfpchar();
                break;
            case R.id.btn_register:
                setflag(true);
                SystemClock.sleep(300);
                fperoll = false;
                removeCallbacks();
                erollfp();
                break;
            case R.id.btnsearch:
//                setflag(true);
//                SystemClock.sleep(300);
//                fpsearch = false;
//                removeCallbacks();
//                searchfp();
                int[] id_iscore = new int[1];
                int red = a6.ZAZGetImage(DEV_ADDR);
                if (red == 0) {
                    testcount = 0;
                    SystemClock.sleep(200);
                    red = a6.ZAZGetImage(DEV_ADDR);
                }
                if (red == 0) {
                    red = a6.ZAZGenChar(DEV_ADDR, fpcharbuf);// != PS_OK) {
                    if (red == a6.PS_OK) {
                        red = a6.ZAZHighSpeedSearch(DEV_ADDR, 1, 0, 1000, id_iscore);
                        if (red == a6.PS_OK) {
                            temp = getResources().getString(R.string.searchsuccess_str) + id_iscore[0];
                            mtvMessage.setText(temp);
                        } else {
                            temp = getResources().getString(R.string.searchfail_str);
                            mtvMessage.setText(temp);
                        }

                    } else {
                        temp = getResources().getString(R.string.getfailchar_str);
                        mtvMessage.setText(temp);
                        ssart = System.currentTimeMillis();
                        objHandler_fp.postDelayed(fpsearchTasks, 1000);

                    }

                }
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            DeviceControl deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 63, 6);
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeCallbacks() {
        objHandler_fp.removeCallbacks(ComparisonTasks);
        objHandler_fp.removeCallbacks(Characteristic);
        objHandler_fp.removeCallbacks(fperollTasks);
        objHandler_fp.removeCallbacks(fpsearchTasks);
        objHandler_fp.removeCallbacks(fpTasks);
    }

    private void setflag(boolean value) {
        fpflag = value;
        fpcharflag = value;
        fpcomparison = value;
        fperoll = value;
        fpsearch = value;
    }


    /*****************************************
     * 线程   start
     ***************************************/

    public void readsfpmatch() {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        fpcharbuf = 1;
        isfpon = false;
        testcount = 0;
        objHandler_fp.postDelayed(ComparisonTasks, 0);
    }

    private Runnable ComparisonTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp = "";
            long timecount = 0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);

            if (timecount > 10000) {
                temp = getResources().getString(R.string.readfptimeout_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if (fpcomparison) {
                temp = getResources().getString(R.string.stopmatch_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            if (nRet == 0) {
                testcount = 0;
                SystemClock.sleep(200);
                nRet = a6.ZAZGetImage(DEV_ADDR);
            }
            if (nRet == 0) {
                if (isfpon) {
                    temp = getResources().getString(R.string.pickupfinger_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(ComparisonTasks, 100);
                    return;
                }

                //nRet = a6.ZAZLoadChar( DEV_ADDR,2,1);
                //a6.ZAZSetCharLen(2304);
                //nRet = a6.ZAZDownChar(DEV_ADDR, 2, pTempletbase, 2304);
                nRet = a6.ZAZGenChar(DEV_ADDR, fpcharbuf);// != PS_OK) {
                if (nRet == a6.PS_OK) {
                    if (fpcharbuf != 1) {
                        int[] iScore = {0, 0};
                        nRet = a6.ZAZMatch(DEV_ADDR, iScore);
                        if (nRet == a6.PS_OK) {
                            temp = getResources().getString(R.string.matchsuccess_str) + iScore[0];
                            mtvMessage.setText(temp);
                        } else {
                            temp = getResources().getString(R.string.matchfail_str) + iScore[0];
                            mtvMessage.setText(temp);
                        }
                        return;
                    }

                    fpcharbuf = 2;
                    isfpon = true;
                    temp = getResources().getString(R.string.putyourfinger_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(ComparisonTasks, 100);
                } else {
                    temp = getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(ComparisonTasks, 1000);

                }

            } else if (nRet == a6.PS_NO_FINGER) {
                temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                isfpon = false;
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(ComparisonTasks, 10);
            } else if (nRet == a6.PS_GET_IMG_ERR) {
                temp = getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp + ": " + nRet);
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(ComparisonTasks, 10);
                //mtvMessage.setText(temp);
                return;
            } else if (nRet == -2) {
                testcount++;
                if (testcount < 3) {
                    temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(ComparisonTasks, 10);
                } else {
                    temp = getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp + ": " + nRet);
                    mtvMessage.setText(temp);

                    return;
                }
            } else {
                temp = getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp + ": " + nRet);
                mtvMessage.setText(temp);

                return;
            }
        }
    };


    public void readsfpchar() {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        testcount = 0;
        objHandler_fp.postDelayed(Characteristic, 0);
    }

    private long timer() {
        long timecount = 0;
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        timecount = (ssend - ssart);
        return timecount;
    }

    byte[] pTemplet = null;
    private Runnable Characteristic = new Runnable() {
        // 运行该服务执行此函数
        public void run() {
            String temp = "";
            long timecount = 0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);
            if (timecount > 10000) {
                temp = getResources().getString(R.string.readfptimeout_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if (fpcharflag) {
                temp = getResources().getString(R.string.stopgetchar_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            if (nRet == 0) {
                testcount = 0;
                SystemClock.sleep(200);
                nRet = a6.ZAZGetImage(DEV_ADDR);
            }
            if (nRet == 0) {
                nRet = a6.ZAZGenChar(DEV_ADDR, a6.CHAR_BUFFER_A);// != PS_OK) {
                if (nRet == a6.PS_OK) {
                    int[] iTempletLength = {0, 0};
                    pTemplet = new byte[512];
                    a6.ZAZSetCharLen(512);
                    nRet = a6.ZAZUpChar(DEV_ADDR, a6.CHAR_BUFFER_A, pTemplet, iTempletLength);
                    if (nRet == a6.PS_OK) {
                        //temp="指纹特征:"+iTempletLength[0] +" \r\n";
                        temp = charToHexString(pTemplet);
                        mtvMessage.setText(temp);
                    }
                    nRet = a6.ZAZDownChar(DEV_ADDR, a6.CHAR_BUFFER_A, pTemplet, iTempletLength[0]);
                    if (nRet == a6.PS_OK) {
                        temp += getResources().getString(R.string.downsuccess_str);

                        mtvMessage.setText(temp);
                    }
                } else {
                    temp = getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(Characteristic, 1000);

                }
            } else if (nRet == a6.PS_NO_FINGER) {
                temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(Characteristic, 10);
            } else if (nRet == a6.PS_GET_IMG_ERR) {
                temp = getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp + "1: " + nRet);
                objHandler_fp.postDelayed(Characteristic, 10);
                mtvMessage.setText(temp);
                return;
            } else if (nRet == -2) {
                testcount++;
                if (testcount < 3) {
                    temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(ComparisonTasks, 10);
                } else {
                    temp = getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp + ": " + nRet);
                    mtvMessage.setText(temp);

                    return;
                }
            } else {
                temp = getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp + "1: " + nRet);
                mtvMessage.setText(temp);
                return;
            }

        }
    };


    public void readsfpimg() {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        testcount = 0;
        objHandler_fp.postDelayed(fpTasks, 0);
    }

    private Runnable fpTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp = "";
            long timecount = 0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);

            if (timecount > 10000) {
                temp = getResources().getString(R.string.readfptimeout_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if (fpflag) {
                temp = getResources().getString(R.string.stopgetimage_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            if (nRet == 0) {
                testcount = 0;
                int[] len = {0, 0};
                byte[] Image = new byte[256 * 360];
                a6.ZAZUpImage(DEV_ADDR, Image, len);
                String str = "/mnt/sdcard/test.bmp";
                a6.ZAZImgData2BMP(Image, str);
                temp = getResources().getString(R.string.getimagesuccess_str);
                mtvMessage.setText(temp);

                Bitmap bmpDefaultPic;
                bmpDefaultPic = BitmapFactory.decodeFile(str, null);
                imageFp.setImageBitmap(bmpDefaultPic);
            } else if (nRet == a6.PS_NO_FINGER) {
                temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fpTasks, 100);
            } else if (nRet == a6.PS_GET_IMG_ERR) {
                temp = getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp + "2: " + nRet);
                objHandler_fp.postDelayed(fpTasks, 100);
                mtvMessage.setText(temp);
                return;
            } else if (nRet == -2) {
                testcount++;
                if (testcount < 3) {
                    temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(ComparisonTasks, 10);
                } else {
                    temp = getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp + ": " + nRet);
                    mtvMessage.setText(temp);

                    return;
                }
            } else {
                temp = getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp + "2: " + nRet);
                mtvMessage.setText(temp);
                return;
            }

        }
    };

    public void erollfp() {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        fpcharbuf = 1;
        isfpon = false;
        testcount = 0;
        objHandler_fp.postDelayed(fperollTasks, 0);
    }

    private Runnable fperollTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp = "";
            long timecount = 0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);

            if (timecount > 10000) {
                temp = getResources().getString(R.string.readfptimeout_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if (fperoll) {
                temp = getResources().getString(R.string.stoperoll_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            if (nRet == 0) {
                testcount = 0;
                SystemClock.sleep(200);
                nRet = a6.ZAZGetImage(DEV_ADDR);
                if (nRet == 0) {
                    testcount = 0;
                    int[] len = {0, 0};
                    byte[] Image = new byte[256 * 360];
                    a6.ZAZUpImage(DEV_ADDR, Image, len);
                    String str = "/mnt/sdcard/test.bmp";
                    a6.ZAZImgData2BMP(Image, str);
                    temp = getResources().getString(R.string.getimagesuccess_str);
                    mtvMessage.setText(temp);

                    Bitmap bmpDefaultPic;
                    bmpDefaultPic = BitmapFactory.decodeFile(str, null);
                    imageFp.setImageBitmap(bmpDefaultPic);
                }
            }

            if (nRet == 0) {
                if (isfpon) {
                    temp = getResources().getString(R.string.pickupfinger_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fperollTasks, 100);
                    return;
                }
                nRet = a6.ZAZGenChar(DEV_ADDR, fpcharbuf);// != PS_OK) {
                if (nRet == a6.PS_OK) {
                    fpcharbuf++;
                    isfpon = true;
                    if (fpcharbuf > 2) {
                        nRet = a6.ZAZRegModule(DEV_ADDR);
                        if (nRet != a6.PS_OK) {
                            temp = getResources().getString(R.string.RegModulefail_str);
                            mtvMessage.setText(temp);

                        } else {
                            nRet = a6.ZAZStoreChar(DEV_ADDR, 1, iPageID);
                            if (nRet == a6.PS_OK) {
                                temp = getResources().getString(R.string.erollsuccess_str) + iPageID;
                                int[] iTempletLength = new int[1];
                                nRet = a6.ZAZUpChar(DEV_ADDR, 1, pTempletbase, iTempletLength);
                                //System.arraycopy(pTemplet, 0, pTempletbase, 0, 2304);
                                mtvMessage.setText(temp);
                                iPageID++;
                            } else {
                                temp = getResources().getString(R.string.erollfail_str);
                                mtvMessage.setText(temp);
                            }
                        }
                    } else {

                        temp = getResources().getString(R.string.getfpsuccess_str);
                        mtvMessage.setText(temp);
                        ssart = System.currentTimeMillis();
                        objHandler_fp.postDelayed(fperollTasks, 500);

                    }
                } else {
                    temp = getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fperollTasks, 1000);

                }

            } else if (nRet == a6.PS_NO_FINGER) {
                temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                isfpon = false;
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fperollTasks, 10);
            } else if (nRet == a6.PS_GET_IMG_ERR) {
                temp = getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp + ": " + nRet);
                objHandler_fp.postDelayed(fperollTasks, 10);
                mtvMessage.setText(temp);
                return;
            } else if (nRet == -2) {
                testcount++;
                if (testcount < 3) {
                    temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(ComparisonTasks, 10);
                } else {
                    temp = getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp + ": " + nRet);
                    mtvMessage.setText(temp);

                    return;
                }
            } else {
                temp = getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp + ": " + nRet);
                mtvMessage.setText(temp);

                return;
            }

        }
    };


    public void searchfp() {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        fpcharbuf = 1;
        testcount = 0;
        objHandler_fp.postDelayed(fpsearchTasks, 0);
    }

    private Runnable fpsearchTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp = "";
            long timecount = 0;
            int[] id_iscore = new int[1];
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);

            if (timecount > 10000) {
                temp = getResources().getString(R.string.readfptimeout_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if (fpsearch) {
                temp = getResources().getString(R.string.stopsearch_str) + "\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            if (nRet == 0) {
                testcount = 0;
                SystemClock.sleep(200);
                nRet = a6.ZAZGetImage(DEV_ADDR);
            }
            if (nRet == 0) {

                nRet = a6.ZAZGenChar(DEV_ADDR, fpcharbuf);// != PS_OK) {
                if (nRet == a6.PS_OK) {
                    nRet = a6.ZAZHighSpeedSearch(DEV_ADDR, 1, 0, 1000, id_iscore);
                    if (nRet == a6.PS_OK) {
                        temp = getResources().getString(R.string.searchsuccess_str) + id_iscore[0];
                        mtvMessage.setText(temp);
                    } else {
                        temp = getResources().getString(R.string.searchfail_str);
                        mtvMessage.setText(temp);
                    }

                } else {
                    temp = getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fpsearchTasks, 1000);

                }

            } else if (nRet == a6.PS_NO_FINGER) {
                temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fpsearchTasks, 10);
            } else if (nRet == a6.PS_GET_IMG_ERR) {
                temp = getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp + ": " + nRet);
                objHandler_fp.postDelayed(fpsearchTasks, 10);
                mtvMessage.setText(temp);
                return;
            } else if (nRet == -2) {
                testcount++;
                if (testcount < 3) {
                    temp = getResources().getString(R.string.readingfp_str) + ((10000 - (ssend - ssart))) / 1000 + "s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(ComparisonTasks, 10);
                } else {
                    temp = getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp + ": " + nRet);
                    mtvMessage.setText(temp);
                    return;
                }
            } else {
                temp = getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp + ": " + nRet);
                mtvMessage.setText(temp);

                return;
            }

        }
    };


    /*****************************************
     * 线程   end
     ***************************************/


    private static String charToHexString(byte[] val) {
        String temp = "";
        for (int i = 0; i < val.length; i++) {
            String hex = Integer.toHexString(0xff & val[i]);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            temp += hex.toUpperCase();
        }
        return temp;
    }


}
