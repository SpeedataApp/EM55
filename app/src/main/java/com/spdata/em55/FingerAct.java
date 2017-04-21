package com.spdata.em55;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalpersona.uareu.Fmd;
import com.mylibrary.FingerManger;
import com.mylibrary.inf.IFingerPrint;
import com.spdata.em55.base.BaseAct;

import java.io.IOException;
import java.text.DecimalFormat;

public class FingerAct extends BaseAct implements View.OnClickListener {
    private Button btnOpen, btnGetImage, btnGetQuality, btnColse, btnCompare, btnCreateTemplate;
    private ImageView fingerImage;
    private TextView tvMsg;
    private IFingerPrint iFingerPrint;
    private DeviceControl deviceControl;
    private Fmd fmd1 = null;
    private Fmd fmd2 = null;
    private byte[] template1;
    private byte[] template2;
    private boolean template = true;
    String TAG = "finger";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger);
        initUI();
        setBtnState(false);
    }

    private void setBtnState(boolean btnState) {
        btnGetImage.setEnabled(btnState);
        btnGetQuality.setEnabled(btnState);
        btnColse.setEnabled(btnState);
        btnCompare.setEnabled(btnState);
        btnCreateTemplate.setEnabled(btnState);
    }

    private void initUI() {
        try {
            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 63, 5, 6);
            deviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                iFingerPrint = FingerManger.getIFingerPrintIntance(FingerAct.this, FingerAct.this, handler);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iFingerPrint == null) {
                            showToast("请链接指纹模板");
                            finish();
                        }
                    }
                });
            }
        }).start();

        tvMsg = (TextView) findViewById(R.id.tv_msg);
        fingerImage = (ImageView) findViewById(R.id.btn_imageView);
        btnOpen = (Button) findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(this);
        btnGetImage = (Button) findViewById(R.id.btn_getimage);
        btnGetImage.setOnClickListener(this);
        btnGetQuality = (Button) findViewById(R.id.btn_quality);
        btnGetQuality.setOnClickListener(this);
        btnCreateTemplate = (Button) findViewById(R.id.btn_getTemplate);
        btnCreateTemplate.setOnClickListener(this);
        btnCompare = (Button) findViewById(R.id.btn_compare);
        btnCompare.setOnClickListener(this);
        btnColse = (Button) findViewById(R.id.btn_colse);
        btnColse.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnOpen) {
            if (iFingerPrint.openReader()) {
                tvMsg.setText("Open reader success");
                setBtnState(true);
            } else {
                tvMsg.setText("Open reader fail");
                setBtnState(false);
            }
        } else if (view == btnColse) {
            if (iFingerPrint.closeReader()) {
                tvMsg.setText("Colse reader success");
                setBtnState(false);
            } else {
                tvMsg.setText("Colse reader fail");
            }
        } else if (view == btnGetImage) {
            iFingerPrint.getImage();
        } else if (view == btnGetQuality) {
            iFingerPrint.getImageQuality();
        } else if (view == btnCreateTemplate) {
            iFingerPrint.createTemplate();

        } else if (view == btnCompare) {
            iFingerPrint.comparisonFinger(template1, template2);
            iFingerPrint.comparisonFinger(fmd1, fmd2);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1://显示图片  黑色指纹
                    ShowFingerBitmap((byte[]) msg.obj, msg.arg1, msg.arg2);
//                    tvMsg.setText((String) msg.obj);
                    break;
                case 2://获取质量  黑色指纹
                    tvMsg.setText(String.format("CompareTemplates() = %d", (Integer) msg.obj));
                    break;
                case 3://获取模板特征   黑色指纹
                    if (template) {
                        template = false;
                        template1 = new byte[1024];
                        template1 = (byte[]) msg.obj;
                        Toast.makeText(FingerAct.this, "template1", Toast.LENGTH_SHORT).show();


                    } else {
                        template = true;
                        template2 = new byte[1024];
                        template2 = (byte[]) msg.obj;
                        Toast.makeText(FingerAct.this, "template2", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4://比较指纹模板特征  黑色指纹
                    tvMsg.setText(String.format("CompareTemplates() = %d", (Integer) msg.obj));
                    break;
                case 5:  //显示图片  金色指纹
                    fingerImage.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 6:// 创建指纹模板 金色指纹
                    if (template) {
                        template = false;
                        fmd1 = (Fmd) msg.obj;
                        Toast.makeText(FingerAct.this, "fmd1", Toast.LENGTH_SHORT).show();
                    } else {
                        template = true;
                        fmd2 = (Fmd) msg.obj;
                        Toast.makeText(FingerAct.this, "fmd2", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 7:
                    int m_score = (int) msg.obj;
                    DecimalFormat formatting = new DecimalFormat("##.######");
                    String conclusionString = "Dissimilarity Score: " + String.valueOf(m_score)
                            + ", False match rate: " +
                            Double.valueOf(formatting.format((double) m_score / 0x7FFFFFFF))
                            + " (" + (m_score < (0x7FFFFFFF / 100000) ? "match" : "no match") + ")";
                    tvMsg.setText(conclusionString);
                    break;
                case 8:
                    tvMsg.setText((String) msg.obj);
                    break;
                case 9:
                    tvMsg.setText((String) msg.obj);
                    break;
                case 10:
//                    fingerImage.setImageBitmap(null);
                    fingerImage.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 11:
                    String temp = charToHexString((byte[]) msg.obj);
                    tvMsg.setText(temp);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ShowFingerBitmap(byte[] image, int width, int height) {
        if (width == 0) return;
        if (height == 0) return;

        int[] RGBbits = new int[width * height];
        fingerImage.invalidate();
        for (int i = 0; i < width * height; i++) {
            int v;
            if (image != null) v = image[i] & 0xff;
            else v = 0;
            RGBbits[i] = Color.rgb(v, v, v);
        }
        Bitmap bmp = Bitmap.createBitmap(RGBbits, width, height, Bitmap.Config.RGB_565);
        fingerImage.setImageBitmap(bmp);
    }

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
