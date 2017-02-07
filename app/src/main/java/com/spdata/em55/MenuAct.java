package com.spdata.em55;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spdata.em55.base.BaseAct;
import com.spdata.em55.lr.CeJuAct;
import com.spdata.em55.lr.GpsAct;
import com.spdata.em55.lr.TemperatureActivity;
import com.spdata.em55.px.ID2.ID2Act;
import com.spdata.em55.px.finger.FpSDKSampleP11MActivity;
import com.spdata.em55.px.pasm.PasmAct;
import com.spdata.em55.px.print.print.demo.firstview.ConnectAvtivity;
import com.spdata.updateversion.UpdateVersion;


/**
 * Created by lenovo_pc on 2016/9/27.
 */

public class MenuAct extends BaseAct implements View.OnClickListener {
    LinearLayout lygps, lywendu, lyceju, lyupdata;
    LinearLayout layoutid, layoutpasm, layoutprint, layoutfinger;
    private Intent intent;
    TextView tvversion;
    private final String TAG = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        lyceju = (LinearLayout) findViewById(R.id.ly_ceju);
        lywendu = (LinearLayout) findViewById(R.id.ly_wendu);
        lygps = (LinearLayout) findViewById(R.id.ly_gps);
        lyupdata = (LinearLayout) findViewById(R.id.ly_updata);
        lyupdata.setOnClickListener(this);
        lygps.setOnClickListener(this);
        lywendu.setOnClickListener(this);
        lyceju.setOnClickListener(this);
        layoutid = (LinearLayout) findViewById(R.id.ly_id);
        layoutprint = (LinearLayout) findViewById(R.id.ly_print);
        layoutpasm = (LinearLayout) findViewById(R.id.ly_pasm);
        layoutfinger = (LinearLayout) findViewById(R.id.ly_finger);
        tvversion= (TextView) findViewById(R.id.tv_menu_version);
        layoutfinger.setOnClickListener(this);
        layoutid.setOnClickListener(this);
        layoutpasm.setOnClickListener(this);
        layoutprint.setOnClickListener(this);
        try {
            tvversion.setText("Version_New:"+getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent = new Intent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMenu();
    }

    public void showMenu() {
        switch (GetEm55External.readEm55()) {
            case "0"://em55_px 主要功能为二代证读取 打印机 pasm卡 指纹
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(true);
                layoutid.setEnabled(true);
                layoutprint.setEnabled(true);
                layoutfinger.setEnabled(true);
                break;
            case "16"://此背夹为em55_lr 功能为温湿度检测，激光测距，gps，北斗
                layoutpasm.setEnabled(false);
                layoutid.setEnabled(false);
                layoutprint.setEnabled(false);
                layoutfinger.setEnabled(false);
                lyceju.setEnabled(true);
                lygps.setEnabled(true);
                lywendu.setEnabled(true);
                break;
            case "32":
                lyceju.setEnabled(false);
                lygps.setEnabled(false);
                lywendu.setEnabled(false);
                layoutpasm.setEnabled(false);
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
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_ceju:
                intent.setClass(MenuAct.this, CeJuAct.class);
                startActivity(intent);
                break;
            case R.id.ly_gps:
                intent.setClass(MenuAct.this, GpsAct.class);
                startActivity(intent);
                break;
            case R.id.ly_wendu:
                intent.setClass(MenuAct.this, TemperatureActivity.class);
                startActivity(intent);
                break;
            case R.id.ly_updata:
                UpdateVersion updateVersion = new UpdateVersion(this);
                updateVersion.startUpdate();
                break;
        }
        if (v == layoutid) {
            Intent intent = new Intent(MenuAct.this, ID2Act.class);
            startActivity(intent);
        } else if (v == layoutprint) {
            Intent intent = new Intent(MenuAct.this, ConnectAvtivity.class);
            startActivity(intent);
        } else if (v == layoutpasm) {
            Intent intent = new Intent(MenuAct.this, PasmAct.class);
            startActivity(intent);
        } else if (v == layoutfinger) {
            Intent intent = new Intent(MenuAct.this, FpSDKSampleP11MActivity.class);
            startActivity(intent);
        }
    }

    /*
    * 获取当前程序的版本号
    */
    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = this.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(
                this.getPackageName(), 0);
        return packInfo.versionName;
    }
}
