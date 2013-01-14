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

/**
 * @author lixingwang
 *
 */
public class AudioPlayer {
	
	private static final String AUDIO_DOWNLOAD_URL = "http://ihuayu.gistxl.com/smc/";
	
	public void playAudio(Context context, String audioURL) throws Exception {
		String audioName = audioURL.split("/")[1];
		//Check the internet first
		int result = downFile(AUDIO_DOWNLOAD_URL + audioName, "/audio/", audioName);
		if(result == -1) throw new Exception("Dowload audi failed");
		play(context,"/audio/" +  audioName);
	}
	
	private void play(Context context, String fileName) throws IllegalArgumentException, IllegalStateException, IOException {
		FileUtils fileUtils = new FileUtils();  
		MediaPlayer player = new MediaPlayer();
		player.setDataSource(fileUtils.getFilePath(fileName));
		player.prepare();
		player.start();
	}
	
	private int downFile(String urlStr, String path, String fileName) throws Exception{  
	        InputStream inputStream = null;  
	        try {  
	            FileUtils fileUtils = new FileUtils();  
	              
	            if(fileUtils.isFileExist(path + fileName)){  
	                return 1;  
	            } else {  
	                inputStream = getInputStreamFromURL(urlStr);  
	                File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);  
	                if(resultFile == null){  
	                    return -1;  
	                }  
	            }  
	        }   
	        catch (Exception e) {  
	            throw new Exception(e); 
	        }  
	        finally{  
	            try {  
	            	if(inputStream != null) {
	            		inputStream.close();  
	            	}
	            } catch (IOException e) {  
	            	 throw new Exception(e); 
	            }  
	        }  
	        return 0;  
	    }  
	
	private InputStream getInputStreamFromURL(String urlStr) throws IOException {  
        HttpURLConnection urlConn = null;  
        InputStream inputStream = null;  
    	URL url  = new URL(urlStr);  
        urlConn = (HttpURLConnection)url.openConnection();  
        inputStream = urlConn.getInputStream();  
        return inputStream;  
	}  

}
