package com.spdata.em55.px.print.print.demo.secondview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spdata.em55.R;
import com.spdata.em55.px.print.print.demo.firstview.ConnectAvtivity;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.spdata.em55.px.print.utils.TXTUtil;
import com.spdata.em55.px.print.utils.preDefiniation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class PrintModeActivity extends Activity implements OnClickListener {
	public Button text;
	public Button pic;
	public Bundle b;


	private ApplicationContext mContext;

	// 进纸,出纸,设置灰度,打印参数,版本号显示
	private Button btnIn, btnOut;
	private Button btnSertGray;
	private EditText evGrayLevel;
	private Button mButtonPrintSettings;
	private TextView tvVersion;

	private Spinner spCoding;
	private String code;
	// 压力测试
	private ToggleButton btntest;
	private Button btnPrintTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printmode);
		mContext = (ApplicationContext) getApplicationContext();
		text = (Button) findViewById(R.id.button_text);
		btnPrintTxt = (Button) findViewById(R.id.btn_print_txt);
		btnPrintTxt.setOnClickListener(this);
		pic = (Button) findViewById(R.id.button_pic);
		mButtonPrintSettings = (Button) findViewById(R.id.button_print_setting);
		tvVersion = (TextView) findViewById(R.id.tv_version);

		//打印TXT
		spCoding=(Spinner) findViewById(R.id.sp_coding);
		spCoding.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2==0){
					code="GBK";
				}else{
					code="UTF-8";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 初始化并监听
		btnIn = (Button) findViewById(R.id.button_paper_in);
		btnOut = (Button) findViewById(R.id.button_paper_out);
		// btnOut.setVisibility(View.GONE);
		// btnIn.setVisibility(View.GONE);
		btnSertGray = (Button) findViewById(R.id.button_set_gray);
		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
		btnSertGray.setOnClickListener(this);
		evGrayLevel = (EditText) findViewById(R.id.ev_gray_level);
		btntest = (ToggleButton) findViewById(R.id.button_test);

//		tvVersion.setText(getVersion());
//
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PrintModeActivity.this,
						com.spdata.em55.px.print.print.demo.printview.TextTabsActivity.class);
				startActivity(intent);
			}
		});

		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PrintModeActivity.this,
						com.spdata.em55.px.print.print.demo.printview.GraphicTabsActivity.class);
				// 图形是true
				startActivity(intent);
			}
		});

		mButtonPrintSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContext.printSettings();
			}
		});
		// 压力测试按钮监听
		btntest.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					startTest();
				} else {
					stopTest();
				}
			}
		});
		//机型:tt43   4.0.3系统暂时不显示打印txt
		if (android.os.Build.VERSION.RELEASE.equals("4.0.3")) {
			btnPrintTxt.setVisibility(View.GONE);
			spCoding.setVisibility(View.GONE);
		}


	}

	// 压力测试

	private final int period = 1000;
	private Timer timer;

	private class TestTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 打印
			String param = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			byte[] bytes = param.getBytes();
			mContext.getObject().ASCII_PrintBuffer(mContext.getState(), bytes,
					bytes.length);
			mContext.getObject().ASCII_PrintBuffer(mContext.getState(),
					new byte[] { 0x0a }, 1);
		}
	}

	private void stopTest() {
		timer.cancel();
		timer = null;
	}

	private void startTest() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(new TestTimerTask(), 1000, period);

	}

	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			String wrongVersion = this.getString(R.string.wrong_version);
			return wrongVersion;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 判断是否按下“BACK”(返回)键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 直接销毁程序
			exitProgram();
			// 返回true以表示消费事件，避免按默认的方式处理“BACK”键的事件
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exitProgram() {
		// TODO Auto-generated method stub

		// 先finishConnectActivity
		ConnectAvtivity.mActivity.finish();
		finish();
	}

	// 点击选择

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.button_paper_in:
				testIN();
				break;

			case R.id.button_paper_out:
				testUnroll((byte) 0x0a);
				break;

			case R.id.button_set_gray:

				String string = evGrayLevel.getText().toString();
				if (!"".equalsIgnoreCase(string)) {
					int level = Integer.parseInt(string);
					// 1b 06 1b fd 20 1b 16
					if (level >= 30) {
						Toast.makeText(PrintModeActivity.this,
								R.string.gary_value_large, Toast.LENGTH_SHORT)
								.show();
						return;
					}
					setLevel(level);
					Toast.makeText(PrintModeActivity.this, R.string.gary_set_ok,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(PrintModeActivity.this,
							R.string.gary_null_input, Toast.LENGTH_SHORT).show();
					return;
				}

				break;
			case R.id.btn_print_txt:
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("*/*");
				startActivityForResult(i, MY_REQUEST_CODE);
				break;

		}
	}

	private final static int MY_REQUEST_CODE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MY_REQUEST_CODE)// 自定义的一个static final int常量
		{
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				// new AlertDialog.Builder(mContext).setMessage(uri+"").show();
				ContentResolver resolver = getContentResolver();
				// ContentResolver对象的getType方法可返回形如content://的Uri的类型
				// 如果是一张图片，返回结果为image/jpeg或image/png等
				String fileType = resolver.getType(uri);
				if (fileType.startsWith("text"))// 判断用户选择的是否为图片
				{
					// 根据返回的uri获取图片路径
//					Cursor cursor = resolver.query(uri,
//							new String[] { MediaStore.Images.Media.DATA },
//							null, null, null);
//					cursor.moveToFirst();
//					String path = cursor.getString(cursor
//							.getColumnIndex(MediaStore.Images.Media.DATA));
//					String firstUri=uri.toString();
//					String secondUri=firstUri.substring(firstUri.indexOf("//"), firstUri.length());
//					String thirdUri=secondUri.replace("%3A", "/").replace("//", "");
//					String resultUri=thirdUri.replace("%2F", "/");
					com.spdata.em55.px.print.print.demo.secondview.NewGetPath newGetPath=new com.spdata.em55.px.print.print.demo.secondview.NewGetPath();
					String resultUri=newGetPath.getPath(mContext, uri);
					String resultTXT= TXTUtil.readTxtFile(resultUri.toString(),code);
					byte[] gbks = null;
					try {
						gbks=resultTXT.getBytes(code);
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					mContext.getObject().ASCII_PrintBuffer(mContext.getState(),gbks, gbks.length);

					Toast.makeText(PrintModeActivity.this, resultTXT + "",
							Toast.LENGTH_LONG).show();
					// do anything you want
				}
			}
		}

	}

	// testIN进纸

	private void testIN() {

		final EditText edvInputLine = new EditText(this);

		edvInputLine.setInputType(InputType.TYPE_CLASS_NUMBER);
		edvInputLine.setText("2");
		edvInputLine
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_remind_input_line)
				.setView(edvInputLine)
				.setNegativeButton(R.string.dialog_cancel, null)
				.setPositiveButton(R.string.dialog_sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {

								if (!edvInputLine.getText().toString()
										.equals("")) {
									int lines = Integer.parseInt(edvInputLine
											.getText().toString());
									mContext.PrintNLine(lines);

								}
							}
						}).show();

	}

	// 出纸

	private void testUnroll(byte lenght) {

		byte[] setCmd = new byte[4];
		setCmd[0] = 0x1b;
		setCmd[1] = 0x6a;
		setCmd[2] = lenght;
		setCmd[3] = (byte) 0x00;

		mContext.getObject().ASCII_PrintBuffer(mContext.getState(), setCmd,
				setCmd.length);
	}

	// 设置灰度
	private void setLevel(int level) {
		// TODO Auto-generated method stub
		byte[] setCmd = new byte[7];
		setCmd[0] = 0x1b;
		setCmd[1] = 0x06;
		setCmd[2] = 0x1b;
		setCmd[3] = (byte) 0xfd;
		setCmd[4] = (byte) level;// (level - 1);
		setCmd[5] = 0x1b;
		setCmd[6] = 0x16;
		mContext.getObject().ASCII_PrintBuffer(mContext.getState(), setCmd,
				setCmd.length);
		System.out.println("setOk");
	}

	public void zeb() {
		mContext.getObject()
				.CON_PageStart(mContext.getState(), false, 480, 320);
		// 第一行
		ArrayList<String> param = new ArrayList<String>();
		param.add("28");
		param.add("30");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("10-19 08:27");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("20");
		param.add("60");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);
		// 第2行
		param.clear();
		param.add("20");
		param.add("110");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("30");
		param.add("114");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("东街揽投部");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("198");
		param.add("122");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("局收 号码");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("30");
		param.add("160");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("福州55件");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("198");
		param.add("162");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("局发 重量");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("20");
		param.add("150");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);
		param.clear();
		param.add("20");
		param.add("190");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("260");
		param.add("8");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("2");
		param.add("N");
		param.add("邮特2014");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("303");
		param.add("57");
		param.add("0");
		param.add("8");
		param.add("2");
		param.add("2");
		param.add("N");
		param.add("特快");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("347");
		param.add("118");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("AAAA");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("345");
		param.add("150");
		param.add("120");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("347");
		param.add("160");
		param.add("0");
		param.add("8");
		param.add("1");
		param.add("1");
		param.add("N");
		param.add("2.00");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("345");
		param.add("190");
		param.add("120");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("20");
		param.add("205");
		param.add("0");
		param.add("1");
		param.add("2");
		param.add("9");
		param.add("70");
		param.add("B");
		param.add("10803-100-2-121-AA");
		mContext.getObject().ASCII_Print1DBarcode(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("303");
		param.add("57");
		param.add("s4");
		param.add("123456");

		mContext.getObject().ASCII_Print2DBarcode(mContext.getState(),
				preDefiniation.BarcodeType.BT_QRcode.getValue(), param, "gb2312");

		// 打印一维条码 条码x,y,旋转, 条码窄度，条码宽度，条码高度， 可显示B/N，条码内容
		mContext.getObject().CON_PageEnd(mContext.getState(),
				mContext.getPrintway());
	}

	private void LQ58() {
		mContext.getObject().CON_PageStart(mContext.getState(), false, 55, 33);
		mContext.getObject().CON_SetPrintDirection(mContext.getState(), 0);
		// 第一行
		ArrayList<String> param = new ArrayList<String>();
		param.add("28");
		param.add("30");
		param.add("1");
		param.add("1");
		param.add("10-19 08:27");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("20");
		param.add("60");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("30");
		param.add("114");
		param.add("2");
		param.add("1");
		param.add("东街揽投部");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("20");
		param.add("150");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("20");
		param.add("155");
		param.add("40");
		param.add("1");
		param.add("2");
		param.add("10803-100-2-121-AA");
		mContext.getObject().ASCII_Print1DBarcode(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("20");
		param.add("155");
		param.add("5");
		param.add("10803");
		mContext.getObject().ASCII_Print2DBarcode(mContext.getState(),
				preDefiniation.BarcodeType.BT_QRcode.getValue(), param,
				"gb2312");
		mContext.getObject().CON_PageEnd(mContext.getState(),
				mContext.getPrintway());
	}

	public void LP80B() {
		mContext.getObject().CON_PageStart(mContext.getState(), false, 60, 40);
		mContext.getObject().CON_SetPrintDirection(mContext.getState(), 14);
		// 第一行
		ArrayList<String> param = new ArrayList<String>();
		param.add("28");
		param.add("30");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("10-19 08:27");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("20");
		param.add("60");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		// 第2行
		param.clear();
		param.add("20");
		param.add("110");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);
		param.clear();
		param.add("30");
		param.add("114");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("东街揽投部");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("198");
		param.add("122");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("局收 号码");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("30");
		param.add("160");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("福州55件");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("198");
		param.add("162");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("局发 重量");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("20");
		param.add("150");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("20");
		param.add("190");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("260");
		param.add("8");
		param.add("TSS24.BF2");

		param.add("0");// 旋转角度
		param.add("2");
		param.add("1");
		param.add("邮特2014");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("303");
		param.add("57");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("2");
		param.add("2");
		param.add("特快");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("347");
		param.add("118");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("AAAA");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("345");
		param.add("150");
		param.add("120");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("347");
		param.add("160");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("2.00");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");

		param.clear();
		param.add("345");
		param.add("190");
		param.add("120");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("5");
		param.add("205");
		param.add("39");
		param.add("40");
		param.add("1");
		param.add("0");
		param.add("2");
		param.add("4");
		param.add("10803-100-2-121-AA");
		mContext.getObject().ASCII_Print1DBarcode(mContext.getState(), param,
				"gb2312");
		// 打印一维条码 条码x,y,旋转, 条码窄度，条码宽度，条码高度， 可显示B/N，条码内容
		mContext.getObject().CON_PageEnd(mContext.getState(),
				mContext.getPrintway());
	}

	public void TSC() {
		// 宽和高是像素点640代表的是80mm
		mContext.getObject().CON_PageStart(mContext.getState(), true, 480, 400);
		mContext.getObject().CON_SetPrintDirection(mContext.getState(), 14);
		// 第一行
		ArrayList<String> param = new ArrayList<String>();

		param.clear();
		param.add("20");
		param.add("60");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);
		// 第2行
		param.clear();
		param.add("20");
		param.add("110");
		param.add("190");
		param.add("2");
		// param.add(null);
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("20");
		param.add("150");
		param.add("190");
		param.add("2");
		// param.add(null);
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);
		param.clear();
		param.add("20");
		param.add("190");
		param.add("190");
		param.add("2");
		// param.add(null);
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("345");
		param.add("150");
		param.add("120");
		param.add("2");
		// param.add(null);
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 347, 160,
				"2.00", 24);

		param.clear();
		param.add("345");
		param.add("190");
		param.add("120");
		param.add("2");
		// param.add(null);
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		param.clear();
		param.add("5");
		param.add("205");
		param.add("39");
		param.add("40");
		param.add("1");
		param.add("0");
		param.add("2");
		param.add("4");
		param.add("10803-100-2-121-AA");
		param.add(null);

		mContext.getObject().ASCII_Print1DBarcode(mContext.getState(), param,
				"gb2312");
		mContext.getObject().DRAW_PrintText(mContext.getState(), 260, 8,
				"邮特2014", 30);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 303, 57, "特快",
				48);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 347, 118,
				"AAAA", 24);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 28, 30,
				"10-19 08:27", 24);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 30, 122,
				"东街揽投部", 24);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 198, 122,
				"局收 号码", 24);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 30, 160,
				"福州55件", 24);

		mContext.getObject().DRAW_PrintText(mContext.getState(), 198, 162,
				"局发 重量", 24);

		// 打印一维条码 条码x,y,旋转, 条码窄度，条码宽度，条码高度， 可显示B/N，条码内容
		mContext.getObject().CON_PageEnd(mContext.getState(),
				mContext.getPrintway());
	}

	public void lp58a() {

		mContext.getObject().CON_PageStart(mContext.getState(), false, 50, 30);

//		mContext.getObject()
//				.ASCII_CtrlFloatPosition(mContext.getState(), 1, 22);
		byte small[] = new byte[3];
		small[0] = 0x1d;
		small[1] = 0x66;
		small[2] = 0x01;

		mContext.getObject().ASCII_PrintBuffer(mContext.getState(), small, 3);
		mContext.getObject().ASCII_Print1DBarcode(mContext.getState(),
				preDefiniation.BarcodeType.BT_CODE128.getValue(), 2, 100,
				preDefiniation.Barcode1DHRI.BH_BLEW.getValue(), "04A1WN171A081");
		mContext.getObject().CON_PageEnd(mContext.getState(),
				mContext.getPrintway());

	}

	public ArrayList<String> paramBarcode(String arg0, String arg1,
										  String arg2, String arg3, String arg4, String arg5, String arg6,
										  String arg7, String arg8, String arg9) {
		ArrayList<String> param = new ArrayList<String>();
		param.add(arg0);
		param.add(arg1);
		param.add(arg2);
		param.add(arg3);
		param.add(arg4);
		param.add(arg5);
		param.add(arg6);
		param.add(arg7);
		param.add(arg8);
		param.add(arg9);
		return param;

	}

	public ArrayList<String> paramString(int arg0, int arg1, int arg2,
										 int arg3, String arg4) {
		ArrayList<String> param = new ArrayList<String>();
		param.clear();
		param.add(arg0 + "");
		param.add(arg1 + "");
		param.add("TSS24.BF2");
		param.add("0");
		param.add(arg2 + "");
		param.add(arg3 + "");
		param.add(arg4);
		param.add(null);

		return param;
	}

	public ArrayList<String> paramLine(String arg0, String arg1, String arg2,
									   String arg3) {
		ArrayList<String> param = new ArrayList<String>();
		param.clear();
		param.add(arg0);
		param.add(arg1);
		param.add(arg2);
		param.add(arg3);

		return param;
	}

	public void LP58() {
		mContext.getObject().CON_PageStart(mContext.getState(), false, 55, 33);
		mContext.getObject().CON_SetPrintDirection(mContext.getState(), 14);
		// 第一行
		ArrayList<String> param = new ArrayList<String>();
		param.add("28");
		param.add("30");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("10-19 08:27");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		param.clear();
		param.add("20");
		param.add("60");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);

		// 第2行
		param.clear();
		param.add("20");
		param.add("110");
		param.add("190");
		param.add("2");
		mContext.getObject().ASCII_PrintLine(mContext.getState(), param);
		param.clear();
		param.add("30");
		param.add("114");
		param.add("TSS24.BF2");
		param.add("0");// 旋转角度
		param.add("1");
		param.add("1");
		param.add("东街揽投部");
		mContext.getObject().ASCII_PrintString(mContext.getState(), param,
				"gb2312");
		mContext.getObject().CON_PageEnd(mContext.getState(),
				mContext.getPrintway());
	}
}
