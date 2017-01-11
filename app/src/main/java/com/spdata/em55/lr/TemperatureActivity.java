package com.spdata.em55.lr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spdata.em55.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureActivity extends Activity {
	
	 //private static final String DEVFILE_PATH = "/proc/driver/captwo";
	 private static final String HUMFILE_PATH = "/sys/class/misc/si7020/hum";
	 private static final String TEMFILE_PATH = "/sys/class/misc/si7020/tem";
	 private TextView Hum_title;
	 private TextView Tem_title;
	 private TextView Humidity;
	 private TextView Temperature;
	 private Button start;
	 private Button stop;
	 private PowerManager pM = null;
	 private WakeLock wK = null;
	 private Timer timer;
	 private Handler handler;
	 private String Hum = "0";
	 private String Tem = "0";
	 private BufferedReader read_Hum;
	 private BufferedReader read_Tem;
	 private BufferedWriter enter;
	 private boolean isRunning = false;
	 File HumName;
	 File TemName;
    /** Called when the activity is first created. */
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wsd);
        Hum_title   = (TextView) findViewById(R.id.Hum_title);
        Tem_title   = (TextView) findViewById(R.id.Tem_title);
        Humidity    = (TextView) findViewById(R.id.Hum_value);
        Temperature = (TextView) findViewById(R.id.Tem_value);
		start = (Button) findViewById(R.id.buttonstart);
        stop = (Button) findViewById(R.id.buttonstop);
        Humidity.setTextColor(Color.BLACK);
        Temperature.setTextColor(Color.BLACK);
        HumName = new File(HUMFILE_PATH);
        TemName = new File(TEMFILE_PATH);
        
        handler = new Handler() {
       	 
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what>0){
                	float[] buf = (float[])msg.obj;
                    Humidity.setText(String.valueOf(buf[0]) + "%");
                    Temperature.setText(String.valueOf(buf[1]) + "℃");
                    Log.d("######","Hum:" + String.valueOf(buf[0]) +  "Tem:" + String.valueOf(buf[1]));
                }
            }
        };
        
        start.setOnClickListener(new View.OnClickListener() {
       	 
            @Override
            public void onClick(View arg0) {
				isRunning = true;
                timer = new Timer();
                timer.schedule(new HumandTemTask(),0, 2000);
            }
        });
        
        stop.setOnClickListener(new View.OnClickListener() {
          	 
            @Override
            public void onClick(View arg0) {
            	if(timer != null){
            		timer.cancel();
            	}
            	 Humidity.setText("%");
                 Temperature.setText("℃");
            	isRunning = false;
            }
        });
        
        pM = (PowerManager) getSystemService(POWER_SERVICE);
		if (pM != null) {
			wK = pM.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockpsam");
		}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	if (wK != null) {
			wK.acquire();
		}
    	
    	if(isRunning){
    		isRunning = true;
            timer = new Timer();
            timer.schedule(new HumandTemTask(),0, 2000);
    	}
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	if (wK != null) {
			wK.release();
		}
    	
    	if(isRunning){
    		if(timer != null){
    			timer.cancel();
    		}
    	}
    }
    
    @Override
    public void onDestroy() {
		super.onDestroy();
    	/*if(timer != null){
    		timer.cancel();
    	}
    	try {
			enter = new BufferedWriter(new FileWriter(DeviceName, false));
			enter.write("off");
			enter.flush();
			enter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/		
    }
    
    class HumandTemTask extends TimerTask
    {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				read_Hum = new BufferedReader(new FileReader(HumName));
				Hum = read_Hum.readLine();
				read_Tem = new BufferedReader(new FileReader(TemName));
				Tem = read_Tem.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("#######",Hum + Tem); 
			float[] data = new float[2];
			data[0] = Float.parseFloat(Hum);
			data[1] = Float.parseFloat(Tem)/100;
			Message message = new Message();
			message.what = 1;
			message.obj = data;
			handler.sendMessage(message);
		}
    }
}