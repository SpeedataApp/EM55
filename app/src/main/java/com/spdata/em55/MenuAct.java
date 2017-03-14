package com.spdata.em55;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spdata.em55.base.BaseAct;
import com.spdata.em55.gxandurx.UhfAct;
import com.spdata.em55.lr.DistanceAct;
import com.spdata.em55.lr.GpsAct;
import com.spdata.em55.lr.TemperatureAct;
import com.spdata.em55.px.ID2.ID2Act;
import com.spdata.em55.px.fingerprint.FingerPrintAct;
import com.spdata.em55.px.print.print.demo.firstview.ConnectAvtivity;
import com.spdata.em55.px.psam.PsamAct;
import com.spdata.updateversion.UpdateVersion;


/**
 * Created by lenovo_pc on 2016/9/27.
 */

public class MenuAct extends BaseAct implements View.OnClickListener {
    LinearLayout lygps, lywendu, lyceju, lyupdata;
    LinearLayout layoutid, layoutpasm, layoutprint, layoutfinger, lyUhf;
    TextView tvversion;
    private final String TAG = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        initUI();
//        UpdateVersion updateVersion = new UpdateVersion(this);
//        updateVersion.startUpdate();
    }

    private void initUI() {
        //lr
        lyceju = (LinearLayout) findViewById(R.id.ly_ceju);
        lywendu = (LinearLayout) findViewById(R.id.ly_wendu);
        lygps = (LinearLayout) findViewById(R.id.ly_gps);
        lyupdata = (LinearLayout) findViewById(R.id.ly_updata);
        lyupdata.setOnClickListener(this);
        lygps.setOnClickListener(this);
        lywendu.setOnClickListener(this);
        lyceju.setOnClickListener(this);
        //px 和 idx
        layoutid = (LinearLayout) findViewById(R.id.ly_id);
        layoutprint = (LinearLayout) findViewById(R.id.ly_print);
        layoutpasm = (LinearLayout) findViewById(R.id.ly_pasm);
        layoutfinger = (LinearLayout) findViewById(R.id.ly_finger);
        tvversion = (TextView) findViewById(R.id.tv_menu_version);
        layoutfinger.setOnClickListener(this);
        layoutid.setOnClickListener(this);
        layoutpasm.setOnClickListener(this);
        layoutprint.setOnClickListener(this);
        //gx  和  urx
        lyUhf = (LinearLayout) findViewById(R.id.ly_uhf);
        lyUhf.setOnClickListener(this);
        try {
            tvversion.setText("Version_New:" + getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMenu();
    }

    public void showMenu() {
        switch (readEm55()) {
            case "1"://em55_px 主要功能为二代证读取 打印机 pasm卡 指纹
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(true);
                layoutid.setEnabled(true);
                layoutprint.setEnabled(true);
                layoutfinger.setEnabled(true);
                lyUhf.setEnabled(false);
                break;
            case "16"://此背夹为em55_lr 功能为温湿度检测，激光测距，gps，北斗
                layoutpasm.setEnabled(false);
                layoutid.setEnabled(false);
                layoutprint.setEnabled(false);
                layoutfinger.setEnabled(false);
                lyUhf.setEnabled(false);
                lyceju.setEnabled(true);
                lygps.setEnabled(true);
                lywendu.setEnabled(true);
                break;
            case "32"://em55_IDX  功能：id2 ，指纹（国内或国外）
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(false);
                lyUhf.setEnabled(false);
                layoutid.setEnabled(true);
                layoutprint.setEnabled(false);
                layoutfinger.setEnabled(true);
                break;
            case "80"://em55_GX  功能：uhf超高屏，枪柄按键,可以触发主机快捷扫描
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(false);
                lyUhf.setEnabled(true);
                layoutid.setEnabled(false);
                layoutprint.setEnabled(false);
                layoutfinger.setEnabled(true);
                break;
            case "64"://em55_URX  功能：电容式指纹采集识别，R2000 UHF超高频 ，旗联超高频
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(false);
                lyUhf.setEnabled(true);
                layoutid.setEnabled(true);
                layoutprint.setEnabled(false);
                layoutfinger.setEnabled(true);
                break;
            default:
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(false);
                layoutid.setEnabled(false);
                layoutprint.setEnabled(false);
                layoutfinger.setEnabled(false);
                lyUhf.setEnabled(false);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_ceju:
                openAct(DistanceAct.class);
                break;
            case R.id.ly_gps:
                openAct(GpsAct.class);
                break;
            case R.id.ly_wendu:
                openAct(TemperatureAct.class);
                break;
            case R.id.ly_updata:
                UpdateVersion updateVersion = new UpdateVersion(this);
                updateVersion.startUpdate();
                break;
        }
        if (v == layoutid) {
            openAct(ID2Act.class);
        } else if (v == layoutprint) {
            openAct(ConnectAvtivity.class);
        } else if (v == layoutpasm) {
            openAct(PsamAct.class);
        } else if (v == layoutfinger) {
            openAct(FingerPrintAct.class);
        } else if (v == lyUhf) {
            openAct(UhfAct.class);
        }
    }
}
