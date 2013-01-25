/**
 * 
 */
package sg.gov.nhb.ihuayu.activity.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;


/**
 * @author lixingwang
 * 
 */
public class FileUtils {
	private static final String TAG = "iHuayu:FileUtils";
	private static final String DATABASE_FILENAME = "database.sqlite";
	private String SDPATH;

	private int FILESIZE = 4 * 1024;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		Log.d(TAG, "[FileUtils] SDPATH = "+SDPATH);
	}

	/**
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
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
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
	
	public SQLiteDatabase openDatabase(Context context) {
		String DB_PATH = "/data/data/sg.gov.nhb.ihuayu/databases/";

		if ((new File(DB_PATH + DATABASE_FILENAME)).exists() == false) {
			File f = new File(DB_PATH);
			if (!f.exists()) {
				f.mkdir();
			}

			try {
				InputStream is = context.getAssets().open(DATABASE_FILENAME);
				OutputStream os = new FileOutputStream(DB_PATH + DATABASE_FILENAME);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return SQLiteDatabase.openOrCreateDatabase(DB_PATH + DATABASE_FILENAME, null);
	}
}
