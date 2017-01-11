package com.spdata.em55.px.print.print.demo.printview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.spdata.em55.R;
import com.spdata.em55.px.print.utils.ApplicationContext;
import com.spdata.em55.px.print.utils.preDefiniation;


public class GraphicThirdActivity extends Activity {
	public int[] fs;
	public int[] at;
	public int[] vat;
	public boolean[] bValid;
	public String[] strCell;
	public Button lab;
	public Button fire;
	public Button move;
	public Button shopping;
	//public Button deskBtn;
	public ApplicationContext context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picapply);

		lab = (Button) findViewById(R.id.Button_pictable);
		fire = (Button) findViewById(R.id.Button_picfire);
		shopping = (Button) findViewById(R.id.Button_picshopping);
		move = (Button) findViewById(R.id.button_picmovie);
		//deskBtn = (Button) findViewById(R.id.Button_desktop);
		context = (ApplicationContext) getApplicationContext();

		lab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.getObject().CON_PageStart(context.getState(), true,
						900, 1000);
				int[] hRow = { 50, 50, 50, 50, 50, 50 };// 6行
				int[] wColumn = { 50, 150, 100, 100, 100, 100, 100, 100 };// 8列
				context.getObject().DRAW_Table(context.getState(), 0, 700, 900,
						400, 6, 8, hRow, wColumn, 4);
				bValid = new boolean[8];
				bValid[0] = true;
				bValid[1] = true;
				bValid[2] = true;
				bValid[3] = true;
				bValid[4] = true;
				bValid[5] = true;
				bValid[6] = true;
				bValid[7] = true;
				strCell = new String[8];
				strCell[0] = "型号";
				strCell[1] = "对账单号";
				strCell[2] = "业务类型";
				strCell[3] = "单据金额";
				strCell[4] = "已收";
				strCell[5] = "应收";
				strCell[6] = "已免收";
				strCell[7] = "欠款";
				fs = new int[8];
				fs[0] = 20;
				fs[1] = 20;
				fs[2] = 20;
				fs[3] = 20;
				fs[4] = 20;
				fs[5] = 20;
				fs[6] = 20;
				fs[7] = 20;
				at = new int[8];
				at[0] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[1] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[2] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[3] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[4] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[5] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[6] = preDefiniation.AlignType.AT_CENTER.getValue();
				at[7] = preDefiniation.AlignType.AT_CENTER.getValue();
				vat = new int[8];
				vat[0] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[1] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[2] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[3] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[4] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[5] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[6] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				vat[7] = preDefiniation.ValignType.VT_MIDDLE.getValue();
				context.getObject().DRAW_TableRow(context.getState(), bValid,
						strCell, fs, at, vat);// 第1行
				strCell[0] = "1";
				strCell[1] = "CG002011";
				strCell[2] = "批发";
				strCell[3] = "441.00";
				strCell[4] = "0.00";
				strCell[5] = "0.00";
				strCell[6] = "441.00";
				strCell[7] = "0.00";
				context.getObject().DRAW_TableRow(context.getState(), bValid,
						strCell, fs, at, vat);// 第2行
				strCell[0] = "2";
				context.getObject().DRAW_TableRow(context.getState(), bValid,
						strCell, fs, at, vat);// 第3行
				strCell[0] = "3";
				context.getObject().DRAW_TableRow(context.getState(), bValid,
						strCell, fs, at, vat);// 第4行
				bValid[0] = false;
				bValid[1] = false;
				bValid[2] = false;
				bValid[3] = false;
				bValid[4] = false;
				bValid[5] = false;
				bValid[6] = false;
				bValid[7] = true;
				at = new int[1];
				at[0] = preDefiniation.AlignType.AT_LEFT.getValue();
				strCell = new String[1];
				strCell[0] = "                               合计                         1323.00         0.00          0.00         1323.00         0.00 ";
				context.getObject().DRAW_TableRow(context.getState(), bValid,
						strCell, fs, at, vat);// 第5行
				at[0] = preDefiniation.AlignType.AT_LEFT.getValue();
				strCell[0] = "本单收款（大写）    壹仟叁佰贰拾叁";
				context.getObject().DRAW_TableRow(context.getState(), bValid,
						strCell, fs, at, vat);// 第6行
				context.getObject().DRAW_SetRotate(context.getState(), 90);
				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());

			}
		});
		fire.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				context.getObject().CON_PageStart(context.getState(), true,
						384, 300);
				context.getObject().DRAW_SetFillMode(false,0);
				context.getObject().DRAW_SetLineWidth(2);
				context.getObject().DRAW_PrintText(context.getState(), 325, 30,
						"北京", 14);
				context.getObject().DRAW_PrintCircle(context.getState(), 373,
						33, 15);
				context.getObject().DRAW_PrintText(context.getState(), 364, 30,
						"售", 14);
				context.getObject().DRAW_PrintText(context.getState(), 50, 60,
						"北 京", 22);
				context.getObject().DRAW_PrintText(context.getState(), 290, 60,
						"武 汉", 22);
				context.getObject().DRAW_PrintText(context.getState(), 175, 50,
						"K250次", 20);
				context.getObject().DRAW_PrintLine(context.getState(), 180, 80,
						240, 80);
				context.getObject().DRAW_PrintLine(context.getState(), 230, 80,
						220, 75);
				context.getObject().DRAW_PrintText(context.getState(), 55, 100,
						"Beijing", 16);
				context.getObject().DRAW_PrintText(context.getState(), 300, 95,
						"Wuhan", 16);
				context.getObject().DRAW_PrintText(context.getState(), 20, 125,
						"2015年02月15日09:08开", 14);
				context.getObject().DRAW_PrintText(context.getState(), 280,
						125, "12车22号下铺", 16);
				context.getObject().DRAW_PrintText(context.getState(), 23, 150,
						"￥109元", 22);
				context.getObject().DRAW_PrintText(context.getState(), 270,
						155, "新空调硬卧", 16);
				context.getObject().DRAW_PrintText(context.getState(), 20, 185,
						"仅限当日当次车", 16);

				context.getObject().DRAW_PrintText(context.getState(), 35, 260,
						"123456789012345", 16);
				context.getObject().DRAW_Print1D2DBarcode(context.getState(),
						preDefiniation.BarcodeType.BT_QRcode.getValue(), 320, 200, 80, 80,
						"火车票");
				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());
			}
		});
		shopping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int size = 20;
				context.getObject().CON_PageStart(context.getState(), true,
						576, 600);
				context.getObject().DRAW_SetFillMode(false,0);
				context.getObject().DRAW_SetLineWidth(2);
				context.getObject().DRAW_PrintText(context.getState(), 100, 5,
						"欢迎光临", 25);
				context.getObject().DRAW_PrintText(context.getState(), 110, 35,
						"店铺名称", size);

				context.getObject().DRAW_PrintText(context.getState(), 5, 60,
						"收银员：收银员名称", size);
				context.getObject().DRAW_PrintText(context.getState(), 5, 85,
						"时间：2015-01-01", size);
				context.getObject().DRAW_PrintLine(context.getState(), 2, 105,
						300, 105);
				context.getObject().DRAW_PrintText(context.getState(), 5, 125,
						"商品名称           单价        数量        金额", 22);

				context.getObject().DRAW_PrintText(context.getState(), 5, 150,
						"花生                      2.00          1       2.00",
						size);
				context.getObject().DRAW_PrintLine(context.getState(), 2, 170,
						300, 170);
				context.getObject().DRAW_PrintText(context.getState(), 5, 190,
						"合计:￥100元", size);
				context.getObject().DRAW_PrintText(context.getState(), 5, 215,
						"优惠金额:￥90元", size);
				context.getObject().DRAW_PrintLine(context.getState(), 2, 235,
						300, 235);
				context.getObject().DRAW_PrintText(context.getState(), 5, 255,
						"电话：22222222222", size);

				context.getObject().DRAW_PrintText(context.getState(), 5, 280,
						"地址：北京得实大厦", size);
				context.getObject().DRAW_PrintText(context.getState(), 5, 305,
						"谢谢惠顾，欢迎下次光临！", size);

				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());
			}
		});
		move.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				context.getObject().CON_PageStart(context.getState(), true,
						384, 384);
				context.getObject().DRAW_SetFillMode(false,0);
				context.getObject().DRAW_SetLineWidth(4);
				context.getObject().DRAW_PrintText(context.getState(), 95, 25,
						"瑞工国际影城", 25);
				context.getObject().DRAW_PrintLine(context.getState(), 95, 65,
						300, 65);
				// context.getObject().DRAW_PrintText(context.getState(),95, 70,
				// "beijng", 20);
				context.getObject().DRAW_SetLineWidth(2);
				context.getObject().DRAW_PrintLine(context.getState(), 0, 90,
						390, 90);
				context.getObject().DRAW_PrintText(context.getState(), 85, 110,
						"请在电影开始前入场", 18);
				// 矩形
				context.getObject().DRAW_PrintRectangle(context.getState(), 18,
						165, 180, 235);
				context.getObject().DRAW_PrintRectangle(context.getState(),
						185, 165, 375, 235);
				context.getObject().DRAW_PrintRectangle(context.getState(), 18,
						240, 375, 300);
				context.getObject().DRAW_PrintRectangle(context.getState(), 18,
						305, 180, 350);
				context.getObject().DRAW_PrintRectangle(context.getState(),
						185, 305, 375, 350);
				context.getObject().DRAW_PrintRectangle(context.getState(), 18,
						360, 375, 410);
				// 文字
				context.getObject().DRAW_PrintText(context.getState(), 25, 185,
						"影厅", 22);
				context.getObject().DRAW_PrintText(context.getState(), 195,
						185, "时间", 22);
				context.getObject().DRAW_PrintText(context.getState(), 25, 255,
						"影片", 22);
				context.getObject().DRAW_PrintText(context.getState(), 25, 315,
						"座位", 22);
				context.getObject().DRAW_PrintText(context.getState(), 195,
						315, "票价", 22);
				// 变化文字
				context.getObject().DRAW_PrintText(context.getState(), 70, 180,
						"得实大厦", 22);
				context.getObject().DRAW_PrintText(context.getState(), 240,
						180, "2015年2月1号12:00", 22);
				context.getObject().DRAW_PrintText(context.getState(), 75, 250,
						"心花怒放", 22);
				context.getObject().DRAW_PrintText(context.getState(), 70, 315,
						"01排01号", 22);
				context.getObject().DRAW_PrintText(context.getState(), 235,
						315, "80元", 22);
				context.getObject().DRAW_Print1D2DBarcode(context.getState(),
						preDefiniation.BarcodeType.BT_UPCA.getValue(), 70, 365, 240, 30,
						"123456789012");

				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());
			}
		});


	}
}