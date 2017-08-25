/* 
 * File: 		EnrollmentActivity.java
 * Created:		2013/05/03
 * 
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.spdata.em55.px.fingerprint.tcs1g;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.PreEnrollmentFmd;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Fmd.Format;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.spdata.em55.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EnrollmentActivity extends Activity {
    private Button m_back;
    private String m_deviceName = "";

    private String m_enginError;

    private Reader m_reader = null;
    private int m_DPI = 0;
    private Bitmap m_bitmap = null;
    private Bitmap Wsqbitmap = null;
    private ImageView m_imgView;
    private TextView m_selectedDevice;
    private TextView m_title;
    private boolean m_reset = false;

    private TextView m_text;
    private TextView m_text_conclusion;
    private String m_textString;
    private String m_text_conclusionString;
    private Engine m_engine = null;
    private int m_current_fmds_count = 0;
    private boolean m_first = true;
    private boolean m_success = false;
    private Fmd m_enrollment_fmd = null;
    private int m_templateSize = 0;
    EnrollmentCallback enrollThread = null;
    private Reader.CaptureResult cap_result = null;
    private Fid compressFid;
    private CompressionImpl compression;
    private byte[] fid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine);
        m_textString = "Place any finger on the reader";
        initializeActivity();
        // initiliaze dp sdk
        try {
            Context applContext = getApplicationContext();
            m_reader = Globals.getInstance().getReader(m_deviceName, applContext);
            m_reader.Open(Priority.EXCLUSIVE);//优先
            m_DPI = Globals.GetFirstDPI(m_reader);
            m_engine = UareUGlobal.GetEngine();
        } catch (Exception e) {
            Log.w("UareUSampleJava", "error during init of reader");
            m_deviceName = "";
            onBackPressed();
            return;
        }

        // loop capture on a separate thread to avoid freezing the UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_current_fmds_count = 0;
                    m_reset = false;
                    enrollThread = new EnrollmentCallback(m_reader, m_engine);
                    while (!m_reset) {
                        try {
                            //创建并返回一个注册的fmd
                            m_enrollment_fmd = m_engine.CreateEnrollmentFmd(Format.ANSI_378_2004, enrollThread);
                            if (m_success = (m_enrollment_fmd != null)) {
                                //获取Fmd的size
                                // 返回FMD的完整二进制数据，包括记录头和所有视图
                                m_templateSize = m_enrollment_fmd.getData().length;
                                m_current_fmds_count = 0;    // reset count on success
                            }
                        } catch (Exception e) {
                            // template creation failed, reset count
                            m_current_fmds_count = 0;
                        }
                    }
                } catch (Exception e) {
                    if (!m_reset) {
                        Log.w("UareUSampleJava", "error during capture");
                        m_deviceName = "";
                        onBackPressed();
                    }
                }
            }
        }).start();
    }

    // called when orientation has changed to manually destroy and recreate activity
    //横屏是调用不在执行onCreate()
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_engine);
        initializeActivity();
    }

    private void initializeActivity() {
        compression = new CompressionImpl();
        m_enginError = "";
        m_title = (TextView) findViewById(R.id.title);
        m_title.setText("Enrollment");
        m_selectedDevice = (TextView) findViewById(R.id.selected_device);
        m_deviceName = getIntent().getExtras().getString("device_name");

        m_selectedDevice.setText("Device: " + m_deviceName);

        m_imgView = (ImageView) findViewById(R.id.bitmap_image);
        m_bitmap = Globals.GetLastBitmap();
        if (m_bitmap == null)
            m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        m_imgView.setImageBitmap(m_bitmap);
        m_back = (Button) findViewById(R.id.back);
        //back 按钮监听事件
        m_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        m_text = (TextView) findViewById(R.id.text);
        m_text_conclusion = (TextView) findViewById(R.id.text_conclusion);
        UpdateGUI();
    }

    public void UpdateGUI() {
        m_imgView.setImageBitmap(m_bitmap);
        m_imgView.invalidate();
        m_text_conclusion.setText(m_text_conclusionString);
        m_text.setText(m_textString);
    }

    /*
         获取并返回fmd将添加到预注册
        获取并返回PreEnrollmentFmd以创建注册FMD。
        在注册期间，CreateEnrollmentFmd（）重复调用EnrollmentCallback.GetFmd（）
        来获取FMD以进行注册。
     */
    public class EnrollmentCallback extends Thread implements Engine.EnrollmentCallback {
        public int m_current_index = 0;
        private Reader m_reader = null;
        private Engine m_engine = null;

        public EnrollmentCallback(Reader reader, Engine engine) {
            m_reader = reader;
            m_engine = engine;
        }

        // callback function is called by dp sdk to retrieve fmds until a null is returned
        @Override
        public PreEnrollmentFmd GetFmd(Format format) {
            PreEnrollmentFmd result = null;
            while (!m_reset) {
                try {
                    cap_result = m_reader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, -1);
                } catch (Exception e) {
                    Log.w("UareUSampleJava", "error during capture: " + e.toString());
                    m_deviceName = "";
                    onBackPressed();
                }
                //  ，cap_result.image=fid
                if (cap_result == null || cap_result.image == null) {
                    continue;//跳出本次循环
                }
                try {
                    m_enginError = "";
                    // 本地保存位图图像  ，cap_result.image 获取fid，getImageData()返回视图图像数据，getViews()[0]返回图像视图
                    //返回一个bitmap
                    fid = cap_result.image.getViews()[0].getImageData();
                    Fid fids=cap_result.image;
                    m_bitmap = Globals.GetBitmapFromRaw(fid, fids.getViews()[0].getWidth(), fids.getViews()[0].getHeight());
                    PreEnrollmentFmd prefmd = new PreEnrollmentFmd();
                    //提取功能并从ANSI或ISO映像创建FMD。
                    //此函数与具有每像素8位 - 无填充 - 正方形像素（对于水平和垂直的dpi相同）
                    // 的FID一起工作。所得FMD的大小将根据特定指纹中的细节而变化。
                    prefmd.fmd = m_engine.CreateFmd(cap_result.image, Format.ANSI_378_2004);
                    prefmd.view_index = 0;
                    m_current_fmds_count++;

                    result = prefmd;
                    break;
                } catch (Exception e) {
                    m_enginError = e.toString();
                    Log.w("UareUSampleJava", "Engine error: " + e.toString());
                }
            }
            //提示录入指纹时信息
            m_text_conclusionString = Globals.QualityToString(cap_result);

            if (!m_enginError.isEmpty()) {
                m_text_conclusionString = "Engine: " + m_enginError;
            }

            if (m_enrollment_fmd != null || m_current_fmds_count == 0) {
                if (!m_first) {
                    if (m_text_conclusionString.length() == 0) {
                        m_text_conclusionString = m_success ? "Enrollment template created, size: " + m_templateSize : "Enrollment template failed. Please try again";
//                        CompressionsWSQ();压缩 wsq
//                        Toast.makeText(EnrollmentActivity.this,"sucsess",Toast.LENGTH_LONG).show();
                    }
                }
                m_textString = "Place any finger on the reader";
                m_enrollment_fmd = null;
            } else {
                m_first = false;
                m_success = false;
                m_textString = "Continue to place the same finger on the reader";
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateGUI();
                }
            });
            return result;
        }
    }

    /**
     * 压缩指纹 返回一个fid
     */
    private void CompressionsWSQ() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    compression.Start();
                    compression.SetWsqBitrate(419, 3);
                    compressFid = compression.CompressFid(cap_result.image, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);

                    byte[] mm = compression.CompressRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(),
                            cap_result.image.getViews()[0].getHeight(), 20, cap_result.image.getBpp(),
                            Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);
//                    Wsqbitmap = Globals.GetBitmapFromRaw(compressFid.getViews()[0].getImageData(), compressFid.getViews()[0].getWidth(), compressFid.getViews()[0].getHeight());
                    byte [] data=compressFid.getViews()[0].getData();

                    saveMyBitmap(mm);

                    Wsqbitmap = Globals.GetBitmapFromRaw(mm,200,100);
//                    Wsqbitmap = Globals.GetBitmapFromRaw(compressFid.getViews()[0].getData(),  compressFid.getViews()[0].getWidth(),
//                            compressFid.getViews()[0].getHeight());
//                    saveMyBitmap(Wsqbitmap);
                    saveMyBitmap(Wsqbitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            m_imgView.setImageBitmap(null);
                            m_imgView.setImageBitmap(Wsqbitmap);
                        }
                    });
                    Log.w("UareUSampleJava", "Compressions: 0," + compressFid);
                } catch (UareUException e) {
                    e.printStackTrace();
                    Log.w("UareUSampleJava", "fid==err ");
//                    Toast.makeText(EnrollmentActivity.this, "erro", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    /**
     * Back 按钮
     */
    @Override
    public void onBackPressed() {
        try {
            m_reset = true;
            try {
                m_reader.CancelCapture();
                m_reader.Close();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            Log.w("UareUSampleJava", "error during reader shutdown");
        }
        Intent i = new Intent();
        i.putExtra("device_name", m_deviceName);
        setResult(Activity.RESULT_OK, i);
        finish();
        try {
            compression.Finish();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    public void saveMyBitmap(byte[] data) {
        try {
//            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bitmap = Bitmap.createBitmap(100, 50, Bitmap.Config.ARGB_8888);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.rewind();
            bitmap.copyPixelsFromBuffer(buffer);
            buffer.position(0);//将buffer的下一读写位置置为0。
            File file = new File("/sdcard/hou.jpg");
            FileOutputStream bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
            bos.flush();//输出
            bos.close();//关闭
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMyBitmap(Bitmap mBitmap) {
        File f = new File("/sdcard/finger.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.WEBP, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
