/**
 * 
 */
package sg.gov.nhb.ihuayu.activity.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * @author lixingwang
 * 
 */
public class FileUtils {
	private static final String TAG = "iHuayu:FileUtils";
	private String SDPATH;

	private int FILESIZE = 4 * 1024;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		// 寰楀埌褰撳墠澶栭儴瀛樺偍璁惧鐨勭洰褰� /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		Log.d(TAG, "[FileUtils] SDPATH = "+SDPATH);
	}

	/**
	 * 鍦⊿D鍗′笂鍒涘缓鏂囦欢
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 鍦⊿D鍗′笂鍒涘缓鐩綍
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 鍒ゆ柇SD鍗′笂鐨勬枃浠跺す鏄惁瀛樺湪
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	public String getFilePath(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.getAbsolutePath();
	}

	/**
	 * 灏嗕竴涓狪nputStream閲岄潰鐨勬暟鎹啓鍏ュ埌SD鍗′腑
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			// while((input.read(buffer)) != -1){
			// output.write(buffer);
			// }
			int len = 0;
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);

			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
