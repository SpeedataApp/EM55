package com.spdata.em55.gxandurx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spdata.em55.R;
import com.speedata.libuhf.IUHFService;

/**
 * Created by 张明_ on 2016/12/28.
 */

public class SetModuleDialog extends Dialog implements android.view.View.OnClickListener {

    private final String[] freq_area_item = {"840-845", "920-925", "902-928", "865-868", "..."};
    private Button setf,back;
    private TextView status;
    private Spinner lf;
    private boolean seted = false;
    private Button setp;
    private EditText pv;
    private IUHFService iuhfService;
    private String model;
    private Context mContext;


    public SetModuleDialog(Context context, IUHFService iuhfService, String model) {
        super(context);
        this.iuhfService = iuhfService;
        this.model = model;
        this.mContext = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sss);

        setf = (Button) findViewById(R.id.button_set_region);
        setf.setOnClickListener(this);
        back = (Button) findViewById(R.id.button_set_back);
        back.setOnClickListener(this);
        status = (TextView) findViewById(R.id.textView_set_status);
        setp = (Button) findViewById(R.id.button_set_antenna);
        setp.setOnClickListener(this);
        setp.setEnabled(false);
        pv = (EditText) findViewById(R.id.editText_antenna);

        lf = (Spinner) findViewById(R.id.spinner_region);
        ArrayAdapter<String> tmp = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, freq_area_item);
        tmp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lf.setAdapter(tmp);

        int re = iuhfService.get_freq_region();
        if (re == iuhfService.REGION_CHINA_920_925) {
            lf.setSelection(1, true);
        } else if (re == iuhfService.REGION_CHINA_840_845) {
            lf.setSelection(0, true);
        } else if (re == iuhfService.REGION_CHINA_902_928) {
            lf.setSelection(2, true);
        } else if (re == iuhfService.REGION_EURO_865_868) {
            lf.setSelection(3, true);
        } else {
            lf.setSelection(4, true);
            status.setText("read region setting read failed");
            Log.e("r2000_kt45", "read region setting read failed");
        }

        int ivp = iuhfService.get_antenna_power();
        if (ivp > 0) {
            setp.setEnabled(true);
            pv.setText("" + ivp);
        }
        if (model.equals("as3992")) {
            pv.setHint("0关天线1开天线");
            setp.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == setf) {
            int freq_region = lf.getSelectedItemPosition();
            if (freq_region >= 4) {
                status.setText("Invalid select");
            } else {
                if (iuhfService.set_freq_region(freq_region) < 0) {
                    status.setText("set freq region failed");
                } else {
                    status.setText("set freq region ok");
                    back.setText("update settings");
                    this.setCancelable(false);
                }
            }

        } else if (v == back) {
            new toast_thread().setr("update settings now").start();
            dismiss();
        } else if (v == setp) {
            int ivp = Integer.parseInt(pv.getText().toString());
            if ((ivp < 0) || (ivp > 30)) {
                status.setText("value range is 0 ~ 30");
            } else {
                int rv = iuhfService.set_antenna_power(ivp);
                if (rv < 0) {
                    status.setText("set antenna power failed");
                } else {
                    status.setText("set antenna power ok");
                    back.setText("update settings");
                    this.setCancelable(false);
                }
            }
        }
    }

    private class toast_thread extends Thread {

        String a;

        public toast_thread setr(String m) {
            a = m;
            return this;
        }

        public void run() {
            super.run();
            Looper.prepare();
            Toast.makeText(mContext, a, Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }
}
