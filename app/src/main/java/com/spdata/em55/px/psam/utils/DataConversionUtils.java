package com.spdata.em55.px.psam.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 串口部分常用数据转换方法
 * @author Echo
 * @date 2015-6-29
 * @version 1.0
 */
public class DataConversionUtils {
	
	/**
	 * 
	 * hexString-byte[] "130632199104213021"->{0x13,0x06....,0x21}
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByteArray(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	/**
	 * byte[]->String {0x23,0x32,0x12}-->"233212" 比如从卡里解析出身份证
	 * 
	 * @param src
	 * @return
	 */
	public static String byteArrayToString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * byte[]->String {0x23,0x32,0x12}-->"23 32 12" 查看log
	 * 
	 * @param res
	 * @param len
	 * @return
	 */
	public static String byteArrayToStringLog(byte[] res, int len) {
		String result = "";
		for (int i = 0; i < len; i++) {
			result += String.format("%02x ", res[i]);
		}
		return result;
	}
	
	/**
	 * byte[]->ascii String {0x71,0x72,0x73,0x41,0x42}->"qrsAB"
	 * 
	 * @param cmds
	 * @return
	 */
	public static String byteArrayToAscii(byte[] cmds) {
		int tRecvCount = cmds.length;
		StringBuffer tStringBuf = new StringBuffer();
		String nRcvString;
		char[] tChars = new char[tRecvCount];
		for (int i = 0; i < tRecvCount; i++) {
			tChars[i] = (char) cmds[i];
		}
		tStringBuf.append(tChars);
		nRcvString = tStringBuf.toString(); // nRcvString从tBytes转成了String类型的"123"
		return nRcvString;
	}


	/**
	 * byte[]转int
	 *
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < bytes.length; i++) {
			int shift = (bytes.length - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}



	/**
	 * 
	 * @param format  example:yyyyMMddHHmmss
	 * @return
	 */

	public static String getCurrentTime(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}
    /**
     *
     *
     * @return
     */

    public static String getDefautCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }


	public static byte[] intToByteArray1(int i) {
		byte[] result = new byte[4];
		result[0] = (byte)((i >> 24) & 0xFF);
		//必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i  & 0xFF000000);
		//，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。
		// 再提下数//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，
		// 比如int转为//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
		// 虽//然 result[0] = (byte)(i  & 0xFF000000); 这样不行，
		// 但是我们可以这样 result[0] = (byte)((i  & //0xFF000000) >>24);
		result[1] = (byte)((i >> 16) & 0xFF);
		result[2] = (byte)((i >> 8) & 0xFF);
		result[3] = (byte)(i & 0xFF);
		return result;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 *
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 *
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		try {
			byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
					.byteValue();
			_b0 = (byte) (_b0 << 4);
			byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
					.byteValue();
			byte ret = (byte) (_b0 ^ _b1);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}
}
