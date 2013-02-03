/**
 * 
 */

package sg.gov.nhb.ihuayu.activity.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * @author lixingwang
 */
public class AudioPlayer {

    private static final String TAG = "iHuayu:AudioPlayer";
    private static final String AUDIO_DOWNLOAD_URL = "http://ihuayu.gistxl.com/smc";
    private static final String STORAGE_PATH = "/audio/";
    private static AudioPlayer player = null;

    /**
     * Create a new instance of AudioPlayer
     */
    public static AudioPlayer newInstance() {
        Log.d(TAG, "[newInstance] + Begin");
        if (player == null) {
            player = new AudioPlayer();
        }
        Log.d(TAG, "[newInstance] + End");
        return player;

    }

    public boolean doCheckDownloaded(String audioURL) throws Exception {
        Log.d(TAG, "[doCheckDownloaded] + Begin");
        Log.d(TAG, "[doCheckDownloaded] + audioURL = " + audioURL);
        String audioName = audioURL.split("/")[1];
        Log.d(TAG, "[doCheckDownloaded] audioName = " + audioName);
        FileUtils fileUtils = new FileUtils();
        if (fileUtils.isFileExist(STORAGE_PATH + audioName)) {
            Log.i(TAG, "[doCheckDownloaded] the audio file is exist, return true");
            return true;
        } else {
            Log.i(TAG, "[doCheckDownloaded] the audio file isn't exist, return false");
            return false;
        }
    }

    public void doPlay(String audioURL) throws Exception {
        Log.d(TAG, "[doDirectPlay] + Begin");
        Log.d(TAG, "[doDirectPlay] audioURL = " + audioURL);
        String audioName = audioURL.split("/")[1];
        Log.d(TAG, "[doDirectPlay] audioName = " + audioName);
        play(STORAGE_PATH + audioName);
        Log.d(TAG, "[doDirectPlay] + End");
    }

    public boolean doDownload(String audioURL) throws Exception {

        Log.d(TAG, "[doDownload] + Begin");
        Log.d(TAG, "[doDownload] audioURL = " + audioURL);
        String audioName = audioURL.split("/")[1];
        Log.d(TAG, "[doDownload] audioName = " + audioName);
        boolean result = false;
        try {
            result = downFile(STORAGE_PATH, audioName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (result == false)
            throw new Exception("Dowload audio failed");
        Log.d(TAG, "[doDownload] + End");
        return result;
    }

    private void play(String fileName) throws IllegalArgumentException, IllegalStateException,
            IOException {
        FileUtils fileUtils = new FileUtils();
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(fileUtils.getFilePath(fileName));
        player.prepare();
        player.start();
    }

    private boolean downFile(String path, String fileName) throws Exception {
        Log.d(TAG, "[downFile] + Begin");
        Log.d(TAG, "[downFile] path = " + path);
        Log.d(TAG, "[downFile] fileName = " + fileName);
        InputStream inputStream = null;
        try {
            FileUtils fileUtils = new FileUtils();

            // if (fileUtils.isFileExist(path + fileName)) {
            // Log.d(TAG, "[downFile] the audio file is exist");
            // return 1;
            // } else {
            Log.d(TAG, "[downFile] urlStr = " + AUDIO_DOWNLOAD_URL + path + fileName);
            inputStream = getInputStreamFromURL(AUDIO_DOWNLOAD_URL + path + fileName);
            File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);
            if (resultFile == null) {
                Log.e(TAG, "[downFile] write to sdcard failed");
                return false;
            }
            // }
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
        return true;
    }

    private InputStream getInputStreamFromURL(String urlStr) throws IOException {
        Log.d(TAG, "[getInputStreamFromURL] urlStr = " + urlStr);
        HttpURLConnection urlConn = null;
        InputStream inputStream = null;
        URL url = new URL(urlStr);
        urlConn = (HttpURLConnection) url.openConnection();
        inputStream = urlConn.getInputStream();
        return inputStream;
    }

}
