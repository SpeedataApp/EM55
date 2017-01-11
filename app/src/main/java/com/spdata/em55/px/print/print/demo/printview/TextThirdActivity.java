package com.spdata.em55.px.print.print.demo.printview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.spdata.em55.R;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.spdata.em55.px.print.utils.preDefiniation;


public class TextThirdActivity extends Activity {
	public Button fire;
	public Button move;
	public Button shopping;
	public ApplicationContext context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_textapply);
		fire = (Button) findViewById(R.id.Button_fire);
		move = (Button) findViewById(R.id.button_movie);
		shopping = (Button) findViewById(R.id.Button_shopping);
		context = (ApplicationContext) getApplicationContext();
		move.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.getObject().CON_PageStart(context.getState(),false,0,0);
				context.getObject().ASCII_CtrlAlignType(context.getState(),
						preDefiniation.AlignType.AT_CENTER.getValue());
				context.getObject().ASCII_PrintString(context.getState(),
						1, 1, 1, 0, 0, "瑞工国际影城", "gb2312");
				context.getObject().ASCII_CtrlFeedLines(context.getState(),1);
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().ASCII_CtrlPrintPosition(context.getState(),24,450);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "影厅  ", "gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "得实大厦    ", "gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "时间  ", "gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "2015年1月16号", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "      1号大厅          ",
						"gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "12:18开场", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "影片  ", "gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "清时明月      ", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "      主演：天明，小月", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "座位  ", "gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "一排01号      ", "gb2312");
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "票价  50元", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().ASCII_CtrlPrintPosition(context.getState(),24,450);
				context.getObject().ASCII_Print1DBarcode(context.getState(),
						preDefiniation.BarcodeType.BT_UPCA.getValue(), 2, 28,
						preDefiniation.Barcode1DHRI.BH_BLEW.getValue(), "123456789012");
				context.getObject().ASCII_CtrlFeedLines(context.getState(),1);
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().CON_PageEnd(context.getState(),context.getPrintway());
			}
		});
		shopping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.getObject().CON_PageStart(context.getState(),false,0,0);
				context.getObject().ASCII_CtrlAlignType(context.getState(),
						preDefiniation.AlignType.AT_CENTER.getValue());
				context.getObject().ASCII_PrintString(context.getState(),0,
						1,0, 0, 0, "欢迎光临  ", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "店铺名称  ", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_CtrlAlignType(context.getState(),
						preDefiniation.AlignType.AT_LEFT.getValue());
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "收银员：收银员名称", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "时间：2015-01-01", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "----------------------------",
						"gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "商品名称        单价      数量      金额",
						"gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0,
						0,"花生           2.00      1      2.00", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "----------------------------",
						"gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "合计:￥100元", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "优惠金额:￥90元", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "电话：01062985019", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "地址：北京得实大厦", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "谢谢惠顾，欢迎下次光临！", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().CON_PageEnd(context.getState(),context.getPrintway());

			}
		});
		fire.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.getObject().CON_PageStart(context.getState(),false,0,0);
//				 context.getObject().CON_SetPrintDirection(context.getState(),false,0,0);
				context.getObject().ASCII_CtrlAlignType(context.getState(),
						preDefiniation.AlignType.AT_RIGHT.getValue());
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "北京 售", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_CtrlAlignType(context.getState(),
						preDefiniation.AlignType.AT_CENTER.getValue());
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 1, 0, 0, "北 京  ————> 武 汉", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "Beijing         Wuhan",
						"gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "2015年02月16日01:01开   12车12号下铺",
						"gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "￥180.00元             新空调硬卧",
						"gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().ASCII_PrintString(context.getState(),0,
						0, 0, 0, 0, "仅限当日当次车", "gb2312");
				context.getObject().ASCII_CtrlPrintCRLF(context.getState(),1);
				context.getObject().ASCII_Print2DBarcode(context.getState(), preDefiniation.BarcodeType.BT_QRcode.getValue(),"123456789",8,76,2);
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().CON_PageEnd(context.getState(),context.getPrintway());
			}
		});
	}
}