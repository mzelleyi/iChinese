/**
 * 
 */
package com.ihuayu.activity.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;

import com.ihuayu.activity.db.entity.Dialog;
import com.ihuayu.activity.db.entity.DialogKeywords;
import com.ihuayu.activity.db.entity.Dictionary;
import com.ihuayu.activity.db.entity.Scenario;

/**
 * @author lixingwang
 *
 */
public class OperationUtils {
	
	
	public static List<Scenario> cursorToScenario(Cursor cursor) {
		List<Scenario> list = new ArrayList<Scenario>();
		while (cursor.moveToNext()) {  
           list.add(cursorToOneScenario(cursor));
        }
		return list;
	}
	
	public static List<Dialog> cursorToDialog(Cursor cursor) {
		List<Dialog> list = new ArrayList<Dialog>();
		while (cursor.moveToNext()) {  
           list.add(cursorToOneDialog(cursor));
        }
		return list;
	}
	
	public static List<Dictionary> cursorToDictionary(Cursor cursor) {
		List<Dictionary> list = new ArrayList<Dictionary>();
		while (cursor.moveToNext()) {  
           list.add(cursorToOneDictionary(cursor));
        }
		return list;
	}
	
	public static List<DialogKeywords> cursorToDialogKeywords(Cursor cursor) {
		List<DialogKeywords> list = new ArrayList<DialogKeywords>();
		while (cursor.moveToNext()) {  
           list.add(cursorToOneDialogKeywords(cursor));
        }
		return list;
	}
	
	private static Dictionary cursorToOneDictionary(Cursor cursor) {
		Dictionary dictionary = new Dictionary();
		for(String name : cursor.getColumnNames()) {
			if("id".equalsIgnoreCase(name)) {
				dictionary.setId(cursor.getString(cursor.getColumnIndex(name)));
			}else if("language_dir".equalsIgnoreCase(name)) {
				dictionary.setLanguage_dir(cursor.getString(cursor.getColumnIndex(name)));
			}else if("keyword".equalsIgnoreCase(name)) {
				dictionary.setKeyword(cursor.getString(cursor.getColumnIndex(name)));
			}else if("keyword_length".equalsIgnoreCase(name)) {
				dictionary.setKeyword_len(cursor.getString(cursor.getColumnIndex(name)));
			}else if("src".equalsIgnoreCase(name)) {
				dictionary.setSrc(cursor.getString(cursor.getColumnIndex(name)));
			}else if("destination".equalsIgnoreCase(name)) {
				dictionary.setDestiontion(cursor.getString(cursor.getColumnIndex(name)));
			}else if("chinese_audio_link".equalsIgnoreCase(name)) {
				dictionary.setChinese_audio(cursor.getString(cursor.getColumnIndex(name)));
			}else if("chinese_py_with_tone".equalsIgnoreCase(name)) {
				dictionary.setChineser_tone_py(cursor.getString(cursor.getColumnIndex(name)));
			}else if("dict_category".equalsIgnoreCase(name)) {
				dictionary.setDic_catagory(cursor.getString(cursor.getColumnIndex(name)));
			}else if("sample_sentence_EN".equalsIgnoreCase(name)) {
				dictionary.setSample_sentance_en(cursor.getString(cursor.getColumnIndex(name)));
			}else if("sample_sentence_CN".equalsIgnoreCase(name)) {
				dictionary.setSample_sentence_ch(cursor.getString(cursor.getColumnIndex(name)));
			}else if("sample_sentence_PY".equalsIgnoreCase(name)) {
				dictionary.setSample_sentance_py(cursor.getString(cursor.getColumnIndex(name)));
			}else if("sample_sentence_CN_Audio_link".equalsIgnoreCase(name)) {
				dictionary.setSample_sentance_audio(cursor.getString(cursor.getColumnIndex(name)));
			}
		}
		return dictionary;
	}
	
	private static DialogKeywords cursorToOneDialogKeywords(Cursor cursor) {
		DialogKeywords dialog = new DialogKeywords();
		for(String name : cursor.getColumnNames()) {
			if("ID".equalsIgnoreCase(name)) {
				dialog.setId(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Dialog_ID".equalsIgnoreCase(name)) {
				dialog.setDialog_id(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Src_Keyword".equalsIgnoreCase(name)) {
				dialog.setSrc_keyword(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Dest_Keyword".equalsIgnoreCase(name)) {
				dialog.setDest_keyword(cursor.getString(cursor.getColumnIndex(name)));
			}
		}
		return dialog;
	}
	
	private static Dialog cursorToOneDialog(Cursor cursor) {
		Dialog dialog = new Dialog();
		for(String name : cursor.getColumnNames()) {
			if("Dialog_ID".equalsIgnoreCase(name)) {
				dialog.setId(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Title_ID".equalsIgnoreCase(name)) {
				dialog.setTitle_id(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Sentence_Sequence_ID".equalsIgnoreCase(name)) {
				dialog.setSentence_sequence_id(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Gender".equalsIgnoreCase(name)) {
				dialog.setGender(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Narrator".equalsIgnoreCase(name)) {
				dialog.setNarrator(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Sentence".equalsIgnoreCase(name)) {
				dialog.setSentence(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Sentence_EN".equalsIgnoreCase(name)) {
				dialog.setSentence_en(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Sentence_PY".equalsIgnoreCase(name)) {
				dialog.setSentence_py(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Sentence_Audio_Link".equalsIgnoreCase(name)) {
				dialog.setAudio(cursor.getString(cursor.getColumnIndex(name)));
			}
		}
		return dialog;
	}
	
	private static Scenario cursorToOneScenario(Cursor cursor) {
		Scenario scenario = new Scenario();
		for(String name : cursor.getColumnNames()) {
			if("Title_ID".equalsIgnoreCase(name)) {
				scenario.setTitle_id(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Title_EN".equalsIgnoreCase(name)) {
				scenario.setTitle_en(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Title_CN".equalsIgnoreCase(name)) {
				scenario.setTitle_cn(cursor.getString(cursor.getColumnIndex(name)));
			}else if("Title_PY".equalsIgnoreCase(name)) {
				scenario.setTitle_py(cursor.getString(cursor.getColumnIndex(name)));
			}
		}
		return scenario;
	}

	public static String getCurrentApplicationVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (NameNotFoundException e) {

		}
		return "1.1";
	}
	
	@SuppressWarnings("unchecked")
	public static List<ContentValues> jsonTileToSqliteContentValuesDictionary(JSONArray resultArray) throws JSONException {
		List<ContentValues> valuesList = new ArrayList<ContentValues>();
		for(int i=0; i < resultArray.length(); i++)  {
			JSONObject object = resultArray.getJSONObject(i);
			ContentValues value = new ContentValues();
			Iterator<String>  iter = object.keys();
			while(iter.hasNext()) {
				String key = iter.next();
				if("id".equalsIgnoreCase(key)) {
					value.put("id", object.getString(key));
				}else if("lang_dir".equalsIgnoreCase(key)) {
					value.put("language_dir", object.getString(key));
				}else if("keyword".equalsIgnoreCase(key)) {
					value.put("keyword", object.getString(key));
				}else if("keyword_len".equalsIgnoreCase(key)) {
					value.put("keyword_length", object.getString(key));
				}else if("src".equalsIgnoreCase(key)) {
					value.put("src", object.getString(key));
				}else if("dest".equalsIgnoreCase(key)) {
					value.put("destination", object.getString(key));
				}else if("audio".equalsIgnoreCase(key)) {
					value.put("chinese_audio_link", object.getString(key));
				}else if("py".equalsIgnoreCase(key)) {
					value.put("chinese_py_with_tone", object.getString(key));
				}else if("category".equalsIgnoreCase(key)) {
					value.put("dict_category", object.getString(key));
				}else if("sample_en".equalsIgnoreCase(key)) {
					value.put("sample_sentence_EN", object.getString(key));
				}else if("sample_cn".equalsIgnoreCase(key)) {
					value.put("sample_sentence_CN", object.getString(key));
				}else if("sample_py".equalsIgnoreCase(key)) {
					value.put("sample_sentence_PY", object.getString(key));
				}else if("sample_audio".equalsIgnoreCase(key)) {
					value.put("sample_sentence_CN_Audio_link", object.getString(key));
				}
			}
			valuesList.add(value);
		}
		return valuesList;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> getScenarioFromJsonObject(JSONArray resultArray) throws JSONException {
		HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>>  dialogKeywordsMap = new HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>>();
		HashMap<ContentValues, List<ContentValues>>  dialogKeywords = null; 
		for(int i=0; i < resultArray.length(); i++)  {
			JSONObject object = resultArray.getJSONObject(i);
			ContentValues value = new ContentValues();
			Iterator<String> iter = object.keys();
			while(iter.hasNext()) {
				String name = iter.next();;
			if("scenario_id".equalsIgnoreCase(name)) {
				value.put("Title_ID", object.getString(name));
			}else if("title_en".equalsIgnoreCase(name)) {
				value.put("Title_EN", object.getString(name));
			}else if("title_cn".equalsIgnoreCase(name)) {
				value.put("Title_CN", object.getString(name));
			}else if("title_py".equalsIgnoreCase(name)) {
				value.put("Title_PY", object.getString(name));
			}else if("dialog".equalsIgnoreCase(name)){
				dialogKeywords = getDialogFromJsonObject(object.getJSONArray("dialog"));
			}
		}
			dialogKeywordsMap.put(value, dialogKeywords);
		}
		return dialogKeywordsMap;
	}
	
	@SuppressWarnings("unchecked")
	private static HashMap<ContentValues, List<ContentValues>> getDialogFromJsonObject(JSONArray resultArray) throws JSONException {
		
//		List<ContentValues> valuesList = new ArrayList<ContentValues>();
		HashMap<ContentValues, List<ContentValues>>  dialogKeywordsMap = new HashMap<ContentValues, List<ContentValues>>();
		
		for(int i=0; i < resultArray.length(); i++)  {
			JSONObject object = resultArray.getJSONObject(i);
			ContentValues dialogValue = new ContentValues();
			List<ContentValues> keywordValues = null;
			Iterator<String> iter = object.keys();
			while(iter.hasNext()) {
				String name = iter.next();
				if("dialogue_id".equalsIgnoreCase(name)) {
					dialogValue.put("Dialog_ID", object.getString(name));
				}else if("Title_ID".equalsIgnoreCase(name)) {
					dialogValue.put("Title_ID", object.getString(name));
				}else if("seq".equalsIgnoreCase(name)) {
					dialogValue.put("Sentence_Sequence_ID", object.getString(name));
				}else if("gender".equalsIgnoreCase(name)) {
					dialogValue.put("Gender", object.getString(name));
				}else if("narrator".equalsIgnoreCase(name)) {
					dialogValue.put("Narrator", object.getString(name));
				}else if("sent_cn".equalsIgnoreCase(name)) {
					dialogValue.put("Sentence", object.getString(name));
				}else if("sent_en".equalsIgnoreCase(name)) {
					dialogValue.put("Sentence_EN", object.getString(name));
				}else if("sent_py".equalsIgnoreCase(name)) {
					dialogValue.put("Sentence_PY", object.getString(name));
				}else if("sent_audio".equalsIgnoreCase(name)) {
					dialogValue.put("Sentence_Audio_Link", object.getString(name));
				}else if("keyword".equalsIgnoreCase(name)) {
					///
					keywordValues = getDialogKeyWorkdFromJsonObject(object.getJSONArray("keyword"));
				}
			}
			dialogKeywordsMap.put(dialogValue, keywordValues);
		}
		return dialogKeywordsMap;
	}
	
	@SuppressWarnings("unchecked")
	private static List<ContentValues> getDialogKeyWorkdFromJsonObject(JSONArray resultArray) throws JSONException {
		List<ContentValues> valuesList = new ArrayList<ContentValues>();
		for (int i = 0; i < resultArray.length(); i++) {
			JSONObject object = resultArray.getJSONObject(i);
			ContentValues value = new ContentValues();
			Iterator<String> iter = object.keys();
			while (iter.hasNext()) {
				String name = iter.next();
				if ("keyword_id".equalsIgnoreCase(name)) {
					value.put("ID", object.getString(name));
				} else if ("Dialog_ID".equalsIgnoreCase(name)) {
					value.put("Dialog_ID", object.getString(name));
				} else if ("keyword_en".equalsIgnoreCase(name)) {
					value.put("Src_Keyword", object.getString(name));
				} else if ("keyword_cn".equalsIgnoreCase(name)) {
					value.put("Dest_Keyword", object.getString(name));
				} else if ("keyword_py".equalsIgnoreCase(name)) {
					value.put("Keyword_PY", object.getString(name));
				}
			}
		}
		return valuesList;
	}
	
}
