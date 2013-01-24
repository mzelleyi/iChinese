/**
 * 
 */
package sg.gov.nhb.ihuayu.activity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author lixingwang
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String TAG = "iHuayu:DatabaseHelper";
//	private SQLiteDatabase db = null;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG,"create ihuayu databases");
//		db.execSQL("CREATE TABLE Dialog (Dialog_ID integer,Title_ID integer,Sentence_Sequence_ID  varchar(512),Gender  varchar(512),Narrator varchar(512),Sentence varchar(1024),Sentence_EN varchar(1024),Sentece_PY varchar(512),Sentence_Audio_Link varchar(18))");
//		db.execSQL("CREATE TABLE Dialog_Keyword (ID integer,Dialog_ID integer,Src_Keyword  varchar(1024),Dest_Keyword varchar(1024), Keyword_PY varchar(1024))");
		db.execSQL("CREATE TABLE name (id INTEGER, language_dir VARCHAR(10), keyword VARCHAR(1024),keyword_length INTEGER, src VARCHAR(1024), destination VARCHAR(1024), chinese_audio_link VARCHAR(1024), chinese_py_with_tone TEXT, dict_category CHAR(50), sample_sentence_EN VARCHAR(1024), sample_sentence_CN VARCHAR(1024), sample_sentence_PY VARCHAR(1024), sample_sentence_CN_Audio_link VARCHAR(1024))");
//		db.execSQL("CREATE TABLE Favorites (Dictionary_ID integer,Update_date datetime)");
//		db.execSQL("CREATE TABLE Information (Version char(10),Last_Update datetime)");
//		db.execSQL("CREATE TABLE Scenario_Category (Title_ID  integer, Title_EN  varchar(512), Title_CN  varchar(512),Title_PY  varchar(512))");
//		db.execSQL("CREATE TABLE Usage_Tracking (Keyword varchar(1024), Update_date datetime)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		Log.d(TAG,"upgrade a database");

	}

}
