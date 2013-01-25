/**
 * 
 */
package sg.gov.nhb.ihuayu.activity.db;

import sg.gov.nhb.ihuayu.activity.rest.FileUtils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author lixingwang
 *
 */
public class DBSqlite {
	
	private Context context;
	public DBSqlite(Context context) {
		this.context = context;
	}
	
	public SQLiteDatabase getSqlDB() {
//		DatabaseHelper helper = new DatabaseHelper(context, "database.sqlite");
//		return helper.getWritableDatabase();
		return new FileUtils().openDatabase(getContext());
	}
	
	public Context getContext() {
		return this.context;
	}
	

}
