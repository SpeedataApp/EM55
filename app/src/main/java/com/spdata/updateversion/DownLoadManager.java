package com.spdata.updateversion;

import android.app.ProgressDialog;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadManager {

	/**
	 * 从服务器下载apk
	 * 
	 * @param path
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static File getFileFromServer(String path, ProgressDialog pd,String name)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		// if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		Log.d("UpdateVersion", "getFileFromServer start");
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		// 获取到文件的大小
		pd.setMax(conn.getContentLength());
		InputStream is = conn.getInputStream();
		// 路径 需要root权限 /system/app
		File file = new File(Environment.getExternalStorageDirectory(),"em55.apk");
//		File file_md5 = new File("/data/etc/", "WristSideMonitor.md5");// getExternalStorageDirectory(),
																// "updata.apk");
		FileOutputStream fos = new FileOutputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buffer = new byte[1024];
		int len;
		int total = 0;
		while ((len = bis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			total += len;
			// 获取当前下载量
			pd.setProgress(total);
		}
		fos.close();
		bis.close();
		is.close();
		return file;
		/*
		 * } else{ return null; }
		 */
	}
	/*
	 * public static boolean removeFile() { boolean result = false; File file =
	 * new File(Environment.getRootDirectory(), "updata.apk");
	 * 
	 * return result; }
	 */

}
