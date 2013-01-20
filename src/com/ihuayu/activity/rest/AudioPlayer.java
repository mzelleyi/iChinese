/**
 * 
 */
package com.ihuayu.activity.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * @author lixingwang
 *
 */
public class AudioPlayer {
	
	private static final String	TAG				= "iHuayu:AudioPlayer";
	private static final String AUDIO_DOWNLOAD_URL = "http://ihuayu.gistxl.com/smc";
	
	public void playAudio(Context context, String audioURL) throws Exception {
		Log.d(TAG, "[playAudio] + Begin");
		Log.d(TAG, "[playAudio] + audioURL = "+audioURL);
		String audioName = audioURL.split("/")[1];
		Log.d(TAG, "[playAudio] audioName = "+audioName);
		//Check the internet first
		int result = downFile("/audio/", audioName);
		if(result == -1) throw new Exception("Dowload audio failed");
		play(context,"/audio/" +  audioName);
	}
	
	private void play(Context context, String fileName) throws IllegalArgumentException, IllegalStateException, IOException {
		FileUtils fileUtils = new FileUtils();  
		MediaPlayer player = new MediaPlayer();
		player.setDataSource(fileUtils.getFilePath(fileName));
		player.prepare();
		player.start();
	}
	
	private int downFile(String path, String fileName) throws Exception {
		Log.d(TAG, "[downFile] + Begin");
//		Log.d(TAG, "[downFile] urlStr = "+ urlStr);
		Log.d(TAG, "[downFile] path = "+ path);
		Log.d(TAG, "[downFile] fileName = "+ fileName);
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();

			if (fileUtils.isFileExist(path + fileName)) {
				Log.d(TAG, "[downFile] the audio file is exist");
				return 1;
			} else {
				Log.d(TAG, "[downFile] the audio file isn't exist, do download");
				Log.d(TAG, "[downFile] urlStr = "+ AUDIO_DOWNLOAD_URL + path + fileName);
				inputStream = getInputStreamFromURL(AUDIO_DOWNLOAD_URL + path + fileName);
				File resultFile = fileUtils.write2SDFromInput(path, fileName,inputStream);
				if (resultFile == null) {
					Log.e(TAG, "[downFile] write to sdcard failed");
					return -1;
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				throw new Exception(e);
			}
		}
		return 0;
	}
	
	private InputStream getInputStreamFromURL(String urlStr) throws IOException {
		Log.d(TAG, "[getInputStreamFromURL] urlStr = "+urlStr);
        HttpURLConnection urlConn = null;  
        InputStream inputStream = null;  
    	URL url  = new URL(urlStr);  
        urlConn = (HttpURLConnection)url.openConnection();  
        inputStream = urlConn.getInputStream();  
        return inputStream;  
	}  

}
