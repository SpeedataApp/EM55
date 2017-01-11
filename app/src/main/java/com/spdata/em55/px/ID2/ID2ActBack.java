//package com.spdata.em55.px.ID2;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.serialport.DeviceControl;
//import android.serialport.SerialPort;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.ToggleButton;
//
//import com.guoguang.jni.JniCall;
//import com.spdata.em55.R;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.StringTokenizer;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class ID2ActBack extends Activity implements OnCheckedChangeListener, OnClickListener {
//
//	private static final String TAG = "ID_DEV";
//	private SerialPort IDDev;
//	private DeviceControl DevCtrl;
//	private DeviceControl DevCtrl2;
//	private static final String SERIALPORT_PATH = "/dev/ttyMT2";
//	private int IDFd,i;
//
//	private ToggleButton powerBtn;
//	private ToggleButton autoBtn;
//	private Button	     findBtn;
//	private Button	     chooseBtn;
//	private Button	     readBtn;
//	private Button	     sendBtn;
//	private Button      readfingerBtn;
//	private TextView	 contView;
//	private EditText EditTextsend;
//	private ImageView mImageViewPhoto;
//
//	private TextView mtextname;
//	private TextView mtextsex;
//	private TextView mtextminzu;
//	private TextView mtextyear;
//	private TextView mtextmouth;
//	private TextView mtextday;
//	private TextView mtextaddr;
//	private TextView mtextnum;
//	private TextView mtextqianfa;
//	private TextView mtextqixian;
//
//	private Handler handler;
//	private ReadThread reader;
//	Timer timer;
//
//	private static final String FindCard = "aaaaaa96690003200122";
//	private static final String ChooseCard = "aaaaaa96690003200221";
//	//不带指纹   读基本信息  + 照片
//	private static final String ReadCard = "aaaaaa96690003300132";
//	//读基本信息  + 照片+指纹  AA AA AA 96 69 00 03 30 10 23
//	private static final String ReadCardFWithFinger = "aaaaaa96690003301023";
//
//	private static final String SendCode = "Send message:";
//	private static final String RevCode = "Rev message:";
//
//	private static final int swtmp1[] = {0x00,0x00,0x90}; //操作成功
//	private static final int swtmp2[] = {0x00,0x00,0x9F}; //寻找证/卡成功
//	private static final int swtmp3[] = {0x00,0x00,0x80}; //寻找证/卡失败
//	private static final int swtmp4[] = {0x00,0x00,0x81}; //选取证/卡失败
//
//	private static int flagstar = 0;
//	private static byte[] revbuf = new byte[1295];
//	private static int revlen;
//	private static byte[] idname = new byte[30];
//	private static byte[] idsex = new byte[2];
//	private static byte[] idminzu = new byte[4];
//	private static byte[] idyear = new byte[8];
//	private static byte[] idmouth = new byte[4];
//	private static byte[] idday = new byte[4];
//	private static byte[] idaddr = new byte[70];
//	private static byte[] idnum = new byte[36];
//	private static byte[] idqianfa = new byte[30];
//	private static byte[] idqishiyear = new byte[8];
//	private static byte[] idjiezhiyear = new byte[8];
//	private static byte[] idqishimouth = new byte[4];
//	private static byte[] idjiezhimouth = new byte[4];
//	private static byte[] idqishiday = new byte[4];
//	private static byte[] idjiezhiday = new byte[4];
//	private static byte[] idjiezhiall = new byte[16];
//	private static byte[] idimg = new byte[1024];
//
//	private String codeAndMinzu[][] = { { "01", "汉" }, { "02", "蒙古" },
//			{ "03", "回" }, { "04", "藏" }, { "05", "维吾尔" }, { "06", "苗" },
//			{ "07", "彝" }, { "08", "壮" }, { "09", "布依" }, { "10", "朝鲜" },
//			{ "11", "满" }, { "12", "侗" }, { "13", "瑶" }, { "14", "白" },
//			{ "15", "土家" }, { "16", "哈尼" }, { "17", "哈萨克" }, { "18", "傣" },
//			{ "19", "黎" }, { "20", "傈僳" }, { "21", "佤" }, { "22", "畲" },
//			{ "23", "高山" }, { "24", "拉祜" }, { "25", "水" }, { "26", "东乡" },
//			{ "27", "纳西" }, { "28", "景颇" }, { "29", "柯尔克孜" }, { "30", "土" },
//			{ "31", "达斡尔" }, { "32", "仫佬" }, { "33", "羌" }, { "34", "布朗" },
//			{ "35", "撒拉" }, { "36", "毛南" }, { "37", "仡佬" }, { "38", "锡伯" },
//			{ "39", "阿昌" }, { "40", "普米" }, { "41", "塔吉克" }, { "42", "怒" },
//			{ "43", "乌孜别" }, { "44", "俄罗斯" }, { "45", "鄂温克" }, { "46", "德昂" },
//			{ "47", "保安" }, { "48", "裕固" }, { "49", "京" }, { "50", "塔塔尔" },
//			{ "51", "独龙" }, { "52", "鄂伦春" }, { "53", "赫哲" }, { "54", "门巴" },
//			{ "55", "珞巴" }, { "56", "基诺" }, { "97", "其它" },
//			{ "98", "国外" } };
//
//	public byte uniteBytes(byte src0, byte src1) {
//		try {
//			byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
//					.byteValue();
//			_b0 = (byte) (_b0 << 4);
//			byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
//					.byteValue();
//			byte ret = (byte) (_b0 ^ _b1);
//			return ret;
//		} catch (Exception e) {
//			return 0;
//		}
//	}
//
//	private byte charToByte(char c) {
//		return (byte) "0123456789ABCDEF".indexOf(c);
//	}
//
//	private static int btoi(byte a) {
//		return (a < 0 ? a + 256 : a);
//	}
//	public static int byteArrayToInt(byte[] bytes) {
//		int value = 0;
//		// 由高位到低位
//		for (int i = 0; i < bytes.length; i++) {
//			int shift = (bytes.length - 1 - i) * 8;
//			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
//		}
//		return value;
//	}
//	public static String byteArrayToString(byte[] src) {
//		StringBuilder stringBuilder = new StringBuilder();
//		if (src == null || src.length <= 0) {
//			return null;
//		}
//		for (int i = 0; i < src.length; i++) {
//			int v = src[i] & 0xFF;
//			String hv = Integer.toHexString(v);
//			if (hv.length() < 2) {
//				stringBuilder.append(0);
//			}
//			stringBuilder.append(hv);
//		}
//		return stringBuilder.toString();
//	}
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.act_id2);
//
//		powerBtn = (ToggleButton)findViewById(R.id.toggleButton_power);
//		powerBtn.setOnCheckedChangeListener(this);
//		autoBtn = (ToggleButton)findViewById(R.id.button_auto);
//		autoBtn.setOnCheckedChangeListener(this);
//
//		findBtn = (Button)findViewById(R.id.button_find);
//		findBtn.setOnClickListener(this);
//		findBtn.setEnabled(false);
//
//		chooseBtn = (Button)findViewById(R.id.button_choose);
//		chooseBtn.setOnClickListener(this);
//		chooseBtn.setEnabled(false);
//
//		readBtn = (Button)findViewById(R.id.button_read);
//		readBtn.setOnClickListener(this);
//		readBtn.setEnabled(false);
//		readfingerBtn= (Button) findViewById(R.id.button_finger);
//		readfingerBtn.setOnClickListener(this);
//		readfingerBtn.setEnabled(false);
//		sendBtn = (Button)findViewById(R.id.button_send);
//		sendBtn.setOnClickListener(this);
//		sendBtn.setEnabled(false);
//
//		contView = (TextView)findViewById(R.id.tv_content);
//
//		EditTextsend = (EditText) findViewById(R.id.editText_send);
//		EditTextsend.setText("20 01");
//
//		mtextname = (TextView) findViewById(R.id.textname);
//		mtextsex = (TextView) findViewById(R.id.textsex);
//		mtextminzu = (TextView) findViewById(R.id.textminzu);
//		mtextyear = (TextView) findViewById(R.id.textyear);
//		mtextmouth = (TextView) findViewById(R.id.textmouth);
//		mtextday = (TextView) findViewById(R.id.textday);
//		mtextaddr = (TextView) findViewById(R.id.textaddr);
//		mtextnum = (TextView) findViewById(R.id.textsfz);
//		mtextqianfa = (TextView) findViewById(R.id.textqianfa);
//		mtextqixian = (TextView) findViewById(R.id.textqixian);
//
//		mImageViewPhoto = (ImageView) findViewById(R.id.imageViewPortrait);
//		id_init();
//		Log.i(TAG, "onCreate is called");
//	}
//
//	public void onDestroy() {
//
//		super.onDestroy();
//		Log.i(TAG, "onDestory is called");
//		if (IDDev != null) {
//			Log.i(TAG, "close serial port");
//			IDDev.CloseSerial(IDFd);
//			IDFd = 0;
//		}
//		if (DevCtrl != null) {
//			Log.i(TAG, "close dev power");
//			try {
////				DevCtrl.PowerOffDevice("88");
////				DevCtrl2.PowerOffDeviceTuozhan("6off");
//				DevCtrl.DeviceClose();
//			} catch (IOException e) {
//				Log.e(TAG, "close power error");
//			}
//		}
//		if(timer != null)
//		{
//			timer.cancel();
//		}
//		if(reader != null)
//		{
//			reader.interrupt();
//		}
//	}
//
//	public void id_init()
//	{
//		Log.i(TAG, "id_init is called");
//		IDDev = new SerialPort();
//		try {
//			IDDev.OpenSerial(SERIALPORT_PATH,115200);
//			IDFd = IDDev.getFd();
//			Log.i(TAG, "SerialPort is open IDFd = " + IDFd);
//		} catch (IOException e) {
//			Log.e(TAG, "open serial error");
//			return;
//		}
//		try   {
//			Thread.currentThread();
//			Thread.sleep(30);
//		}
//		catch(InterruptedException   e){}
//		try {
//			DevCtrl = new DeviceControl("/sys/class/misc/mtgpio/pin");
//			DevCtrl2 = new DeviceControl("/sys/class/misc/aw9523/gpio");
//			//DevCtrl.PowerOnDevice();
//			Log.d(TAG,"DevCtrl is open DevCtrl = " + DevCtrl);
//		} catch (IOException e) {
//			Log.e(TAG, "open power error");
//			return;
//		}
//		try   {
//			Thread.currentThread();
//			Thread.sleep(30);
//		}
//		catch(InterruptedException   e){}
//		reader = new ReadThread();//读取信息
//		reader.start();
//		handler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				if(msg.what == 1)
//				{
//					Log.i(TAG, "handler is called");
//					byte[] buf = (byte[])msg.obj;
//					if((btoi(buf[0]) == 0xaa)&&(btoi(buf[1]) == 0xaa)&&(btoi(buf[2]) == 0xaa)&&(btoi(buf[3]) == 0x96)&&(btoi(buf[4]) == 0x69))
//					{
//						int datelen = (int)btoi(buf[5]);
//						datelen <<= 8;
//						datelen += btoi(buf[6]);
//						if((datelen + 7) <= buf.length)
//						{
//							flagstar = 0;
//							revlen = buf.length;
//							System.arraycopy(buf, 0, revbuf, 0, buf.length);
//						}
//						else
//						{
//							flagstar = datelen + 7;
//							revlen = buf.length;
//							System.arraycopy(buf, 0, revbuf, 0, buf.length);
//						}
//					}
//					else
//					{
//						int buflen = buf.length;
//						if((revlen + buflen) > flagstar)
//						{
//							return ;
//						}
//						System.arraycopy(buf, 0, revbuf, revlen, buflen);
//						revlen += buf.length;
//						if(revlen == flagstar)
//						{
//							flagstar = 0;
//						}
//					}
//					if(flagstar == 0)
//					{
//						mtextname.setText("");
//						mtextsex.setText("");
//						mtextminzu.setText("");
//						mtextyear.setText("");
//						mtextmouth.setText("");
//						mtextday.setText("");
//						mtextaddr.setText("");
//						mtextnum.setText("");
//						mtextqianfa.setText("");
//						mtextqixian.setText("");
//						mImageViewPhoto.setImageBitmap(null);
//						int res = checkPackage(revbuf,revlen);
//						switch(res)
//						{
//							//1.数据头错误    2.校验错误   3.长度错误   4.选取证/卡失败  5.操作成功 6.寻找证/卡成功  7.寻找证/卡失败
//							case 0:
//								System.arraycopy(revbuf, 14, idname, 0, 30);
//								System.arraycopy(revbuf, 44, idsex, 0, 2);
//								System.arraycopy(revbuf, 46, idminzu, 0, 4);
//								System.arraycopy(revbuf, 50, idyear, 0, 8);
//								System.arraycopy(revbuf, 58, idmouth, 0, 4);
//								System.arraycopy(revbuf, 62, idday, 0, 4);
//								System.arraycopy(revbuf, 66, idaddr, 0, 70);
//								System.arraycopy(revbuf, 136, idnum, 0, 36);
//								System.arraycopy(revbuf, 172, idqianfa, 0, 30);
//								System.arraycopy(revbuf, 202, idqishiyear, 0, 8);
//								System.arraycopy(revbuf, 218, idjiezhiyear, 0, 8);
//								System.arraycopy(revbuf, 210, idqishimouth, 0, 4);
//								System.arraycopy(revbuf, 226, idjiezhimouth, 0, 4);
//								System.arraycopy(revbuf, 214, idqishiday, 0, 4);
//								System.arraycopy(revbuf, 230, idjiezhiday, 0, 4);
//								System.arraycopy(revbuf, 218, idjiezhiall, 0, 16);
//								byte a[] = byteArrayToAscii(idsex).getBytes();
//								if(btoi(a[0]) == '0')
//								{
//									mtextsex.setText("未知");
//								}
//								else if(btoi(a[0]) == '1')
//								{
//									mtextsex.setText("男");
//								}
//								else if(btoi(a[0]) == '2')
//								{
//									mtextsex.setText("女");
//								}
//								else if(btoi(a[0]) == '9')
//								{
//									mtextsex.setText("未说明");
//								}
//
//								try {
//									String mssg =  new String(idname, "UTF-16LE");
//									mtextname.setText(mssg.substring(0,4));
//								} catch (UnsupportedEncodingException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//
//								byte b[] = new byte[2];
//								byte c[] = byteArrayToAscii(idminzu).getBytes();
//								b[0] = c[0];
//								b[1] = c[2];
//								for(i = 0;i < 6;i++)
//								{
//									if(byteArrayToAscii(b).substring(0, 2).equals(codeAndMinzu[i][0]))
//									{
//										mtextminzu.setText(codeAndMinzu[i][1]);
//										break;
//									}
//								}
//								try {
//									String mssgA =  new String(idaddr, "UTF-16LE");
//									mtextaddr.setText(mssgA);
//								} catch (UnsupportedEncodingException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//
//								try {
//									String mssgb =  new String(idqianfa, "UTF-16LE");
//									mtextqianfa.setText(mssgb);
//								} catch (UnsupportedEncodingException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								mtextyear.setText(byteArrayToAscii(idyear));
//								mtextmouth.setText(byteArrayToAscii(idmouth));
//								mtextday.setText(byteArrayToAscii(idday));
//								mtextnum.setText(byteArrayToAscii(idnum));
//								if(idjiezhiyear[0] >= '0' && idjiezhiyear[0] <= '9')
//								{
//									mtextqixian.setText(byteArrayToAscii(idqishiyear) + "."  + byteArrayToAscii(idqishimouth) + "." + byteArrayToAscii(idqishiday) + "-" + byteArrayToAscii(idjiezhiyear) + "."+ byteArrayToAscii(idjiezhimouth) + "."+ byteArrayToAscii(idjiezhiday));
//								}
//								else
//								{
//									try {
//										String sjiezhi =  new String(idjiezhiall, "UTF-16LE");
//										mtextqixian.setText(byteArrayToAscii(idqishiyear) + "."  + byteArrayToAscii(idqishimouth) + "." + byteArrayToAscii(idqishiday) + "-" + sjiezhi);
//									} catch (UnsupportedEncodingException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
////								contView.setText("照片如下：");
////								contView.append("\n");
//								System.arraycopy(revbuf, 270, idimg, 0, 1024);
//								byte[] bmp = new byte[14 + 40 + 308 * 126];
//								int ret = JniCall.buf2Bmp(idimg, bmp);
//								Bitmap bitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.length);
//								mImageViewPhoto.setImageBitmap(bitmap);
//								break;
//							case 1:
//								contView.setText("数据头错误");
//								break;
//							case 2:
//								contView.setText("校验错误");
//								break;
//							case 3:
//								contView.setText("长度错误");
//								break;
//							case 4:
//								contView.setText("选取证/卡失败 ");
//								break;
//							case 5:
//								contView.setText("操作成功");
//								break;
//							case 6:
//								contView.setText("寻找证/卡成功");
//								break;
//							case 7:
//								contView.setText("寻找证/卡失败");
//								break;
//							default:
//								contView.setText("没有发现身份证");
//								break;
//						}
//					}
//				}
//			}
//		};
//
//	}
//	/**
//	 * byte[]->ascii String {0x71,0x72,0x73,0x41,0x42}->"qrsAB"
//	 *
//	 * @param cmds
//	 * @return
//	 */
//	public static String byteArrayToAscii(byte[] cmds) {
//		int tRecvCount = cmds.length;
//		StringBuffer tStringBuf = new StringBuffer();
//		String nRcvString;
//		char[] tChars = new char[tRecvCount];
//		for (int i = 0; i < tRecvCount; i++) {
//			tChars[i] = (char) cmds[i];
//		}
//		tStringBuf.append(tChars);
//		nRcvString = tStringBuf.toString(); // nRcvString从tBytes转成了String类型的"123"
//		return nRcvString;
//	}
//
//	class RemindTask extends TimerTask{
//		public void run(){
//			try{
//				Log.i(TAG, "TimerTask is called");
//				byte[] cardtemp = null;
//				// TODO Auto-generated method stub
//				cardtemp = HexString2Bytes(FindCard);
//				IDDev.WriteSerialByte(IDFd, cardtemp);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				cardtemp = HexString2Bytes(ChooseCard);
//				IDDev.WriteSerialByte(IDFd, cardtemp);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				cardtemp = HexString2Bytes(ReadCard);
//				IDDev.WriteSerialByte(IDFd, cardtemp);
//			}catch(Exception e){
//			}
//		}
//	}
//
//	@Override
//	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//		// TODO Auto-generated method stub
//		if(arg0 == powerBtn)
//		{
//			if(arg1)
//			{
//				Log.i(TAG, "powerBtn is called on");
//				try {
////					DevCtrl.PowerOnDevice("88");
////					DevCtrl2.PowerOnDeviceTuozhan("6on");
//					Log.d(TAG,"power on");
//					try   {
//						Thread.currentThread();
//						Thread.sleep(100);
//					}
//					catch(InterruptedException   e){}
//				} catch (Exception e) {
//					Log.e(TAG, "open power error");
//					//return;
//				}
////		        reader = new ReadThread();
////		        reader.start();
//				findBtn.setEnabled(true);
//				findBtn.setEnabled(true);
//				chooseBtn.setEnabled(true);
//				readBtn.setEnabled(true);
//				readfingerBtn.setEnabled(true);
//				sendBtn.setEnabled(true);
//			}
//			else
//			{
//				findBtn.setEnabled(false);
//				findBtn.setEnabled(false);
//				chooseBtn.setEnabled(false);
//				readBtn.setEnabled(false);
//				readfingerBtn.setEnabled(false);
//				sendBtn.setEnabled(false);
//				Log.i(TAG, "powerBtn is called off");
//				if(timer != null)
//				{
//					timer.cancel();
//				}
//					try {
////						DevCtrl.PowerOffDevice("88");
////						DevCtrl2.PowerOffDeviceTuozhan("6off");
//					} catch (Exception e) {
//						contView.setText(R.string.Status_ManipulateFail);
//					}
//				autoBtn.setChecked(false);
//			}
//		}
//		else if(arg0 == autoBtn)
//		{
//			if(arg1)
//			{
//				Log.i(TAG, "autoBtn is called on");
//				try {
////                    DevCtrl.PowerOnDevice("88");
////                    DevCtrl2.PowerOnDeviceTuozhan("6on");
//					Log.d(TAG,"power on");
//					try   {
//						Thread.currentThread();
//						Thread.sleep(100);
//					}
//					catch(InterruptedException   e){}
//				} catch (Exception e) {
//					contView.setText(R.string.Status_ManipulateFail);
//				}
//				//reader = new ReadThread();
//				//reader.start();
////				try   {
////				    Thread.currentThread();
////					Thread.sleep(30);
////				}
////				catch(InterruptedException   e){}
//				timer = new Timer();
//				timer.schedule(new RemindTask(), 20, 3000);
//				findBtn.setEnabled(false);
//				findBtn.setEnabled(false);
//				chooseBtn.setEnabled(false);
//				readBtn.setEnabled(false);
//				readfingerBtn.setEnabled(false);
//				sendBtn.setEnabled(false);
//			}
//			else
//			{
//				Log.i(TAG, "autoBtn is called off");
//				findBtn.setEnabled(false);
//				findBtn.setEnabled(false);
//				chooseBtn.setEnabled(false);
//				readBtn.setEnabled(false);
//				readfingerBtn.setEnabled(false);
//				sendBtn.setEnabled(false);
//				if(timer != null)
//				{
//					timer.cancel();
//				}
//				try {
//					//reader.interrupt();
////                    DevCtrl.PowerOffDevice("88");
////                    DevCtrl2.PowerOffDeviceTuozhan("6off");
//				} catch (Exception e) {
//					contView.setText(R.string.Status_ManipulateFail);
//				}
//				powerBtn.setChecked(false);
//			}
//		}
//	}
//
//	public byte[] sendStringToBytes(String sendstring) {
//		sendstring = sendstring.toUpperCase();
//		int length = sendstring.length() / 2;
//		char[] hexchars = sendstring.toCharArray();
//		byte[] temp = new byte[length];
//		for (int i = 0; i < length; i++) {
//			int pos = i * 2;
//			temp[i] = (byte) (charToByte(hexchars[pos]) << 4 | charToByte(hexchars[pos + 1]));
//		}
//
//		return temp;
//	}
//
//	public byte[] HexString2Bytes(String src) {
//		byte[] ret = new byte[src.length() / 2];
//		byte[] tmp = src.getBytes();
//		for (int i = 0; i < src.length() / 2; i++) {
//			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
//		}
//		return ret;
//	}
//
//	/**
//	 * 异或指令
//	 *
//	 * @param data
//	 * @return
//	 */
//	private byte xor(byte[] buf,int len)
//	{
//		int i;
//		byte a,b;
//
//		a = buf[0];
//		for(i = 1;i < len;i++)
//		{
//			a = (byte)(a^buf[i]);
//		}
//		Log.d(TAG, "xor =" + String.format("%02x", a) + "\n");
//		return a;
//	}
//
//	/**
//	 * 打包指令
//	 *
//	 * @param cmd data
//	 * @return
//	 */
//	private byte[] cmddataPackage(byte[] cmd,byte[] data) {
//		byte[] result = new byte[cmd.length + data.length + 8];
//		byte[] xorbuf = new byte[cmd.length + data.length + 2];
//		result[0] = (byte) 0xaa;
//		result[1] = (byte) 0xaa;
//		result[2] = (byte) 0xaa;
//		result[3] = (byte) 0x96;
//		result[4] = (byte) 0x69;
//		result[5] = 0x00;
//		result[6] = (byte)(cmd.length+ data.length + 2);
//		System.arraycopy(cmd, 0, result, 7, cmd.length);
//		System.arraycopy(data, 0, result, 7 + cmd.length, data.length);
//		System.arraycopy(result, 5, xorbuf, 0, xorbuf.length);
//		result[result.length - 1] = xor(xorbuf,xorbuf.length);
//
//		return result;
//	}
//
//	/**
//	 * 打包指令
//	 *
//	 * @param cmd
//	 * @return
//	 */
//	private byte[] cmdPackage(byte[] cmd) {
//		byte[] result = new byte[cmd.length + 8];
//		byte[] xorbuf = new byte[cmd.length + 2];
//		result[0] = (byte) 0xaa;
//		result[1] = (byte) 0xaa;
//		result[2] = (byte) 0xaa;
//		result[3] = (byte) 0x96;
//		result[4] = (byte) 0x69;
//		result[5] = 0x00;
//		result[6] = (byte)(cmd.length + 1);
//		System.arraycopy(cmd, 0, result, 7, cmd.length);
//		System.arraycopy(result, 5, xorbuf, 0, xorbuf.length);
//		result[result.length - 1] = xor(xorbuf,xorbuf.length);
//
//		return result;
//	}
//
//	/**
//	 * 校验指令
//	 *
//	 * @param buf len
//	 * @return
//	 * 1.数据头错误    2.校验错误   3.长度错误   4.选取证/卡失败  5.操作成功 6.寻找证/卡成功  7.寻找证/卡失败
//	 */
//	private int checkPackage(byte[] buf,int len) {
//
//		int res = 100;
//		byte[] xorbuf = new byte[len - 6];
//
//		if((btoi(buf[0]) != 0xaa)||(btoi(buf[1]) != 0xaa)||(btoi(buf[2]) != 0xaa)||(btoi(buf[3]) != 0x96)||(btoi(buf[4]) != 0x69))
//		{
//			res = 1;
//			return res;
//		}
//		System.arraycopy(buf, 5, xorbuf, 0, len - 6);
//		byte xorflag = xor(xorbuf,xorbuf.length);
//		if(xorflag != buf[len -1])
//		{
//			res = 2;
//			return res;
//		}
//
//		int datelen = (int)btoi(buf[5]);
//		datelen <<= 8;
//		datelen += btoi(buf[6]);
//		if(datelen != (len - 7))
//		{
//			res = 3;
//			return res;
//		}
//
//		if(swtmp4[2] == btoi(buf[9]))
//		{
//			res = 4;
//		}
//		else if((swtmp1[0] == btoi(buf[7]))&&(swtmp1[1] == btoi(buf[8]))&&(swtmp1[2] == btoi(buf[9])))
//		{
//			res = 5;
//		}
//		else if((swtmp2[0] == btoi(buf[7]))&&(swtmp2[1] == btoi(buf[8]))&&(swtmp2[2] == btoi(buf[9])))
//		{
//			res = 6;
//		}
//		else if((swtmp3[0] == btoi(buf[7]))&&(swtmp3[1] == btoi(buf[8]))&&(swtmp3[2] == btoi(buf[9])))
//		{
//			res = 7;
//		}
//		if(len == 1295)
//		{
//			res = 0;
//		}
//		return res;
//	}
//
//	@Override
//	public void onClick(View view) {
//
//		byte[] cardtemp = null;
//		// TODO Auto-generated method stub
//		if(view == findBtn)
//		{
//			Log.d(TAG, "findBtn IDFd = " + IDFd);
//			contView.setText("");
//			cardtemp = HexString2Bytes(FindCard);
//			IDDev.WriteSerialByte(IDFd, cardtemp);
//		}
//		else if(view == chooseBtn)
//		{
//			contView.setText("");
//			cardtemp = HexString2Bytes(ChooseCard);
//			IDDev.WriteSerialByte(IDFd, cardtemp);
//		}
//		else if(view == readBtn)
//		{
//			contView.setText("");
//			cardtemp = HexString2Bytes(ReadCard);
//			IDDev.WriteSerialByte(IDFd, cardtemp);
//		}
//		else if(view == readfingerBtn)
//		{
//			contView.setText("");
//			cardtemp = HexString2Bytes(ReadCardFWithFinger);
//			IDDev.WriteSerialByte(IDFd, cardtemp);
//		}
//		else if(view == sendBtn)
//		{
//			String temp1 = EditTextsend.getText().toString();
//			byte[] temp2 = temp1.getBytes();
//			String l = new String();
//			for(byte i : temp2)
//			{
//				l += String.format("%c", i);
//			}
//
//			StringTokenizer tk = new StringTokenizer(l.toString());
//			int nums = tk.countTokens();
//			int index = 0;
//			byte[] cm = new byte[nums];
//			while (tk.hasMoreTokens()) {
//				try {
//					cm[index++] = (byte) Integer.parseInt(tk.nextToken(), 16);
//					if(index == nums)
//						break;
//				} catch (NumberFormatException p) {
//					return;
//				}
//			}
//			byte[] senddate = cmdPackage(cm);
//			IDDev.WriteSerialByte(IDFd, senddate);
//		}
//	}
//
//	class ReadThread extends Thread {
//		public void run() {
//			super.run();
//			Log.d(TAG, "thread start");
//			while(!isInterrupted()) {
//				Message msg = new Message();
//				//Log.d(TAG, "thread start Message");
//				byte[] buf;
//				try {
//					buf = IDDev.ReadSerial(IDFd,128);
//					Log.d(TAG, "thread start buf");
//				} catch (IOException e) {
//					Log.e(TAG, "ReadSerial error");
//					return;
//				}
//				if(buf != null)
//				{
//					Log.d(TAG, "read end");
//					msg.what = 1;
//					msg.obj = buf;
//					handler.sendMessage(msg);
//				}
//			}
//			Log.d(TAG, "thread stop");
//		}
//	}
//}
