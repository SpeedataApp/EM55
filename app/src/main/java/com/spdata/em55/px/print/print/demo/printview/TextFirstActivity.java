package com.spdata.em55.px.print.print.demo.printview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.spdata.em55.R;
import com.spdata.em55.lr.GpsAct;
import com.spdata.em55.px.print.utils.ApplicationContext;


public class TextFirstActivity extends Activity {
	public AutoCompleteTextView text;
	public Button PrintText;


	public int alignType;

	// 文字设置
	public CheckBox dwight;
	public CheckBox dhight;
	public CheckBox dthick;
	public CheckBox underline;
	public CheckBox small;
	public CheckBox OppositeColor;
	// 尺寸设置

	public EditText wight;
	public EditText hight;
	public EditText X0;
	public EditText Y0;
	// 对齐方式
	public RadioGroup align;
	public RadioButton left;
	public RadioButton center;
	public RadioButton right;
	public int state = 1;
	public ApplicationContext context;
	private Context mContext;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.getInstance().addActivity(TextFirstActivity.this);
		setContentView(R.layout.activity_general);
		mContext = this;
		// 尺寸设置
		wight = (EditText) findViewById(R.id.editText01);
		wight.setText("60");
		hight = (EditText) findViewById(R.id.editText02);
		hight.setText("40");
		// 文字设置
		dwight = (CheckBox) findViewById(R.id.checkBox01);
		dhight = (CheckBox) findViewById(R.id.checkBox02);
		dthick = (CheckBox) findViewById(R.id.checkBox03);
		underline = (CheckBox) findViewById(R.id.checkBox04);
		small = (CheckBox) findViewById(R.id.checkBox05);
		OppositeColor = (CheckBox) findViewById(R.id.checkBox07);

		// 对齐方式
		align = (RadioGroup) findViewById(R.id.radioGroup1);
		left = (RadioButton) findViewById(R.id.radio0);
		center = (RadioButton) findViewById(R.id.radio1);
		right = (RadioButton) findViewById(R.id.radio2);


		text = (AutoCompleteTextView) findViewById(R.id.autoCompleteText_text);
		PrintText = (Button) findViewById(R.id.button_printtext);

		context = (ApplicationContext) getApplicationContext();



		//设置监听
		align.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				alignType=0;
				if (center.isChecked()) {
					alignType = 1;
				}
				if (right.isChecked()) {
					alignType = 2;
				}
				context.setAlignType(alignType);

			}
		});


		PrintText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//控制输入,确保不为空
				String wighttext = wight.getText().toString();
				String highttext = hight.getText().toString();

				if (("".equalsIgnoreCase(wighttext))||("".equalsIgnoreCase(highttext))) {
					Toast.makeText(TextFirstActivity.this,
							R.string.null_wight_hight, Toast.LENGTH_SHORT).show();
					return;
				}

				context.getObject().CON_PageStart(context.getState(), false,
						Integer.parseInt(wight.getText().toString()),
						Integer.parseInt(hight.getText().toString()));

				{

					// 对齐方式

					alignType=0;
					if (center.isChecked()) {
						alignType = 1;
					}
					if (right.isChecked()) {
						alignType = 2;
					}

					context.getObject().ASCII_CtrlAlignType(context.getState(),
							alignType);

					context.getObject().ASCII_CtrlOppositeColor(
							context.getState(), OppositeColor.isChecked());
					if (dthick.isChecked()) {
						context.getObject().ASCII_PrintBuffer(
								context.getState(),
								new byte[] { 0x1b, 0x45, 0x01 }, 3);

					} else {
						// 取消加粗打印指令
						context.getObject().ASCII_PrintBuffer(
								context.getState(),
								new byte[] { 0x1b, 0x45, 0x00 }, 3);
					}

					/**
					 * 设置大小 范围为1~8之间
					 *
					 * @param size
					 */

					context.getObject().ASCII_PrintString(context.getState(),
							dwight.isChecked() ? 1 : 0,
							dhight.isChecked() ? 1 : 0,
							dthick.isChecked() ? 1 : 0,
							underline.isChecked() ? 1 : 0,
							small.isChecked() ? 1 : 0,
							text.getText().toString(), "gb2312");
					context.getObject().ASCII_CtrlFeedLines(context.getState(),
							1);
					context.getObject().ASCII_CtrlPrintCRLF(context.getState(),
							1);
				}
				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());
			}
		});
	}

	/**
	 * 设置大小 范围为1~8之间
	 *
	 * @param size
	 */
	public void SetSize(int size) {
		context.getObject().ASCII_PrintBuffer(context.getState(),
				new byte[] { 0x1b, 0x57, (byte) size }, 3);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		SharedPreferences prefs = getPreferences(0);
		String restoredText = prefs.getString("text", null);
		if (restoredText != null) {
			text.setText(restoredText, TextView.BufferType.EDITABLE);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//关闭保存功能
		//SharedPreferences.Editor editor = getPreferences(0).edit();
		//editor.putString("text", text.getText().toString());
		//editor.commit();
	}
}