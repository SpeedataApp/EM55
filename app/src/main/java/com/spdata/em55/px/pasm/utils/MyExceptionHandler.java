package com.spdata.em55.px.pasm.utils;

import android.content.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyExceptionHandler implements UncaughtExceptionHandler {
	protected static final String SAVE_EXCEPTION_FILE_PARENT_PATH = "";

	protected static final String SAVE_EXCEPTION_FILE_NAME = "";

	@SuppressWarnings("unused")
	private Context context;

	private static MyExceptionHandler exceptionHandler = new MyExceptionHandler();
	UncaughtExceptionHandler defaultExceptionHandler;

	private MyExceptionHandler() {
	}

	public static MyExceptionHandler getInstanceMyExceptionHandler() {
		return exceptionHandler;
	}

	/**
	 * 初始化方法
	 * 
	 * @param context
	 *            上下文对象
	 */
	public void init(Context context) {
		this.context = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 异常处理方法
	 * 
	 * @Params Thread对象
	 * @param Throwable对象
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(thread, ex) && defaultExceptionHandler != null) {
			defaultExceptionHandler.uncaughtException(thread, ex);
		}
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				Intent intent = new Intent(context, MenuHome.class)
//						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				Log.d("uncaughtException","restart MenuHome");
//				context.startActivity(intent);
//				Looper.loop();
//			}
//		}.start();
		 //杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	// 程序异常处理方法
	private boolean handleException(Thread thread, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		long startTimer = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
		Date firstDate = new Date(System.currentTimeMillis()); // 第一次创建文件，也就是开始日期
		String str = formatter.format(firstDate);
		// sb.append(startTimer);
		// sb.append("\n");
		sb.append(str); // 把当前的日期写入到字符串中
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		String errorresult = writer.toString();
		sb.append(errorresult);
		sb.append("\n");
		try {
			FileUtil.write(context, "errlog", sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

}
