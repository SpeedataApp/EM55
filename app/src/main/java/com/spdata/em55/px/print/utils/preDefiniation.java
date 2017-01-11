/**
 * FILE: preDefiniation.java
 *
 * 定义枚举型全局变量，表示打印预定义参数
 */
package com.spdata.em55.px.print.utils;

public class preDefiniation {

	/**
	 * text align type
	 * @author Administrator
	 *
	 */
	public enum AlignType {

		/**
		 * align left
		 */
		AT_LEFT(0),

		/**
		 * align center
		 */
		AT_CENTER(1),

		/**
		 * align right
		 */
		AT_RIGHT(2);
		private int iValue;

		private AlignType(int value) {
			iValue = value;
		}

		public int getValue() {
			return iValue;
		}

		public static AlignType getEnum(int value) {
			AlignType at = null;
			switch(value)
			{
				case 0:
					at = AT_LEFT;
					break;
				case 1:
					at = AT_CENTER;
					break;
				case 2:
					at = AT_RIGHT;
					break;
			}
			return at;
		}
	}

	/**
	 * vertical align type
	 */
	public enum ValignType {

		/**
		 * top align
		 */
		VT_TOP(0),

		/**
		 * middle align
		 */
		VT_MIDDLE(1),

		/**
		 * bottom align
		 */
		VT_BOTTOM(2);
		private int iValue;

		private ValignType(int value) {
			iValue = value;
		}

		public int getValue() {
			return iValue;
		}

		public static ValignType getEnum(int value)
		{
			ValignType vt = null;
			switch(value)
			{
				case 0:
					vt = VT_TOP;
					break;
				case 1:
					vt = VT_MIDDLE;
					break;
				case 2:
					vt = VT_BOTTOM;
					break;
			}
			return vt;
		}
	}

	/**
	 * barcode type, include 1d barcode and 2d barcode
	 * @author Administrator
	 *
	 */
	public enum BarcodeType {

		/**
		 * UPC-A barcode
		 */
		BT_UPCA(65),

		/**
		 * UPC-E barcode
		 */
		BT_UPCE(66),

		/**
		 * EAN13/JAN13 barcode
		 */
		BT_EAN13(67),

		/**
		 * EAN8/JAN8 barcode
		 */
		BT_EAN8(68),

		/**
		 * CODE39 barcode
		 */
		BT_CODE39(69),

		/**
		 * ITF barcode
		 */
		BT_CODEITF(70),

		/**
		 * codabar barcode
		 */
		BT_CODEBAR(71),

		/**
		 * code93 barcode
		 */
		BT_CODE93(72),

		/**
		 * code128 barcode
		 */
		BT_CODE128(73),

		/**
		 * 2D PDF417 barcode
		 */
		BT_PDF417(0),

		/**
		 * 2D QR barcode
		 */
		BT_QRcode(2),

		/**
		 * Data Matic barcode
		 */
		BT_DATAMATIC(1);

		private int iValue;
		private BarcodeType(int value) {
			iValue = value;
		}
		public int getValue() {
			return iValue;
		}
		public static BarcodeType getEnum(int value)
		{
			BarcodeType bt = null;
			switch(value)
			{
				case 65:
					bt = BT_UPCA;
					break;
				case 66:
					bt = BT_UPCE;
					break;
				case 67:
					bt = BT_EAN13;
					break;
				case 68:
					bt = BT_EAN8;
					break;
				case 69:
					bt = BT_CODE39;
					break;
				case 70:
					bt = BT_CODEITF;
					break;
				case 71:
					bt = BT_CODEBAR;
					break;
				case 72:
					bt = BT_CODE93;
					break;
				case 73:
					bt = BT_CODE128;
					break;
				case 0:
					bt = BT_PDF417;
					break;
				case 1:
					bt = BT_DATAMATIC;
					break;
				case 2:
					bt = BT_QRcode;
					break;
			}
			return bt;
		}
	}

	/**
	 * Graphic rotate rangles
	 * @author Administrator
	 *
	 */
	public enum RotatAngle {

		/**
		 * none
		 */
		RA_0(0),

		/**
		 * rotate 90 angle
		 */
		RA_90(90),

		/**
		 * rotate 180 angle
		 */
		RA_180(180),
		/**
		 * rotate 270 angle
		 */
		RA_270(270);
		private int iValue;

		private RotatAngle(int value) {
			iValue = value;
		}

		public int getValue() {
			return iValue;
		}
		public static RotatAngle getEnum(int value)
		{
			RotatAngle ra = null;
			switch(value)
			{
				case 0:
					ra = RA_0;
					break;
				case 90:
					ra = RA_90;
					break;
				case 180:
					ra = RA_90;
					break;
				case 270:
					ra = RA_90;
					break;
				default:
					ra = RA_90;
					break;
			}
			return ra;
		}
	}

	/**
	 * Printer status
	 * @author Administrator
	 *
	 */
	public enum PrinterStatus {

		/**
		 * unknow
		 */
		PS_UNKNOW(0),

		/**
		 * printer status error
		 */
		PS_ERROR(1),

		/**
		 * paper out
		 */
		PS_PAPAEROUT(2),

		/**
		 * printer ok
		 */
		PS_OK(3);
		private int iValue;

		private PrinterStatus(int value) {
			iValue = value;
		}

		public int getValue() {
			return iValue;
		}
	}

	/**
	 * barcode HRI position
	 * @author Administrator
	 *
	 */
	public enum Barcode1DHRI {
		/**
		 * none string
		 */
		BH_NO(0),

		/**
		 * under barcode
		 */
		BH_UNDER(1),

		/**
		 * blew the barcode
		 */
		BH_BLEW(2);
		private int iValue;

		private Barcode1DHRI(int value) {
			iValue = value;
		}

		public int getValue() {
			return iValue;
		}
		public static Barcode1DHRI getEnum(int value)
		{
			Barcode1DHRI bh = null;
			switch(value)
			{
				case 0:
					bh = BH_NO;
					break;
				case 1:
					bh = BH_UNDER;
					break;
				case 2:
					bh = BH_BLEW;
					break;
			}
			return bh;
		}
	}

	/**
	 * print mode type
	 * @author Administrator
	 *
	 */
	public enum TransferMode {
		/**
		 * standard mode print
		 */
		TM_NONE(0),

		/**
		 * RG-WP100 print protocol
		 */
		TM_DT_V1(1),
		/**
		 * RG-WP200 print protocol
		 */
		TM_DT_V2(2);

		private int iValue;

		private TransferMode(int value) {
			iValue = value;
		}

		public int getValue() {
			return iValue;
		}
		public static TransferMode getEnum(int value)
		{
			TransferMode tm = null;
			switch(value)
			{
				case 0:
					tm = TM_NONE;
					break;
				case 1:
					tm = TM_DT_V1;
					break;
				case 2:
					tm = TM_DT_V2;
					break;
			}
			return tm;
		}
	}
}
