package com.spdata.em55.gxandurx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.spdata.em55.R;
import com.spdata.em55.gxandurx.MsgEvent;
import com.speedata.libuhf.IUHFService;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 张明_ on 2016/12/28.
 */

public class ReadTagDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Button Ok;
    private Button Cancle;
    private TextView EPC;
    private TextView Status;
    private EditText Read_Addr;
    private EditText Read_Count;
    private EditText Password;
    private IUHFService iuhfService;
    private String current_tag_epc;
    private int which_choose;
    private String model;

    public ReadTagDialog(Context context,IUHFService iuhfService
            ,int which_choose,String current_tag_epc,String model) {
        super(context);
        // TODO Auto-generated constructor stub
        this.iuhfService=iuhfService;
        this.current_tag_epc=current_tag_epc;
        this.which_choose=which_choose;
        this.model=model;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        Ok = (Button) findViewById(R.id.btn_read_ok);
        Ok.setOnClickListener(this);
        Cancle = (Button) findViewById(R.id.btn_read_cancle);
        Cancle.setOnClickListener(this);

        EPC = (TextView) findViewById(R.id.textView_read_epc);
        EPC.setText(current_tag_epc);
        Status = (TextView) findViewById(R.id.textView_read_status);

        Read_Addr = (EditText) findViewById(R.id.editText_read_addr);
        Read_Count = (EditText) findViewById(R.id.editText_read_count);
        Password = (EditText) findViewById(R.id.editText_rp);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == Ok) {
            String str_addr = Read_Addr.getText().toString();
            String str_count = Read_Count.getText().toString();
            String str_passwd = Password.getText().toString();
            String res = iuhfService.read_area(which_choose,str_addr,str_count,str_passwd);
            if (res == null) {
                Status.setText(R.string.Status_Read_Card_Faild);
            } else {
                EventBus.getDefault().post(new MsgEvent("read_Status",res));
                dismiss();
            }
        } else if (v == Cancle) {
            dismiss();
        }
    }

}
