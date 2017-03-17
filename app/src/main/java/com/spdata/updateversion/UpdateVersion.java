package com.spdata.updateversion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateVersion {

    private final String TAG = this.getClass().getName();

    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;

    private UpdataInfo info;
    private String localVersion;
    private Context mContext;
    private byte[] downLoadApkLock;
    private byte[] startUpdate;

    public UpdateVersion(Context context) {
        mContext = context;
        downLoadApkLock = new byte[0];
        startUpdate = new byte[0];
    }

    public void startUpdate() {
        synchronized (startUpdate) {
            try {
                localVersion = getVersionName();
                System.out.println("localVersion--" + localVersion);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            CheckVersionTask cv = new CheckVersionTask();
            new Thread(cv).start();
        }
    }

    /*
     * 获取当前程序的版本号
     */
    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(
                mContext.getPackageName(), 0);
        return packInfo.versionName;
    }

    /*
     * 从服务器获取xml解析并进行比对版本号
     */
    public class CheckVersionTask implements Runnable {

        public void run() {
            try {
                // 从资源文件获取服务器 地址
                // http://218.247.237.138/speedataApps/kt55/em55_version.xml
                String path = "http://218.247.237.138/speedataApps/kt55/em55_version_new.xml";
                // 包装成url的对象
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(50000);
                InputStream is = conn.getInputStream();
                info = UpdataInfoParser.getUpdataInfo(is);
                System.out
                        .println("VersionActivity            ----------->          info = "
                                + info);
                if (info.getVersion().equals(localVersion)) {
                    Log.i(TAG, "版本号相同无需升级");
                    Message msg = new Message();
                    msg.what = UPDATA_NONEED;
                    handler.sendMessage(msg);
                    // LoginMain();
                } else {
                    Log.i(TAG, "版本号不同 ,提示用户升级 ");
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                // 待处理
                e.printStackTrace();
                Log.i(TAG, "Exception");
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_NONEED:
                    Toast.makeText(mContext, "版本号相同无需升级", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case UPDATA_CLIENT:
                    // 对话框通知用户升级程序
                    Toast.makeText(mContext, "可以升级程序啦~", Toast.LENGTH_SHORT).show();
                    downLoadApk();
                    // showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    // 服务器超时
                    Toast.makeText(mContext, "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    // LoginMain();
                    break;
                case SDCARD_NOMOUNTED:
                    // sdcard不可用
                    Toast.makeText(mContext, "SD卡不可用", Toast.LENGTH_SHORT).show();
                    break;
                case DOWN_ERROR:
                    // 下载apk失败
                    Toast.makeText(mContext, "下载新版本失败", Toast.LENGTH_SHORT).show();
                    // LoginMain();
                    break;
            }
        }
    };

	/*
	 * 
	 * 弹出对话框通知用户更新程序
	 * 
	 * 弹出对话框的步骤： 1.创建alertDialog的builder. 2.要给builder设置属性, 对话框的内容,样式,按钮
	 * 3.通过builder 创建一个对话框 4.对话框show()出来
	 */
	/*
	 * protected void showUpdataDialog() { AlertDialog.Builder builer = new
	 * Builder(this); builer.setTitle("版本升级");
	 * builer.setMessage(info.getDescription()); // 当点确定按钮时从服务器上下载 新的apk 然后安装
	 * builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int which) { Log.i(TAG,
	 * "下载apk,更新"); downLoadApk(); } }); // 当点取消按钮时进行登录
	 * builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int which) { // TODO
	 * Auto-generated method stub // LoginMain(); } }); AlertDialog dialog =
	 * builer.create(); dialog.show(); }
	 */

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        synchronized (downLoadApkLock) {
            final ProgressDialog pd; // 进度条对话框
            pd = new ProgressDialog(mContext);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("正在下载更新");
			/*
			 * if (!Environment.getExternalStorageState().equals(
			 * Environment.MEDIA_MOUNTED)) { Message msg = new Message();
			 * msg.what = SDCARD_NOMOUNTED; handler.sendMessage(msg); } else {
			 */
            pd.show();
            new Thread() {
                @Override
                public void run() {
                    try {
                        File file = DownLoadManager.getFileFromServer(
                                info.getUrl(), pd, "em55_new.apk");
//						File filemd5 = DownLoadManager.getFileFromServer(
//								info.getMd5(), pd, "md5.tar");
                        sleep(1000);
                        installApk(file);
                        pd.dismiss(); // 结束掉进度条对话框
                    } catch (Exception e) {
                        pd.dismiss();
                        Message msg = new Message();
                        msg.what = DOWN_ERROR;
                        handler.sendMessage(msg);
                        e.printStackTrace();
                    }
                }
            }.start();

        }
        // }
    }

    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        Log.d(TAG, "installApk start");
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        Log.d(TAG, "installApk end");
    }

}