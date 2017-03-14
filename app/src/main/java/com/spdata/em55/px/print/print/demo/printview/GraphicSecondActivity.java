package com.spdata.em55.px.print.print.demo.printview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.spdata.em55.R;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.spdata.em55.px.print.utils.preDefiniation;

import java.io.InputStream;

import rego.printlib.export.regoPrinter;


public class GraphicSecondActivity extends Activity {
	private ApplicationContext context;
	private Button pridraw;
	// 高宽设置

	public EditText wight;
	public EditText hight;

	/**
	 * 打印机控制类
	 */
	private regoPrinter mobileprinter;
	private int iObjectCode;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picdraw);
		pridraw = (Button) findViewById(R.id.button_pridraw);
		// 高宽设置

		wight = (EditText) findViewById(R.id.editText_picwide);
		hight = (EditText) findViewById(R.id.editText_pichight);
		final InputStream bitmap = this.getResources().openRawResource(
				+R.drawable.star);
		final InputStream bitmaptwo = this.getResources().openRawResource(
				+R.drawable.printico);
		context = (ApplicationContext) getApplicationContext();

		mobileprinter = context.getObject();
		iObjectCode = context.getState();

		pridraw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//控制输入,确保不为空
				String wighttext = wight.getText().toString();
				String highttext = hight.getText().toString();

				if (("".equalsIgnoreCase(wighttext))||("".equalsIgnoreCase(highttext))) {

					Toast.makeText(GraphicSecondActivity.this,
							R.string.null_wight_hight, Toast.LENGTH_SHORT).show();
					return;
				}


				context.getObject().CON_PageStart(context.getState(), true,
						Integer.parseInt(wight.getText().toString()),
						Integer.parseInt(hight.getText().toString()));
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().DRAW_SetFillMode(false,0);
				//	context.getObject().DRAW_SetLineStyle(Paint.Style.FILL);
				context.getObject().DRAW_SetLineWidth(4);

				context.getObject().DRAW_PrintRectangle(context.getState(), 10,
						100, 320, 160);
				context.getObject().DRAW_PrintLine(context.getState(), 25, 110,
						25, 140);
				context.getObject().DRAW_PrintLine(context.getState(), 25, 140,
						57, 140);
				context.getObject().DRAW_PrintLine(context.getState(), 25, 110,
						57, 140);
				context.getObject().DRAW_PrintCircle(context.getState(), 90,
						125, 15);
				context.getObject().DRAW_PrintOval(context.getState(), 120,
						110, 160, 140);
				context.getObject().DRAW_PrintPicture(context.getState(),
						bitmap, 165, 102, 53, 44);
				context.getObject().DRAW_PrintRectangle(context.getState(),
						225, 110, 250, 140);
				context.getObject().DRAW_Print1D2DBarcode(context.getState(),
						preDefiniation.BarcodeType.BT_QRcode.getValue(), 257, 100, 58, 58,
						"12345678");
				//	context.getObject().DRAW_SetRotate(context.getState(), 90);
				context.getObject().DRAW_PrintPicture(context.getState(),
						bitmaptwo, 0, 170, 268, 176);


				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());

			}
		});
	}
}
