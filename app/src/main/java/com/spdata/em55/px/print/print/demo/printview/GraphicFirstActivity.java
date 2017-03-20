package com.spdata.em55.px.print.print.demo.printview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.spdata.em55.R;
import com.spdata.em55.lr.GpsAct;
import com.spdata.em55.px.print.utils.ApplicationContext;


public class GraphicFirstActivity extends Activity {
	public AutoCompleteTextView languageText;
	public Button printLan;
	// 高宽设置
	public EditText wight;
	public EditText hight;
	public EditText X0;
	public EditText Y0;
	public ApplicationContext context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.getInstance().addActivity(GraphicFirstActivity.this);
		setContentView(R.layout.activity_language);

		wight = (EditText) findViewById(R.id.editText_lanwide);
		hight = (EditText) findViewById(R.id.editText_lanhight);

		printLan = (Button) findViewById(R.id.button_printlan);
		context = (ApplicationContext) getApplicationContext();
		languageText = (AutoCompleteTextView) findViewById(R.id.autoCompleteText_lan);
		printLan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//控制输入,确保不为空
				String wighttext = wight.getText().toString();
				String highttext = hight.getText().toString();

				if (("".equalsIgnoreCase(wighttext))||("".equalsIgnoreCase(highttext))) {

					Toast.makeText(GraphicFirstActivity.this,
							R.string.null_wight_hight, Toast.LENGTH_SHORT).show();
					return;
				}


				context.getObject().CON_PageStart(context.getState(), true,
						Integer.parseInt(wight.getText().toString()),
						Integer.parseInt(hight.getText().toString()));
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().DRAW_SetFillMode(false,0);

				// 对多语言数据进行处理一行一行处理
				int y = 25;
				int size=8;
				String str[] = languageText.getText().toString().split("\n");
				for (int i = 0; i < str.length; i++) {
					y += 25;

					context.getObject().DRAW_PrintText(context.getState(), 20,
							y, str[i], 20);
					size+=2;
				}
				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());
			}
		});
	}
}