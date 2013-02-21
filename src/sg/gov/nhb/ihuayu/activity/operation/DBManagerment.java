/**
 * 
 */

package sg.gov.nhb.ihuayu.activity.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.db.DBSqlite;
import sg.gov.nhb.ihuayu.activity.db.entity.ScenarioDialog;
import sg.gov.nhb.ihuayu.activity.db.entity.DialogKeywords;
import sg.gov.nhb.ihuayu.activity.db.entity.Dictionary;
import sg.gov.nhb.ihuayu.activity.db.entity.FuzzyResult;
import sg.gov.nhb.ihuayu.activity.db.entity.QueryType;
import sg.gov.nhb.ihuayu.activity.db.entity.Scenario;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * @author lixingwang
 */
public class DBManagerment {
    private static final String TAG = "iHuayu:DBManagerment";
    private IhuayuOperationImpl operation = null;

    public DBManagerment(Context context) {
        // TODO maybe single instance should be used.
        DBSqlite dbManager = new DBSqlite(context);
        operation = new IhuayuOperationImpl(dbManager);
    }

    public void close() {
        operation.close();
    }

    public List<Dictionary> searchDictionary(QueryType type, String keyword) {
        return operation.searchDictionary(type.getName(), keyword);
    }

    public FuzzyResult fuzzySearchDictionary(QueryType type, String keyword) {
        return operation.fuzzySearch(type.getName(), keyword);
    }

    public List<Scenario> getAllScenarios() {
        return operation.queryScenario("select * from Scenario_Category", null);
    }

    public List<HashMap<ScenarioDialog, List<DialogKeywords>>> getDialogList(String titleId) {
        List<HashMap<ScenarioDialog, List<DialogKeywords>>> dialogKeywordList = new ArrayList<HashMap<ScenarioDialog, List<DialogKeywords>>>();
        List<ScenarioDialog> dialogList = operation.queryDialog(
                "select * from Dialog where title_id = ? ", new String[] {
                    titleId
                });
        for (ScenarioDialog dialog : dialogList) {
            HashMap<ScenarioDialog, List<DialogKeywords>> dialogKeyword = new HashMap<ScenarioDialog, List<DialogKeywords>>();
            List<DialogKeywords> keywards = operation.queryDialogKeywords(
                    "select * from Dialog_Keyword where Dialog_ID = ? ", new String[] {
                        dialog.getId()
                    });
            dialogKeyword.put(dialog, keywards);
            dialogKeywordList.add(dialogKeyword);
        }
        return dialogKeywordList;
    }

    public long addBookmark(int dictionaryID) {
        return operation.insertToBookmark(dictionaryID + "");
    }

    public List<Dictionary> getAllBookMarks() {
        return operation.getAllBookmarks();
    }

    public int removeFromFavorites(int dictionaryId) {
        return operation.removeFromFavorites(dictionaryId);
    }

    public void removeFromFavorites(int[] dictionaryId) {
        for (int i = 0; i < dictionaryId.length; i++) {
            operation.removeFromFavorites(dictionaryId[i]);
        }
    }

    public boolean hasbookmarked(int dictionaryID)
    {
        Log.d(TAG, "[hasbookmarked] dictionaryID = " + dictionaryID);
        // if(operation.getBookmark(dictionaryID) != -1) return true;
        if (operation.getBookmark(dictionaryID) > 0) {
            Log.d(TAG, "[hasbookmarked] has been bookmarked,return true");
            return true;
        }
        else {
            Log.d(TAG, "[hasbookmarked] hasn't been bookmarked,return false");
            return false;
        }
    }

    public Dictionary getDictinary(int dicId) {
        // Should return only one record.
        List<Dictionary> dictionaryList = operation.queryDictionary(
                "select rowid, * from dictionary where id = ? ", new String[] {
                    dicId + ""
                });
        if (dictionaryList == null || dictionaryList.size() == 0)
            return null;
        return dictionaryList.get(0);
    }

    public List<Dictionary> getNextDictionary(int currentDictionaryId) {
        List<Dictionary> dictionaryList = operation.queryDictionary(
                "select rowid, * from dictionary where rowid in (?,?)", new String[] {
                        (currentDictionaryId + 1) + "", (currentDictionaryId + 2) + ""
                });
        if (dictionaryList != null) {
            int size = dictionaryList.size();
            Log.d(TAG, "[getNextDictionary] size = " + size);
            return dictionaryList;
//            if (size == 2) {
//                Log.d(TAG, "[getNextDictionary] get(0).getRowid = "
//                        + dictionaryList.get(0).getRowid());
//                Log.d(TAG, "[getNextDictionary] get(1).getRowid = "
//                        + dictionaryList.get(1).getRowid());
//                return dictionaryList.get(0);
//            } else if (size == 1) {
//                Log.d(TAG, "[getNextDictionary] get(0).getRowid = "
//                        + dictionaryList.get(0).getRowid());
//                return dictionaryList.get(0);
//            } else {
//                Log.e(TAG, "[getNextDictionary] error while getNextDictionary");
//                return null;
//            }
        } else {
            Log.e(TAG, "[getNextDictionary] error while getNextDictionary");
            return null;
        }
    }

    public List<Dictionary> getPreviousDictionary(int currentDictionaryId) {
        List<Dictionary> dictionaryList = operation.queryDictionary(
                // "select rowid, * from dictionary where rowid = ? ", new
                // String[] {
                // (currentDictionaryId - 1) + ""});
                "select rowid, * from dictionary where rowid in (?,?)", new String[] {
                        (currentDictionaryId - 1) + "", (currentDictionaryId - 2) + ""
                });
        if (dictionaryList != null) {
            int size = dictionaryList.size();
            Log.d(TAG, "[getPreviousDictionary] size = " + size);
            return dictionaryList;
//            if (size == 2) {
//                Log.d(TAG, "[getPreviousDictionary] get(0).getRowid = "
//                        + dictionaryList.get(0).getRowid());
//                Log.d(TAG, "[getPreviousDictionary] get(1).getRowid = "
//                        + dictionaryList.get(1).getRowid());
//                return dictionaryList.get(1);
//            } else if (size == 1) {
//                Log.d(TAG, "[getPreviousDictionary] get(0).getRowid = "
//                        + dictionaryList.get(0).getRowid());
//                return dictionaryList.get(0);
//            } else {
//                Log.e(TAG, "[getPreviousDictionary] error while getPreviousDictionary");
//                return null;
//            }
        } else {
            Log.e(TAG, "[getPreviousDictionary] error while getPreviousDictionary");
            return null;
        }
    }

    // For update
    public void insertDictionary(List<ContentValues> params) {
        operation.insertDictionary(params);
    }

    // For update
    public void insertDictionary(ContentValues params) {
        operation.insertDictionary(params);
    }

    public int deleteDictionary(String id) {
    	return operation.deleteDictionary(id);
    }
    
    // For update
    public void insertScenario(
            HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenarioDialogKeyword) {
        operation.insertIntoScenarioDialogKeyWord(scenarioDialogKeyword);
    }

    public void insertScenario(ContentValues contentValues,
            HashMap<ContentValues, List<ContentValues>> scenarioDialogKeyword) {
        operation.insertIntoScenarioDialogKeyWord(contentValues, scenarioDialogKeyword);
    }

    public String getLastUpdateTime() {
        return operation.getLastUpdateTime();
    }

    public long updateUpdateTime() {
        return operation.updateUpdateTime();
    }

    public String getLastCancelUpdateTime() {
        return operation.getCancelTime();
    }

    public long updateCancelUpdateTime() {
        return operation.updateCancelTime();
    }
}
