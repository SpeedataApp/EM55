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

import com.spdata.em55.MenuAct;
import com.spdata.em55.R;
import com.spdata.em55.px.print.print.demo.firstview.ConnectAvtivity;
import com.spdata.em55.px.print.print.demo.printview.GraphicTabsActivity;
import com.spdata.em55.px.print.print.demo.printview.TextTabsActivity;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.spdata.em55.px.print.utils.TXTUtil;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;


public class PrintModeActivity extends Activity implements OnClickListener {
	public Button text;
	public Button pic;


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
		ApplicationContext.getInstance().addActivity(PrintModeActivity.this);
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
		btnSertGray = (Button) findViewById(R.id.button_set_gray);
		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
		btnSertGray.setOnClickListener(this);
		evGrayLevel = (EditText) findViewById(R.id.ev_gray_level);
		btntest = (ToggleButton) findViewById(R.id.button_test);
		tvVersion.setText(getVersion());

		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PrintModeActivity.this,
						TextTabsActivity.class);
				startActivity(intent);
			}
		});

		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PrintModeActivity.this,
						GraphicTabsActivity.class);
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
	@Override
	protected void onPause() {
		super.onPause();
		if (timer!=null){
			stopTest();
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
		try {
			ConnectAvtivity.mActivity.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PrintModeActivity.this.finish();
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
					NewGetPath newGetPath=new NewGetPath();
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
}
