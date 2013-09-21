/**
 *
 */

package sg.gov.nhb.ihuayu.activity.operation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.db.DBSqlite;
import sg.gov.nhb.ihuayu.activity.db.entity.DialogKeywords;
import sg.gov.nhb.ihuayu.activity.db.entity.Dictionary;
import sg.gov.nhb.ihuayu.activity.db.entity.FuzzyResult;
import sg.gov.nhb.ihuayu.activity.db.entity.QueryType;
import sg.gov.nhb.ihuayu.activity.db.entity.Scenario;
import sg.gov.nhb.ihuayu.activity.db.entity.ScenarioDialog;
import sg.gov.nhb.ihuayu.activity.rest.AudioPlayer;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author lixingwang
 */
public class IhuayuOperationImpl {
    private static final String TAG = "iHuayu:IhuayuOperationImpl";
    private SQLiteDatabase db = null;
    private DBSqlite manager;

    public IhuayuOperationImpl(DBSqlite manager) {
        this.db = manager.getSqlDB();
        this.manager = manager;
    }

    public void close() {
        if (this.db != null) {
            try {
                this.db.close();
            } catch (Exception e) {
                Log.i(TAG, e.getLocalizedMessage());
            }
        }
    }

    public List<Scenario> queryScenario(String condition, String[] params) {
        Cursor cursor = null;
        List<Scenario> list = null;
        try {
            if (db != null) {
                cursor = this.db.rawQuery(condition, params);
                list = OperationUtils.cursorToScenario(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    public void insertDictionary(List<ContentValues> params) {
        // db.execSQL("CREATE TABLE name (id INTEGER, language_dir VARCHAR(10), keyword VARCHAR(1024),keyword_length INTEGER, src VARCHAR(1024), destination VARCHAR(1024), chinese_audio_link VARCHAR(1024), chinese_py_with_tone TEXT, dict_category CHAR(50), sample_sentence_EN VARCHAR(1024), sample_sentence_CN VARCHAR(1024), sample_sentence_PY VARCHAR(1024), sample_sentence_CN_Audio_link VARCHAR(1024))");
        if (db != null) {
            for (ContentValues values : params) {
                // long result = this.db.insert("dictionary", null, values);
                this.db.insert("dictionary", null, values);
            }
        }
    }

    public long insertDictionary(ContentValues param) {
        if (param != null && db != null) {
            String id = param.getAsString("id");
            if ("true".equalsIgnoreCase(param.getAsString("deleted"))) {
                deleteDictionary(param.getAsString("id"));
            } else {
                int updateCount = this.db.update("dictionary", param, "id=?", new String[]{
                        id
                });
                if (updateCount == 0) {
                    this.db.insert("dictionary", null, param);
                } else {
                    // Remove exist file.
                    AudioPlayer player = AudioPlayer.newInstance();
                    player.deleteExistAudioFile(param.getAsString("chinese_audio_link"));
                    player.deleteExistAudioFile(param.getAsString("sample_sentence_CN_Audio_link"));
                }
                return updateCount;
            }
        }
        return 0;
    }

    public void deleteDictionary(String id) {
        if (db != null) {
            this.db.delete("Favorites", "Dictionary_ID=?", new String[]{
                    id
            });
            this.db.delete("dictionary", "id=?", new String[]{
                    id
            });
        }
    }

    public void insertIntoScenarioDialogKeyWord(
            HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenarioDialogKeyword) {
        if (scenarioDialogKeyword != null && scenarioDialogKeyword.size() > 0) {
            Iterator<ContentValues> keyIter = scenarioDialogKeyword.keySet().iterator();
            while (keyIter.hasNext()) {
                ContentValues scenraioValues = keyIter.next();
                insertTable("Scenario_Category", scenraioValues);
                HashMap<ContentValues, List<ContentValues>> dialogKeywordsMap = scenarioDialogKeyword
                        .get(scenraioValues);

                if (dialogKeywordsMap != null && dialogKeywordsMap.size() > 0) {
                    Iterator<ContentValues> dialogKeyIter = dialogKeywordsMap.keySet().iterator();
                    while (dialogKeyIter.hasNext()) {
                        ContentValues dialogValues = dialogKeyIter.next();
                        insertTable("Dialog", dialogValues);

                        List<ContentValues> keyWordValues = dialogKeywordsMap.get(dialogValues);
                        if (keyWordValues != null) {
                            for (ContentValues keywordValues : keyWordValues) {
                                insertTable("Dialog_Keyword", keywordValues);
                            }
                        }
                    }
                }

            }
        }

    }

    public void deleteScenario(String id) {
        if (db != null) {
            // Get Dialog
            List<ScenarioDialog> dialogList = queryDialog(
                    "select * from Dialog where title_id = ? ", new String[]{
                    id
            });
            for (ScenarioDialog dialog : dialogList) {
                db.delete("Dialog_Keyword", "Dialog_ID=?", new String[]{
                        dialog.getId()
                });
            }
            db.delete("Dialog", "Title_ID=?", new String[]{
                    id
            });
            db.delete("Scenario_Category", "Title_ID=?", new String[]{
                    id
            });
        }
    }

    public void deleteDialog(String id) {
        if (db != null) {
            // Get Dialog
            db.delete("Dialog", "Dialog_ID=?", new String[]{
                    id
            });
        }
    }

    public void deleteDialogKeyword(String id) {
        if (db != null) {
            // Get Dialog
            db.delete("Dialog_Keyword", "ID=?", new String[]{
                    id
            });
        }
    }

    public void insertIntoScenarioDialogKeyWord(ContentValues contentValues,
                                                HashMap<ContentValues, List<ContentValues>> dialogKeywordsMap) {
        if (contentValues != null && contentValues.size() > 0) {
            // insertTable("Scenario_Category", contentValues);
            if ("true".equalsIgnoreCase(contentValues.getAsString("deleted"))) {
                deleteScenario(contentValues.getAsString("Title_ID"));
            } else {
                updateAndInsertScenario(contentValues, "Scenario_Category", "Title_ID");
            }
        }
        if (dialogKeywordsMap != null && dialogKeywordsMap.size() > 0) {
            if (dialogKeywordsMap != null && dialogKeywordsMap.size() > 0) {
                Iterator<ContentValues> dialogKeyIter = dialogKeywordsMap
                        .keySet().iterator();
                while (dialogKeyIter.hasNext()) {
                    ContentValues dialogValues = dialogKeyIter.next();
                    // insertTable("Dialog", dialogValues);
                    if ("true".equalsIgnoreCase(dialogValues.getAsString("deleted"))) {
                        deleteDialog(dialogValues.getAsString("Dialog_ID"));
                    } else {
                        updateAndInsertScenario(dialogValues, "Dialog", "Dialog_ID");
                    }
                    List<ContentValues> keyWordValues = dialogKeywordsMap
                            .get(dialogValues);
                    if (keyWordValues != null) {
                        for (ContentValues keywordValues : keyWordValues) {
                            // insertTable("Dialog_Keyword", keywordValues);
                            if ("true".equalsIgnoreCase(keywordValues.getAsString("deleted"))) {
                                deleteDialogKeyword(keywordValues.getAsString("ID"));
                            } else {
                                updateAndInsertScenario(keywordValues, "Dialog_Keyword", "ID");
                            }
                        }
                    }
                }

            }
        }
    }

    public int updateAndInsertScenario(ContentValues contentValues, String tableName,
                                       String primeryKeyId) {
        if (contentValues != null && db != null) {
            String id = contentValues.getAsString(primeryKeyId);
            int updateCount = this.db.update(tableName, contentValues, primeryKeyId + "=?",
                    new String[]{
                            id
                    });
            if (updateCount == 0) {
                this.db.insert(tableName, null, contentValues);
            } else {
                // Remove exist file.
                String audioFile = contentValues.getAsString("Sentence_Audio_Link");
                AudioPlayer.newInstance().deleteExistAudioFile(audioFile);
            }
            return updateCount;
        }
        return 0;
    }

    public long insertToBookmark(String dictionaryID) {
        if (db != null) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("Dictionary_ID", dictionaryID);
                contentValues.put("Update_date", getStandardTimeStr());
                return this.db.insert("Favorites", null, contentValues);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public List<Dictionary> getAllBookmarks() {
        List<Dictionary> list = null;
        Cursor cursor = null;
        if (db != null) {
            try {
                // TODO. maybe have performance issue.
                cursor = this.db
                        .rawQuery(
                                "select rowid, * from dictionary where id in (select Dictionary_ID from Favorites)",
                                null);
                list = OperationUtils.cursorToDictionary(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return list;
    }

    public int getBookmark(int dictionaryId) {
        try {
            Log.d(TAG, "[getBookmark] + Begin");
            // TODO. maybe have performance issue.
            Cursor result = this.db.rawQuery("select * from Favorites where Dictionary_ID = ?",
                    new String[]{
                            dictionaryId + ""
                    });
            // return result.getInt(result.getColumnIndex("Dictionary_ID"));
            Log.d(TAG, "[getBookmark] Cursor Count = " + result.getCount());
            return result.getCount();
        } catch (Exception e) {
            return -1;
        }
    }

    public int removeFromFavorites(int dictionaryId) {
        int result = 0;
        if (db != null) {
            result = this.db.delete("Favorites", "Dictionary_ID = ?", new String[]{
                    dictionaryId + ""
            });
        }
        return result;
    }

    private String getStandardTimeStr() throws ParseException {
        DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        return format2.format(now);
    }

    private String getUpdatesTimeStr() throws ParseException {
        // 2011-04-01T01S01S01
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH'S'mm'S'ss");
        Date now = new Date();
        return format2.format(now);
    }

    public long insertTable(String name, ContentValues contentValues) {
        long result = 0;
        if (db != null) {
            result = this.db.insert(name, null, contentValues);
        }
        return result;
    }

    public List<ScenarioDialog> queryDialog(String condition, String[] params) {
        Cursor cursor = null;
        List<ScenarioDialog> list = null;
        if (db != null) {
            try {
                cursor = this.db.rawQuery(condition, params);
                list = OperationUtils.cursorToDialog(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return list;
    }

    public List<Dictionary> queryDictionary(String condition, String[] params) {
        Cursor cursor = null;
        List<Dictionary> list = null;
        if (db != null) {
            try {
                cursor = this.db.rawQuery(condition, params);
                list = OperationUtils.cursorToDictionary(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return list;
    }

    public List<DialogKeywords> queryDialogKeywords(String condition, String[] params) {
        Cursor cursor = null;
        List<DialogKeywords> list = null;
        if (db != null) {
            try {
                cursor = this.db.rawQuery(condition, params);
                list = OperationUtils.cursorToDialogKeywords(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return list;
    }

    public List<Dictionary> searchDictionary(String language_dir, String keyword) {
        List<Dictionary> list = null;
        if (db != null) {
            db.execSQL("create index IF NOT EXISTS dictinaryIndex ON dictionary (language_dir, keyword)");
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(
                        "select rowid, * from dictionary where language_dir = ? and keyword like ? order by rowid limit 20",
                        new String[]{
                                language_dir, keyword + "%"
                        });
                list = OperationUtils.cursorToDictionary(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }


        }
        // Collections.sort(list);
        return list;
    }

    public FuzzyResult fuzzySearch(String language_dir, String keyword) {
        FuzzyResult result = new FuzzyResult();
        List<Dictionary> exactResult = queryDictionary(
                "select rowid, * from dictionary where keyword like ? ", new String[]{
                keyword
        });
        if (exactResult != null && exactResult.size() > 0) {
            result.setDictionaryList(exactResult);
            result.setExactResult(true);
        } else {
            Fuzzy fuzzy = new Fuzzy();
            result.setDictionaryList(fuzzy.fuzzySearch(keyword, language_dir, this));
            result.setExactResult(false);
        }

        // Select from Scenario
        List<Scenario> scenarioList = null;
        if (QueryType.EN.getName().equals(language_dir)) {
            scenarioList = queryScenario(
                    "SELECT * FROM Scenario_Category WHERE Title_EN LIKE ? ", new String[]{
                    "%" + keyword + "%"
            });
        } else {
            scenarioList = queryScenario(
                    "SELECT * FROM Scenario_Category WHERE Title_CN LIKE ? ", new String[]{
                    "%" + keyword + "%"
            });
        }
        result.setScenarioList(scenarioList);
        return result;
    }

    public String getLastUpdateTime() {
        String dataTime = null;
        if (db != null) {
            Cursor result = this.db.rawQuery("select Last_Update from Information where version = ?",
                    new String[]{
                            OperationUtils.getCurrentApplicationVersion(this.manager.getContext())
                    });
            if (result != null) {
                if (result.moveToNext()) {
                    dataTime = result.getString(result.getColumnIndex("Last_Update"));
                }
            } else {
                Log.w(TAG, "Cannot query last update time");
            }
        }

        if (TextUtils.isEmpty(dataTime)) {
            dataTime = "2011-04-01T01S01S01";
        }
        return dataTime;
    }

    public long updateUpdateTime() {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Version",
                    OperationUtils.getCurrentApplicationVersion(this.manager.getContext()));
            contentValues.put("Last_Update", getUpdatesTimeStr());
            if (db != null) {
                return this.db.insert("Information", null, contentValues);
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
