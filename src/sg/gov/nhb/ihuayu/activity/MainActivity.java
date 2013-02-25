
package sg.gov.nhb.ihuayu.activity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import sg.gov.nhb.ihuayu.R;
import sg.gov.nhb.ihuayu.activity.operation.DBManagerment;
import sg.gov.nhb.ihuayu.activity.rest.FileUtils;
import sg.gov.nhb.ihuayu.activity.rest.RestService;
import sg.gov.nhb.ihuayu.view.MyDialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * @author Kesen
 */
public class MainActivity extends FragmentActivity implements
        TabHost.OnTabChangeListener {

    // Message code for do action
    private static final int COPY_DB_TO_PHONE = 101;
    private static final int CHECK_UPDATE_COUNT = 102;
    private static final int UPDATE_DATA_TO_DB = 103;
    private static final int UPDATE_TIME_STAMP = 104;
    private static final int UPDATE_CANCEL_TIME = 105;

    // Message code for dialog
    private static final int SHOW_CHECKING_DIALOG = 106;
    private static final int HIDE_CHECKING_DIALOG = 107;
    private static final int SHOW_NUMBER_OF_UPDATES = 108;
    private static final int HIDE_NUMBER_OF_UPDATES = 109;
    private static final int UPDATE_DOWNLOAD_PROCESS = 110;
    private static final int SHOW_DOWNLOAD_PROCESS = 111;
    private static final int HIDE_DOWNLOAD_PROCESS = 112;

    private static final String TAG = "iHuayu:MainActivity";
    // Flag for HandlerThread
    private static final String THREAD_NAME = "MainActivity";
    // Flag for tab name
    private static final String TAB_SEARCH = "Search";
    private static final String TAB_SCENARIO = "Scenario";
    private static final String TAB_BOOKMARK = "Bookmark";
    private static final String TAB_INFO = "Info";

    // Flag for fragment
    public static final String fragment_tag_search = "tag_Search";
    public static final String fragment_tag_scenario = "tag_Scenario";
    public static final String fragment_tag_bookmark = "tag_Bookmark";
    public static final String fragment_tag_info = "tag_Info";
    public static final String fragment_tag_help = "tag_Help";
    public static final String fragment_tag_bookmark_detail = "tag_bookmark_detail";
    public static final String fragment_tag_search_detail = "tag_search_detail";
    public static final String fragment_tag_scenario_detail = "tag_scenario_detail";

    public static DBManagerment dbManagerment = null;
    public static Resources mRes = null;
    public static int mMax_Progress = 0;

    // The DB Operation Thread
    private static NonUiHandler mNonUiHandler = null;
    // The update/copyDB Handler Thread
    private HandlerThread mHandlerThread = null;

    private TabHost mTabHost = null;
    private LayoutInflater mInflater = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[onCreate] + Begin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mContext = this.getApplicationContext();
        // mActivity = (FragmentActivity) this.getParent();
        mRes = this.getResources();
        mInflater = this.getLayoutInflater();

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        setupTab(TAB_SEARCH);
        setupTab(TAB_SCENARIO);
        setupTab(TAB_BOOKMARK);
        setupTab(TAB_INFO);

        // mTabHost.getTabWidget().setDividerDrawable(null);
        // mTabHost.addTab(mTabHost
        // .newTabSpec(TAB_SEARCH)
        // .setIndicator(mRes.getString(R.string.tab_bar_search),
        // mRes.getDrawable(R.drawable.tab_search_selector))
        // .setContent(R.id.tab_content_search));
        // mTabHost.addTab(mTabHost
        // .newTabSpec(TAB_SCENARIO)
        // .setIndicator(mRes.getString(R.string.tab_bar_scenario),
        // mRes.getDrawable(R.drawable.tab_scenarios_selector))
        // .setContent(R.id.tab_content_scenario));
        // mTabHost.addTab(mTabHost
        // .newTabSpec(TAB_BOOKMARK)
        // .setIndicator(mRes.getString(R.string.tab_bar_bookmark),
        // mRes.getDrawable(R.drawable.tab_bookmark_selector))
        // .setContent(R.id.tab_content_bookmark));
        // mTabHost.addTab(mTabHost
        // .newTabSpec(TAB_INFO)
        // .setIndicator(mRes.getString(R.string.tab_bar_info),
        // mRes.getDrawable(R.drawable.tab_help_selector))
        // .setContent(R.id.tab_content_info));
        //
        // for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++)
        // {
        // View view = mTabHost.getTabWidget().getChildAt(i);
        // view.setBackgroundResource(R.drawable.tab_bg_selector);
        //
        // TextView title = (TextView) view.findViewById(android.R.id.title);
        // title.setTextSize(mRes.getDimension(R.dimen.tab_bar_textview_size));
        // title.setTextColor(mRes.getColorStateList(R.color.tab_text_color));
        // title.invalidate();
        // }

        mTabHost.setCurrentTabByTag(TAB_SEARCH);

        this.updateTab(TAB_SEARCH, R.id.tab_content_search);

        mHandlerThread = new HandlerThread(THREAD_NAME);
        mHandlerThread.setPriority(Thread.NORM_PRIORITY);
        mHandlerThread.start();
        mNonUiHandler = new NonUiHandler(mHandlerThread.getLooper());

        if (!FileUtils.hasDBFileInPhone()) {
            sendNonUIHandlerMsg(COPY_DB_TO_PHONE, 500);
        } else {
            dbManagerment = new DBManagerment(MainActivity.this);
//            try
//            {
//                if (canShowUpdate()) {
//                    sendNonUIHandlerMsg(CHECK_UPDATE_COUNT, 500);
//                }
//            } catch (ParseException e)
//            {
//                Log.i(TAG, e.getMessage());
//            }
        }

        Log.d(TAG, "[onCreate] + End");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "[onStart] + Begin");
        // TODO Auto-generated method stub
        super.onStart();

        Log.d(TAG, "[onStart] + End");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "[onRestart] + Begin");
        // TODO Auto-generated method stub
        super.onRestart();

        Log.d(TAG, "[onRestart] + End");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "[onResume] + Begin");
        // TODO Auto-generated method stub
        super.onResume();
        mTabHost.setOnTabChangedListener(this);
        try
	      {
	          if (canShowUpdate()) {
	              sendNonUIHandlerMsg(CHECK_UPDATE_COUNT, 500);
	          }
	      } catch (ParseException e)
	      {
	          Log.i(TAG, e.getMessage());
	      }
        Log.d(TAG, "[onResume] + End");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "[onPause] + Begin");
        // TODO Auto-generated method stub
        super.onPause();

        Log.d(TAG, "[onPause] + End");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "[onStop] + Begin");
        // TODO Auto-generated method stub
        super.onStop();

        Log.d(TAG, "[onStop] + End");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "[onDestory] + Begin");
        // TODO Auto-generated method stub
        super.onDestroy();
        mTabHost = null;

        Log.d(TAG, "[onDestory] + End");
    }

    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "[onBackPressed] + Begin");
        // FragmentManager fm = this.getSupportFragmentManager();
        // int stackCount = fm.getBackStackEntryCount();
        // Log.d(TAG, "[onBackPressed] stackCount = "+stackCount);
        // TODO Auto-generated method stub
        // super.onBackPressed();
        dbManagerment.close();
        this.finish();
    }

    @Override
    public void onTabChanged(String tabTag) {
        Log.d(TAG, "[onTabChanged] + Begin,tabTag:" + tabTag);
        // TODO Auto-generated method stub

        // // Pop to home page
        // FragmentManager fm = getSupportFragmentManager();
        // if (fm.getBackStackEntryCount() > 0) {
        // Log.d(TAG, "[onTabChanged] popBackStack ");
        // fm.popBackStack(fm.getBackStackEntryAt(0).getId(),
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // }

        if (TAB_SEARCH.equals(tabTag)) {
            updateTab(tabTag, R.id.tab_content_search);
            return;
        } else if (TAB_SCENARIO.equals(tabTag)) {
            updateTab(tabTag, R.id.tab_content_scenario);
            return;
        } else if (TAB_BOOKMARK.equals(tabTag)) {
            updateTab(tabTag, R.id.tab_content_bookmark);
            return;
        } else if (TAB_INFO.equals(tabTag)) {
            updateTab(tabTag, R.id.tab_content_info);
            return;
        } else {
            Log.e(TAG, "Error Tab Type");
        }
        Log.d(TAG, "[onTabChanged] + End");
    }

    private void setupTab(final String tag) {
        View tabview = null;
        TabSpec tabSpec = null;

        if (TAB_SEARCH.equals(tag)) {
            tabview = createTabView(mRes.getString(R.string.tab_bar_search),
                    mRes.getDrawable(R.drawable.tab_search_selector));
            tabSpec = mTabHost.newTabSpec(tag).setIndicator(tabview)
                    .setContent(R.id.tab_content_search);
        } else if (TAB_SCENARIO.equals(tag)) {
            tabview = createTabView(mRes.getString(R.string.tab_bar_scenario),
                    mRes.getDrawable(R.drawable.tab_scenarios_selector));
            tabSpec = mTabHost.newTabSpec(tag).setIndicator(tabview)
                    .setContent(R.id.tab_content_scenario);
        } else if (TAB_BOOKMARK.equals(tag)) {
            tabview = createTabView(mRes.getString(R.string.tab_bar_bookmark),
                    mRes.getDrawable(R.drawable.tab_bookmark_selector));
            tabSpec = mTabHost.newTabSpec(tag).setIndicator(tabview)
                    .setContent(R.id.tab_content_bookmark);
        } else if (TAB_INFO.equals(tag)) {
            tabview = createTabView(mRes.getString(R.string.tab_bar_info),
                    mRes.getDrawable(R.drawable.tab_help_selector));
            tabSpec = mTabHost.newTabSpec(tag).setIndicator(tabview)
                    .setContent(R.id.tab_content_info);
        } else {
            Log.e(TAG, "Error Tab Type");
        }
        mTabHost.addTab(tabSpec);
    }

    private View createTabView(final String text, final Drawable icon) {
        View view = mInflater.inflate(R.layout.tab_item, null);
        view.setBackgroundResource(R.drawable.tab_bg_selector);

        ImageView img = (ImageView) view.findViewById(R.id.tab_item_icon);
        img.setImageDrawable(icon);

        TextView tv = (TextView) view.findViewById(R.id.tab_item_text);
        tv.setText(text);
        tv.setTextColor(mRes.getColorStateList(R.color.tab_text_color));
        return view;
    }

    private void updateTab(String tabTag, int viewHolderId) {
        Log.d(TAG, "[updateTab] + Begin, tabTag=" + tabTag + ",viewHolderId="
                + String.valueOf(viewHolderId));

        FragmentManager fm = this.getSupportFragmentManager();
        Log.i(TAG, "[updateTab] getBackStackEntryCount " + fm.getBackStackEntryCount());
        FragmentTransaction ft = fm.beginTransaction();
        Fragment newFragment = fm.findFragmentById(viewHolderId);
        if (newFragment == null) {
            String fragmentTag = null;
            if (TAB_SEARCH.equals(tabTag)) {
                newFragment = SearchFragment.newInstance();
                fragmentTag = MainActivity.fragment_tag_search;
            } else if (TAB_SCENARIO.equals(tabTag)) {
                newFragment = ScenarioFragment.newInstance();
                fragmentTag = MainActivity.fragment_tag_scenario;
            } else if (TAB_BOOKMARK.equals(tabTag)) {
                newFragment = BookmarkFragment.newInstance();
                fragmentTag = MainActivity.fragment_tag_bookmark;
            } else if (TAB_INFO.equals(tabTag)) {
                newFragment = InfoFragment.newInstance();
                fragmentTag = MainActivity.fragment_tag_info;
            } else {
                Log.e(TAG, "Error Tab Type");
            }
            BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;
            Log.i(TAG, "[updateTab] find fragment == null, do add fragment:" + fragmentTag);
            ft.add(viewHolderId, newFragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        } else {
            if (BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED && TAB_BOOKMARK.equals(tabTag)) {
                Fragment fragment = fm.findFragmentByTag(MainActivity.fragment_tag_bookmark_detail);
                if (fragment != null) {
                    Log.i(TAG, "[updateTab] BookmarkFragment show detail view");
                    mTabHost.setCurrentTabByTag(tabTag);
                } else {
                    BookmarkFragment.removeIndicateWindow();
                    Log.i(TAG, "[updateTab] BookmarkFragment do restartLoader");
                    BookmarkFragment mBookmarkFragment = (BookmarkFragment) newFragment;
                    mBookmarkFragment.getLoaderManager().restartLoader(0, null,
                            mBookmarkFragment.mBookmarkListFragment);
                    BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;
                }
            } else {
                Log.i(TAG, "[updateTab] find fragment != null, do setCurrentTabByTag");
                mTabHost.setCurrentTabByTag(tabTag);
            }
        }
        Log.d(TAG, "[updateTab] + End");
    }

    private final class NonUiHandler extends Handler {

        public NonUiHandler(Looper looper) {
            super(looper);
            Log.d(TAG, "[NonUihandler] Constructor");
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COPY_DB_TO_PHONE:
                    Log.d(TAG, "[NonUihandler][handleMessage] - COPY_DB_TO_PHONE");
                    try {
                        copyDB2Phone();
                    } catch (Exception e) {
                        // Ignore the exception since it's in another thread.
                        Log.e(TAG, e.getMessage());
                    }

                    break;
                case CHECK_UPDATE_COUNT:
                    Log.d(TAG, "[NonUihandler][handleMessage] - CHECK_UPDATE_COUNT");
                    if (Utils.hasNetwork(MainActivity.this)) {
                        try {
                            sendUIHandlerMsg(SHOW_CHECKING_DIALOG, 0);
                            checkForUpdate();
                            sendUIHandlerMsg(HIDE_CHECKING_DIALOG, 500);
                        } catch (Exception e) {
                            // Ignore the exception since it's in another
                            // thread.
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        Log.i(TAG, "no internet");
                    }
                    break;
                case UPDATE_DATA_TO_DB:
                    Log.d(TAG, "[NonUihandler][handleMessage] - UPDATE_DATA_TO_DB");
                    sendUIHandlerMsg(HIDE_NUMBER_OF_UPDATES, 0);
                    if (Utils.hasNetwork(MainActivity.this)) {
                        try {
                            sendUIHandlerMsg(SHOW_DOWNLOAD_PROCESS, 0);
                            updateDB();
                        } catch (Exception e) {
                            // Ignore the exception since it's in another
                            // thread.
                            Log.e(TAG, e.getMessage());
                            sendUIHandlerMsg(HIDE_DOWNLOAD_PROCESS, 0);
                        }
                    }
                    break;
                case UPDATE_TIME_STAMP:
                    Log.d(TAG, "[NonUihandler][handleMessage] - UPDATE_TIME_STAMP");
                    updateTimeStamp();
                    updateCancelTimeStamp();
                    break;
                case UPDATE_CANCEL_TIME:
                    Log.d(TAG, "[NonUihandler][handleMessage] - UPDATE_CANCEL_TIME");
                    sendUIHandlerMsg(HIDE_NUMBER_OF_UPDATES, 0);
                    updateCancelTimeStamp();
                    break;
                default:
                    Log.e(TAG, "[NonUihandler][handleMessage] Something wrong in handleMessage()");
                    break;
            }
        }

        private void copyDB2Phone() throws InvalidKeyException, ClientProtocolException,
                IOException, ParseException {
            Log.d(TAG, "[copyDB2Phone] + Begin");
            dbManagerment = new DBManagerment(MainActivity.this);
            sendNonUIHandlerMsg(CHECK_UPDATE_COUNT, 500);
            Log.d(TAG, "[copyDB2Phone] + End");
        }

        private void updateTimeStamp() {
            if (null != dbManagerment) {
                dbManagerment.updateUpdateTime();
            }
        }

        private void updateCancelTimeStamp() {
            if (null != dbManagerment) {
                dbManagerment.updateCancelUpdateTime();
            }
        }

        private void checkForUpdate() throws InvalidKeyException, ClientProtocolException,
                IOException, ParseException {
            Log.d(TAG, "[checkForUpdate] + Begin");
            RestService service = new RestService();
            mMax_Progress = service.getNumberIfDownloads(dbManagerment.getLastUpdateTime());
            Log.i(TAG, "[checkForUpdate] update count =" + mMax_Progress);
            Log.d(TAG, "[checkForUpdate] + End");
        }

        private void updateDB() throws InvalidKeyException, ClientProtocolException, IOException,
                ParseException, JSONException {
            RestService service = new RestService();
            List<ContentValues> dictionaryList = service.getDictionary(dbManagerment
                    .getLastUpdateTime());
            HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenarioMap = service
                    .getScenario(dbManagerment.getLastUpdateTime());

            // Update record one by one.
            if (dictionaryList != null && dictionaryList.size() > 0)
                for (ContentValues dictionaryContentValue : dictionaryList) {
                    dbManagerment.insertDictionary(dictionaryContentValue);
                    sendUIHandlerMsg(UPDATE_DOWNLOAD_PROCESS, 0);
                }

            if (scenarioMap != null && scenarioMap.size() > 0) {
                Iterator<ContentValues> keyIter = scenarioMap.keySet().iterator();
                while (keyIter.hasNext()) {
                    ContentValues scenraioValues = keyIter.next();
                    HashMap<ContentValues, List<ContentValues>> dialogKeywordsMap = scenarioMap
                            .get(scenraioValues);
                    dbManagerment.insertScenario(scenraioValues, dialogKeywordsMap);
                    // Update record one by one.
                    sendUIHandlerMsg(UPDATE_DOWNLOAD_PROCESS, 0);
                }
            }
            sendUIHandlerMsg(HIDE_DOWNLOAD_PROCESS, 0);
        }
    }

    private final Handler mUiHandler = new Handler() {

        private DialogFragment mCheckUpdateDialog = null;
        private DialogFragment mUpdateCountDialog = null;
        private ProgressDialog mProgressDialog = null;
        private int mProgress = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CHECKING_DIALOG: {
                    Log.d(TAG, "[mUihandler handleMessage] SHOW_CHECKING_DIALOG");
                    mCheckUpdateDialog = MyDialogFragment.newInstance(MainActivity.this,
                            MyDialogFragment.CHECKING_UPDATE);
                    mCheckUpdateDialog.show(MainActivity.this.getSupportFragmentManager(),
                            "check_update_dialog");
                    break;
                }
                case HIDE_CHECKING_DIALOG: {
                    Log.d(TAG, "[mUihandler handleMessage] HIDE_CHECKING_DIALOG");
                    if (mCheckUpdateDialog != null) {
                        mCheckUpdateDialog.dismiss();
                    }
                    if (mMax_Progress > 0) {
                        sendUIHandlerMsg(SHOW_NUMBER_OF_UPDATES, 0);
                    } else {
                        Log.d(TAG, "no need show update count");
                    }
                    break;
                }
                case SHOW_NUMBER_OF_UPDATES: {
                    Log.d(TAG, "[mUihandler handleMessage] SHOW_NUMBER_OF_UPDATES ");
                    mUpdateCountDialog = MyDialogFragment.newInstance(MainActivity.this,
                            MyDialogFragment.UPDATE_COUNT, mMax_Progress);
                    mUpdateCountDialog.show(MainActivity.this.getSupportFragmentManager(),
                            "dialog_update_count");
                    break;
                }
                case HIDE_NUMBER_OF_UPDATES: {
                    Log.d(TAG, "[mUihandler handleMessage] HIDE_NUMBER_OF_UPDATES");
                    if (mUpdateCountDialog != null) {
                        mUpdateCountDialog.dismiss();
                    }
                    break;
                }
                case SHOW_DOWNLOAD_PROCESS: {
                    Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_PROCESS");
                    mProgressDialog = new ProgressDialog(MainActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMax(MainActivity.mMax_Progress);
                    mProgressDialog.show();
                    break;
                }
                case HIDE_DOWNLOAD_PROCESS: {
                    Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_PROCESS");
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    sendNonUIHandlerMsg(UPDATE_TIME_STAMP, 0);
                    break;
                }
                case UPDATE_DOWNLOAD_PROCESS: {
                    Log.d(TAG, "[mUihandler handleMessage] UPDATE_DOWNLOAD_PROCESS");
                    // if (mProgress >= mMax_Progress) {
                    // mProgressDialog.dismiss();
                    // } else {
                    mProgress++;
                    mProgressDialog.setProgress(mProgress);
                    // }
                    break;
                }
                default: {
                    Log.e(TAG, "[mUihandler handleMessage] Something wrong!!!");
                    break;
                }
            }
        }
    };

    private static void sendNonUIHandlerMsg(int msgCode, int delayTime) {
    	if(mNonUiHandler == null) return;
        if (mNonUiHandler.hasMessages(msgCode)) {
            mNonUiHandler.removeMessages(msgCode);
        }
        if (delayTime > 0) {
            mNonUiHandler.sendEmptyMessageDelayed(msgCode, delayTime);
        } else {
            mNonUiHandler.sendEmptyMessage(msgCode);
        }
    }

    private void sendUIHandlerMsg(int msgCode, int delayTime) {
        if (delayTime > 0) {
            mUiHandler.sendEmptyMessageDelayed(msgCode, delayTime);
        } else {
            mUiHandler.sendEmptyMessage(msgCode);
        }
    }

    public static void updateDateToDB() {
        // sendUIHandlerMsg(HIDE_NUMBER_OF_UPDATES,0);
        sendNonUIHandlerMsg(UPDATE_DATA_TO_DB, 0);
    }

    public static void updateCancelTime() {
        sendNonUIHandlerMsg(UPDATE_CANCEL_TIME, 0);
    }

    private boolean canShowUpdate() throws ParseException {
    	if(dbManagerment == null) return false;
        String cancelTime = dbManagerment.getLastCancelUpdateTime();
        if (cancelTime == null || cancelTime.length() <= 0)
            return true;
        Date now = new Date();
        DateFormat cancelFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date cancelDateTime = cancelFormat.parse(cancelTime);
        long diff = now.getTime() - cancelDateTime.getTime();
        long day = diff / (1000 * 60 * 60 * 24);
        if (day >= 1)
            return true;
        return false;
    }
}
