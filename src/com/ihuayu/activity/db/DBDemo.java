/**
 * 
 */
package com.ihuayu.activity.db;

import java.util.List;

import android.content.Context;

import com.ihuayu.activity.db.entity.Dialog;
import com.ihuayu.activity.db.entity.DialogKeywords;
import com.ihuayu.activity.db.entity.Dictionary;
import com.ihuayu.activity.db.entity.Scenario;
import com.ihuayu.activity.operation.IhuayuOperationImpl;

/**
 * @author lixingwang
 *
 */
public class DBDemo {

	public DBDemo(Context context) {
		DBSqlite dbManager = new DBSqlite(context);
		IhuayuOperationImpl operation = new IhuayuOperationImpl(dbManager);
		long startTime = System.currentTimeMillis();
		//Search
		List<Dictionary> dicList = operation.searchDictionary("en2sc", "acc");
		long endtime = System.currentTimeMillis();
		System.out.println("Time====" + (double)(endtime - startTime)/1000);
		
		//Query from scenarios
		List<Scenario> scenarios = operation.queryScenario("select * from Scenario_Category where title_id = ?", new String[]{"1"});
		for(Scenario scenario : scenarios) {
			List<Dialog> dialogList = operation.queryDialog("select * from Dialog where title_id = ? ", new String[]{scenario.getTitle_id()});
			for(Dialog dialog : dialogList) {
				System.out.println("Dialog ==========" + dialog.getSentence());
				List<DialogKeywords> keywards = operation.queryDialogKeywords("select * from Dialog_Keyword where Dialog_ID = ? ", new String[]{dialog.getId()});
				for(DialogKeywords keyword : keywards) {
					System.out.println("Keyword ==========" + keyword.getDest_keyword());
				}
			}
		}
		
//		List<Dialog> dialogs = operation.queryDialog("select * from Dialog where Title_ID = ", new String[]{"1"});
		
		
//		DatabaseHelper helper = new DatabaseHelper(this, "database.sqlite");
//		SQLiteDatabase db = helper.getWritableDatabase();
//		
////		db.execSQL("select * from Dialog where Title_ID = 1");
//		Cursor result = db.query("Dialog", new String[]{"Title_ID", "Sentence"}, "Title_ID=?", new String[]{"1"}, null, null, null);
//		
//		while (result.moveToNext()) {  
//            String id = result.getString(result.getColumnIndex("Title_ID"));  result.getString(result.getColumnIndex("Dialog_ID"))
//            String name = result.getString(result.getColumnIndex("Sentence"));  
//            System.out.println("=======" + id + "||||||" + name);
//        }  
//		
//		RestService service = new RestService();
//		try {
//			operation.insertDictionary(service.getDictionary(operation.getLastUpdateTime()));
//			operation.insertIntoScenarioDialogKeyWord(service.getScenario(operation.getLastUpdateTime()));
//			
////			service.getScenario(operation.getLastUpdateTime());
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
