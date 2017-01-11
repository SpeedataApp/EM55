package com.spdata.em55.px.ID2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spdata.em55.BaseAct;
import com.spdata.em55.R;
import com.speedata.libid2.IDInfor;
import com.speedata.libid2.IDManager;
import com.speedata.libid2.IDReadCallBack;
import com.speedata.libid2.IID2Service;

import java.io.IOException;

public class ID2Act extends BaseAct implements OnClickListener {

    private static final String TAG = "ID_DEV";
    private static final String SERIALPORT_PATH = "/dev/ttyMT2";
    private int IDFd, i;

    private ToggleButton powerBtn;
    private ToggleButton autoBtn;
    private Button findBtn;
    private Button chooseBtn;
    private Button readBtn;
    private Button sendBtn;
    private Button readfingerBtn;
    private TextView contView;
    private EditText EditTextsend;
    private ImageView mImageViewPhoto;

    private TextView mtextname;
    private TextView mtextsex;
    private TextView mtextminzu;
    private TextView mtextyear;
    private TextView mtextmouth;
    private TextView mtextday;
    private TextView mtextaddr;
    private TextView mtextnum;
    private TextView mtextqianfa;
    private TextView mtextqixian;
    private IDInfor idInfor;
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            idInfor = (IDInfor) msg.obj;
            if (idInfor.isSuccess()) {
                mtextsex.setText(idInfor.getSex());
                mtextname.setText(idInfor.getName());
                mtextaddr.setText(idInfor.getAddress());
                mtextminzu.setText(idInfor.getNation());
                mtextyear.setText(idInfor.getYear());
                mtextmouth.setText(idInfor.getMonth());
                mtextday.setText(idInfor.getDay());
                mtextsex.setText(idInfor.getSex());
                mtextnum.setText(idInfor.getNum());
                mtextqianfa.setText(idInfor.getQianFa());
                mtextqixian.setText(idInfor.getDeadLine());
//                mtextqixian.setText(idInfor.getStartYear() + idInfor.getStartMonth() + idInfor.getStartDay() + "-" + idInfor.getEndYear() + idInfor.getEndMonth() + idInfor.getEndDay());
                mImageViewPhoto.setImageBitmap(idInfor.getBmps());
                if (idInfor.isWithFinger()) {
                    //有zhiwen
                    idInfor.getFingerprStringer();
                    Toast.makeText(ID2Act.this, "该身份证有指纹！", Toast.LENGTH_SHORT).show();
                }

            } else {
                //TODO ERROR
                contView.setText(idInfor.getErrorMsg());
            }

        }
    };
    private IID2Service iid2Service;
    private CheckBox boxselect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initUI();
        Log.i(TAG, "onCreate is called");
        iid2Service = IDManager.getInstance();
        try {
            iid2Service.initDev(this, new IDReadCallBack() {
                @Override
                public void callBack(IDInfor infor) {
                    Message msg = new Message();
                    msg.obj = infor;
                    handler.sendMessage(msg);
                }
            }, SERIALPORT_PATH, 115200, DeviceControl.PowerType.MAIN_AND_EXPAND, 88, 6);
//        }, "/dev/ttyMT2", 115200, IID2Service.PowerType.MAIN, 88);只主板上电
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void initUI() {
        setContentView(R.layout.act_id2);
        findBtn = (Button) findViewById(R.id.button_find);
        findBtn.setOnClickListener(this);
        chooseBtn = (Button) findViewById(R.id.button_choose);
        chooseBtn.setOnClickListener(this);
        readBtn = (Button) findViewById(R.id.button_read);
        readBtn.setOnClickListener(this);
        readfingerBtn = (Button) findViewById(R.id.button_finger);
        readfingerBtn.setOnClickListener(this);
        contView = (TextView) findViewById(R.id.tv_content);
        mtextname = (TextView) findViewById(R.id.textname);
        mtextsex = (TextView) findViewById(R.id.textsex);
        mtextminzu = (TextView) findViewById(R.id.textminzu);
        mtextyear = (TextView) findViewById(R.id.textyear);
        mtextmouth = (TextView) findViewById(R.id.textmouth);
        mtextday = (TextView) findViewById(R.id.textday);
        mtextaddr = (TextView) findViewById(R.id.textaddr);
        mtextnum = (TextView) findViewById(R.id.textsfz);
        mtextqianfa = (TextView) findViewById(R.id.textqianfa);
        mtextqixian = (TextView) findViewById(R.id.textqixian);
        mImageViewPhoto = (ImageView) findViewById(R.id.imageViewPortrait);
        boxselect = (CheckBox) findViewById(R.id.box_select);
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            iid2Service.releaseDev();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Context mContext;

    @Override
    public void onClick(View view) {
        if (view == findBtn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = iid2Service.searchCard();
                    final String msg = iid2Service.parseReturnState(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contView.setText(msg);
                        }
                    });
                }
            }).start();

        } else if (view == chooseBtn) {
            int result = iid2Service.selectCard();
            String msg = iid2Service.parseReturnState(result);
            contView.setText(msg);
        } else if (view == readBtn) {
            contView.setText("");
            iid2Service.getIDInfor(false);

//            IDInfor idInfor = iid2Service.readCard(false);
//            Message msg = new Message();
//            msg.obj = idInfor;
//            handler.sendMessage(msg);
        } else if (view == readfingerBtn) {
            contView.setText("");
            iid2Service.getIDInfor(true);
        } else if (view == sendBtn) {

        }
    }
}
