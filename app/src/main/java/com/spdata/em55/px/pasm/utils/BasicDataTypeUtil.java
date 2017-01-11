package com.spdata.em55.px.pasm.utils;

public class BasicDataTypeUtil {

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return 0;
		}
		hexString = hexString.toUpperCase();
		if (hexString.length() == 1) {
			hexString = "0" + hexString;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d[0];
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
