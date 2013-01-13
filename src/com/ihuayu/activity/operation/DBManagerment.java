/**
 * 
 */
package com.ihuayu.activity.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.ihuayu.activity.db.DBSqlite;
import com.ihuayu.activity.db.entity.Dialog;
import com.ihuayu.activity.db.entity.DialogKeywords;
import com.ihuayu.activity.db.entity.Dictionary;
import com.ihuayu.activity.db.entity.QueryType;
import com.ihuayu.activity.db.entity.Scenario;

/**
 * @author lixingwang
 *
 */
public class DBManagerment {
	
	private IhuayuOperationImpl operation = null;
	
	public DBManagerment(Context context) {
		//TODO maybe single instance should be used.
		DBSqlite dbManager = new DBSqlite(context);
		operation = new IhuayuOperationImpl(dbManager);
	}
	
	public List<Dictionary> searchDictionary(QueryType type, String keyword) {
		return operation.searchDictionary(type.getName(), keyword);
	}
	
	public List<Scenario> getAllScenarios() {
		return operation.queryScenario("select * from Scenario_Category", null);
	}
	
	public List<HashMap<Dialog, List<DialogKeywords>>> getDialogList(String titleId) {
		List<HashMap<Dialog, List<DialogKeywords>>> dialogKeywordList = new ArrayList<HashMap<Dialog,List<DialogKeywords>>>();
		List<Dialog> dialogList = operation.queryDialog("select * from Dialog where title_id = ? ", new String[]{titleId});
		for(Dialog dialog : dialogList) {
			HashMap<Dialog, List<DialogKeywords>> dialogKeyword = new HashMap<Dialog, List<DialogKeywords>>();
			List<DialogKeywords> keywards = operation.queryDialogKeywords("select * from Dialog_Keyword where Dialog_ID = ? ", new String[]{dialog.getId()});
			dialogKeyword.put(dialog, keywards);
			dialogKeywordList.add(dialogKeyword);
		}
		
		return dialogKeywordList;
	}
	
	public void addBookmark(String dictionaryID) {
		operation.insertToBookmark(dictionaryID);
	}

}
