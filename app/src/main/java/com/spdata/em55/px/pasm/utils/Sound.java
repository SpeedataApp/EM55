package com.spdata.em55.px.pasm.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

public class Sound {
	private SoundPool soundPool;
	private int soundId;
	private boolean play = false;

	// private final String file = "/libs/CheckSound.wav";
	// private final static String tag = "Sound";
	// static byte[] buffer = null;
	// AudioTrack at = null;
	// int pcmlen = 0;
	// private javax.sound.sampled.AudioFormat audioFormat = null;
	// private SourceDataLine sourceDataLine = null;
	// private DataLine.Info dataLine_info = null;
	// private AudioInputStream audioInputStream = null;

	public Sound() {
		// MusicTest();
		palyMusic = new byte[0];
	}

	public void playAlarmSound() {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		soundId = soundPool.load(
				"/system/media/audio/alarms/Alarm_Beep_01.ogg", 0);
		soundPool.play(soundId, 1, 1, 0, 0, 1);
	}

	public void playNotificationSound() {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		soundId = soundPool.load("/system/media/audio/notifications/pixiedust.ogg",
				0);
		try {
			Thread.sleep(500);// 给予初始化音乐文件足够时间
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		soundPool.play(soundId, 1, 1, 0, 0, 1);
	}

	public void playCheckSound() {

	}

	// public void MusicTest() {
	//
	// try {
	// audioInputStream = AudioSystem.getAudioInputStream(new File(file));
	// } catch (UnsupportedAudioFileException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// audioFormat = audioInputStream.getFormat();
	// dataLine_info = new DataLine.Info(SourceDataLine.class,audioFormat);
	// try {
	// sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLine_info);
	// } catch (LineUnavailableException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// public void play() throws IOException, LineUnavailableException{
	// byte[] b = new byte[1024];
	// int len = 0;
	// sourceDataLine.open(audioFormat, 1024);
	// sourceDataLine.start();
	// while ((len = audioInputStream.read(b)) > 0){
	// sourceDataLine.write(b, 0, len);
	// }
	// audioInputStream.close();
	// sourceDataLine.drain();
	// sourceDataLine.close();
	// }
	private byte[] palyMusic;
	public void playMusic(Context context, int name) {
		synchronized (palyMusic) {
			final MediaPlayer alarmMusic;
			alarmMusic = MediaPlayer.create(context, name);
			// alarmMusic = MediaPlayer.create(this,
			// com.android.wristsidemonitor.R.raw.nswdy);
			alarmMusic.setLooping(false);
			alarmMusic.start();
			// 播放闹钟
			Handler handler1 = new Handler();
			handler1.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					alarmMusic.stop();
					alarmMusic.release();
				}
			}, 1000 * 5);
			Log.d("Sound", "---1---");
			alarmMusic.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.d("Sound", "播放完毕");
					// 根据需要添加自己的代码。。。
					// alarmMusic.stop();
					// alarmMusic.release();
				}
			});
		}
	}

}
