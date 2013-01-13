/**
 * 
 */
package com.ihuayu.activity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author lixingwang
 *
 */
public class DBManager {
	
	private Context context;
	public DBManager(Context context) {
		this.context = context;
	}
	
	public SQLiteDatabase getSqlDB() {
		DatabaseHelper helper = new DatabaseHelper(context, "database.sqlite");
		return helper.getWritableDatabase();
	}
	
	public Context getContext() {
		return this.context;
	}
	

}
