package com.spdata.em55.px.pasm.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	public static void write(Context context, String filename, String content)
			throws IOException {
		if (!filename.endsWith(".txt")) {
			filename = filename + ".txt";
		}
		FileOutputStream fos = context.openFileOutput(filename,
				context.MODE_PRIVATE + Context.MODE_APPEND
						+ Context.MODE_WORLD_READABLE
						+ Context.MODE_WORLD_WRITEABLE);
		fos.write(content.getBytes());
		fos.flush();
		fos.close();
	}

	public static String read(Context context, String fileName) {
		String content = "";
		if (!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}
		try {
			FileInputStream fis = context.openFileInput(fileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int len = 0;
			byte buf[] = new byte[1024];
			while ((len = fis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			fis.close();
			content = bos.toString();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
