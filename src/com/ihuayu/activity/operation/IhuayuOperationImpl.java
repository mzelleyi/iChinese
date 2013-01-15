/**
 * 
 */
package com.ihuayu.activity.operation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ihuayu.activity.db.DBSqlite;
import com.ihuayu.activity.db.entity.Dialog;
import com.ihuayu.activity.db.entity.DialogKeywords;
import com.ihuayu.activity.db.entity.Dictionary;
import com.ihuayu.activity.db.entity.FuzzyResult;
import com.ihuayu.activity.db.entity.Scenario;

/**
 * @author lixingwang
 *
 */
public class IhuayuOperationImpl {
	private static final String TAG = "iHuayu:IhuayuOperationImpl";
	private SQLiteDatabase db = null;
	DBSqlite manager;
	
	public IhuayuOperationImpl(DBSqlite manager) {
		this.db = manager.getSqlDB();
		this.manager = manager;
	}

	public List<Scenario> queryScenario(String condition, String[] params) {
		Cursor result = this.db.rawQuery(condition, params);
		return OperationUtils.cursorToScenario(result);
	}
	
	public void insertDictionary(List<ContentValues> params) {
//		db.execSQL("CREATE TABLE name (id INTEGER, language_dir VARCHAR(10), keyword VARCHAR(1024),keyword_length INTEGER, src VARCHAR(1024), destination VARCHAR(1024), chinese_audio_link VARCHAR(1024), chinese_py_with_tone TEXT, dict_category CHAR(50), sample_sentence_EN VARCHAR(1024), sample_sentence_CN VARCHAR(1024), sample_sentence_PY VARCHAR(1024), sample_sentence_CN_Audio_link VARCHAR(1024))");
		for(ContentValues values : params) {
			long result = this.db.insert("name", null, values);
		}
	} 
	
	public void insertIntoScenarioDialogKeyWord(HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenarioDialogKeyword) {
		if(scenarioDialogKeyword != null && scenarioDialogKeyword.size() > 0) {
			Iterator<ContentValues> keyIter = scenarioDialogKeyword.keySet().iterator();
			while(keyIter.hasNext()) {
				ContentValues scenraioValues = keyIter.next();
				insertTable("Scenario_Category", scenraioValues);
				HashMap<ContentValues, List<ContentValues>> dialogKeywordsMap = scenarioDialogKeyword.get(scenraioValues);
				
				if(dialogKeywordsMap != null && dialogKeywordsMap.size() > 0) {
					Iterator<ContentValues> dialogKeyIter = dialogKeywordsMap.keySet().iterator();
					while(dialogKeyIter.hasNext()) {
						ContentValues dialogValues = keyIter.next();
						insertTable("Dialog", dialogValues);
						
						List<ContentValues> keyWordValues = dialogKeywordsMap.get(dialogValues);
						for(ContentValues keywordValues : keyWordValues) {
							insertTable("Dialog_Keyword", keywordValues);
						}
					}
				}
				
			}
		}
		
	}
	
	public long insertToBookmark(String dictionaryID) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("Dictionary_ID", dictionaryID);
			contentValues.put("Update_date", getStandardTimeStr());
			return this.db.insert("Favorites", null, contentValues);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public List<Dictionary> getAllBookmarks() {
		try {
			//TODO. maybe have performance issue.
			Cursor result = this.db.rawQuery("select * from dictionary where id in (select Dictionary_ID from Favorites)", null);
			return OperationUtils.cursorToDictionary(result);
		} catch (Exception e) {
			return null;
		}
	}
	
	public int getBookmark(int dictionaryId) {
		try {
			Log.d(TAG, "[getBookmark] + Begin");
			//TODO. maybe have performance issue.
			Cursor result = this.db.rawQuery("select * from Favorites where Dictionary_ID = ?", new String[]{dictionaryId + ""});
			//return result.getInt(result.getColumnIndex("Dictionary_ID"));
			Log.d(TAG, "[getBookmark] Cursor Count = "+result.getCount());
			return result.getCount();
		} catch (Exception e) {
			return -1;
		}
	}
	
	public int removeFromFavorites(int dictionaryId) {
		int result = this.db.delete("Favorites", "Dictionary_ID = ?", new String[]{dictionaryId + ""});
		return result;
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getStandardTimeStr() throws ParseException {
		DateFormat  format2 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date(); 
		return format2.format(now);
	}
	
	public long insertTable(String name, ContentValues contentValues) {
		long result = this.db.insert(name, null, contentValues);
		return result;
	} 
	
	public List<Dialog> queryDialog(String condition, String[] params) {
		Cursor result = this.db.rawQuery(condition, params);
		return OperationUtils.cursorToDialog(result);
	}
	
	public List<Dictionary> queryDictionary(String condition, String[] params) {
		Cursor result = this.db.rawQuery(condition, params);
		return OperationUtils.cursorToDictionary(result);
	}
	
	public List<DialogKeywords> queryDialogKeywords(String condition, String[] params) {
		Cursor result = this.db.rawQuery(condition, params);
		return OperationUtils.cursorToDialogKeywords(result);
	}
	
	public List<Dictionary> searchDictionary(String language_dir, String keyword) {
		db.execSQL("create index IF NOT EXISTS dictinaryIndex ON dictionary (language_dir, keyword)");
		Cursor result = db.rawQuery("select * from dictionary where language_dir = ? and keyword like ?", new String[]{language_dir,  keyword + "%"});
		return OperationUtils.cursorToDictionary(result);
	}
	
	public FuzzyResult fuzzySearch(String language_dir, String keyword) {
		FuzzyResult result = new FuzzyResult();
		List<Dictionary> exactResult = queryDictionary("select * from dictionary where keyword = ? ", new String[]{keyword});
		if(exactResult != null && exactResult.size() > 0) {
			result.setDictionaryList(exactResult);
			result.setExactResult(true);
		}else {
			Fuzzy fuzzy = new Fuzzy();
			result.setDictionaryList(fuzzy.fuzzySearch(keyword, language_dir, this));
			result.setExactResult(false);
		}
		
		return result;
	}
	
	public String getLastUpdateTime() {
		String dataTime = "";
		Cursor result = this.db.rawQuery("select Last_Update from Information where version = ?", new String[]{OperationUtils.getCurrentApplicationVersion(this.manager.getContext())});
		if(result.moveToNext()) {
			dataTime = result.getString(result.getColumnIndex("Last_Update"));
		}
		if(dataTime == null || dataTime.length() <= 0) {
			dataTime = "2011-04-01T01S01S01";
		}
		return dataTime;
	}

}
