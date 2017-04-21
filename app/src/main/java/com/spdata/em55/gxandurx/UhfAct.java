package com.spdata.em55.gxandurx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.spdata.em55.R;
import com.spdata.em55.base.BaseAct;
import com.spdata.em55.gxandurx.dialog.LockTagDialog;
import com.spdata.em55.gxandurx.dialog.ReadTagDialog;
import com.spdata.em55.gxandurx.dialog.SearchTagDialog;
import com.spdata.em55.gxandurx.dialog.SetEPCDialog;
import com.spdata.em55.gxandurx.dialog.SetModuleDialog;
import com.spdata.em55.gxandurx.dialog.SetPasswordDialog;
import com.spdata.em55.gxandurx.dialog.SpeedTestDialog;
import com.spdata.em55.gxandurx.dialog.WriteTagDialog;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;


public class UhfAct extends BaseAct implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    private static final String[] list = {"Reserved", "EPC", "TID", "USER"};
    private TextView Cur_Tag_Info;
    private TextView Status;
    private Spinner Area_Select;
    private ArrayAdapter<String> adapter;
    private Button Search_Tag;
    private Button Read_Tag;
    private Button Write_Tag;
    private Button Set_Tag;
    private Button Set_Password;
    private Button Set_EPC;
    private Button Lock_Tag;
    private EditText Tag_Content;
    private IUHFService iuhfService;
    private String current_tag_epc = null;
    private Button Speedt;
    private PowerManager pM = null;
    private PowerManager.WakeLock wK = null;
    private int init_progress = 0;
    private String modle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    private DeviceControl myDeviceControl6;
    private DeviceControl myDeviceControl4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.getInstance().addActivity(UhfAct.this);
        String readEm55 = getEM55Model();
        if (readEm55.equals("80") ) {
            try {
                myDeviceControl6 = new DeviceControl(DeviceControl.PowerType.EXPAND, 7);
                myDeviceControl4 = new DeviceControl(DeviceControl.PowerType.EXPAND, 5);
                myDeviceControl4.PowerOnDevice();
                myDeviceControl6.PowerOnDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (readEm55.equals("48")||readEm55.equals("81")){
            try {
                myDeviceControl6 = new DeviceControl(DeviceControl.PowerType.EXPAND, 6);
                myDeviceControl4 = new DeviceControl(DeviceControl.PowerType.EXPAND, 7);
                myDeviceControl4.PowerOnDevice();
                myDeviceControl6.PowerOnDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String em55 = SharedXmlUtil.getInstance(UhfAct.this).read("readEm55", "");
        if (em55 != null && !em55.equals(readEm55)) {
            SharedXmlUtil.getInstance(UhfAct.this).write("modle", "");
        }
        SharedXmlUtil.getInstance(UhfAct.this).write("readEm55", readEm55);
        iuhfService = UHFManager.getUHFService(UhfAct.this);
        if (iuhfService == null) {
            Toast.makeText(UhfAct.this, "模块不识别", Toast.LENGTH_SHORT).show();
            return;
        }
        modle = SharedXmlUtil.getInstance(UhfAct.this).read("modle", "");
        initUI();
//        if (openDev()) return;
        newWakeLock();
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
        Set_Tag.setEnabled(true);
        Search_Tag.setEnabled(true);
        Read_Tag.setEnabled(true);
        Write_Tag.setEnabled(true);
        Set_EPC.setEnabled(true);
        Set_Password.setEnabled(true);
        Lock_Tag.setEnabled(true);
        Area_Select.setEnabled(true);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openDev()) return;
    }


    @Override
    protected void onPause() {
        super.onPause();
//        Log.d("r2000_kt45", "called ondestory");
        iuhfService.CloseDev();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent mEvent) {
        String type = mEvent.getType();
        String msg = (String) mEvent.getMsg();
        if (type.equals("write_Status")) {
            UhfAct.this.Status
                    .setText(R.string.Status_Write_Card_Ok);
        }
        if (type.equals("set_current_tag_epc")) {
            current_tag_epc = msg;
            Cur_Tag_Info.setText(msg);
            UhfAct.this.Status
                    .setText(R.string.Status_Select_Card_Ok);
        }
        if (type.equals("read_Status")) {
            Tag_Content.setText(msg);
            UhfAct.this.Status
                    .setText(R.string.Status_Read_Card_Ok);
        }
        if (type.equals("setPWD_Status")) {
            UhfAct.this.Status
                    .setText(R.string.Status_Write_Card_Ok);
        }
        if (type.equals("lock_Status")) {
            UhfAct.this.Status
                    .setText("设置成功");
        }
        if (type.equals("SetEPC_Status")) {
            UhfAct.this.Status
                    .setText(R.string.Status_Write_Card_Ok);
        }
    }

    private void newWakeLock() {
        init_progress++;
        pM = (PowerManager) getSystemService(POWER_SERVICE);
        if (pM != null) {
            wK = pM.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "lock3992");
            if (wK != null) {
                wK.acquire();
                init_progress++;
            }
        }

        if (init_progress == 1) {
            Log.w("3992_6C", "wake lock init failed");
        }
    }

    private boolean openDev() {
        if (iuhfService.OpenDev() != 0) {
            Cur_Tag_Info.setText("Open serialport failed");
            new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    finish();
                }
            }).show();
            return true;
        }
        return false;
    }

    private void initUI() {
        setContentView(R.layout.main);
        Tag_Content = (EditText) findViewById(R.id.editText_content);
        Write_Tag = (Button) findViewById(R.id.btn_write);
        Write_Tag.setOnClickListener(this);
        Read_Tag = (Button) findViewById(R.id.btn_read);
        Read_Tag.setOnClickListener(this);
        Search_Tag = (Button) findViewById(R.id.btn_search);
        Search_Tag.setOnClickListener(this);
        Set_Tag = (Button) findViewById(R.id.btn_check);
        Set_Tag.setOnClickListener(this);
        Set_Password = (Button) findViewById(R.id.btn_setpasswd);
        Set_Password.setOnClickListener(this);
        Set_EPC = (Button) findViewById(R.id.btn_setepc);
        Set_EPC.setOnClickListener(this);
        Lock_Tag = (Button) findViewById(R.id.btn_lock);
        Lock_Tag.setOnClickListener(this);
        Speedt = (Button) findViewById(R.id.button_spt);
        Speedt.setOnClickListener(this);
        Cur_Tag_Info = (TextView) findViewById(R.id.textView_epc);
        Cur_Tag_Info.setText("");
        Status = (TextView) findViewById(R.id.textView_status);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Area_Select = (Spinner) findViewById(R.id.spinner_area);
        Area_Select.setAdapter(adapter);
        Set_Tag.setEnabled(false);
        Search_Tag.setEnabled(false);
        Read_Tag.setEnabled(false);
        Write_Tag.setEnabled(false);
        Set_EPC.setEnabled(false);
        Set_Password.setEnabled(false);
        Lock_Tag.setEnabled(false);
        Area_Select.setEnabled(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            myDeviceControl4.PowerOffDevice();
            myDeviceControl6.PowerOffDevice();
//            iuhfService.CloseDev();
            wK.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (arg0 == Read_Tag) {
            if (current_tag_epc == null) {
                Status.setText(R.string.Status_No_Card_Select);
                return;
            }
            ReadTagDialog readTag = new ReadTagDialog(this, iuhfService
                    , Area_Select.getSelectedItemPosition(), current_tag_epc, modle);
            readTag.setTitle(R.string.Item_Read);
            readTag.show();
        } else if (arg0 == Write_Tag) {
            if (current_tag_epc == null) {
                Status.setText(R.string.Status_No_Card_Select);
                return;
            }
            WriteTagDialog writeTag = new WriteTagDialog(this, iuhfService,
                    Tag_Content.getText().toString(), Area_Select.getSelectedItemPosition()
                    , current_tag_epc, modle);
            writeTag.setTitle(R.string.Item_Write);
            writeTag.show();
        } else if (arg0 == Search_Tag) {

            SearchTagDialog searchTag = new SearchTagDialog(this, iuhfService, modle);
            searchTag.setTitle(R.string.Item_Choose);
            searchTag.show();

        } else if (arg0 == Set_Tag) {
            SetModuleDialog setDialog = new SetModuleDialog(this, iuhfService, modle);
            setDialog.setTitle(R.string.Item_Set_Title);
            setDialog.show();

        } else if (arg0 == Set_Password) {
            if (current_tag_epc == null) {
                Status.setText(R.string.Status_No_Card_Select);
                return;
            }
            SetPasswordDialog setPasswordDialog = new SetPasswordDialog(this
                    , iuhfService, current_tag_epc, modle);
            setPasswordDialog.setTitle(R.string.SetPasswd_Btn);
            setPasswordDialog.show();
        } else if (arg0 == Set_EPC) {
            if (current_tag_epc == null) {
                Status.setText(R.string.Status_No_Card_Select);
                return;
            }
            SetEPCDialog setEPCDialog = new SetEPCDialog(this, iuhfService, current_tag_epc);
            setEPCDialog.setTitle(R.string.SetEPC_Btn);
            setEPCDialog.show();
        } else if (arg0 == Lock_Tag) {
            if (current_tag_epc == null) {
                Status.setText(R.string.Status_No_Card_Select);
                return;
            }
            LockTagDialog lockTagDialog = new LockTagDialog(this, iuhfService
                    , current_tag_epc, modle);
            lockTagDialog.setTitle(R.string.Lock_Btn);
            lockTagDialog.show();
        } else if (arg0 == Speedt) {
            SpeedTestDialog sptd = new SpeedTestDialog(this, iuhfService);
            sptd.setTitle("Speed Test");
            sptd.show();
        }
    }

    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    Toast.makeText(UhfAct.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        UhfAct.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UhfAct Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
    }
}
