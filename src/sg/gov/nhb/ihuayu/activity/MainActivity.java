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
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.res.Resources;
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
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

/**
 * 
 * @author Kesen
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnTabChangeListener {
	private static final int	COPY_DB_TO_PHONE			= 101;
	private static final int	CHECK_UPDATE_COUNT			= 102;
	private static final int	UPDATE_DB					= 103;
	private static final int	SHOW_DOWNLOAD_DIALOG		= 104;
	private static final int	HIDE_DOWNLOAD_DIALOG		= 105;
	private static final int	SHOW_NUMBER_OF_UPDATES		= 106;
	private static final int	HIDE_NUMBER_OF_UPDATES		= 107;
	private static final int	DOWNLOAD_UPDATES			= 108;
	private static final int	HIDE_DOWNLOAD_UPDATES		= 109;
	private static final int	SHOW_DOWNLOAD_UPDATES_DB	= 110;
	private static final int	HIDE_DOWNLOAD_UPDATES_DB	= 112;
	private static final int	UPDATE_DOWNLOAD_UPDATES_DB	= 113;

	private static final String TAG                    = "iHuayu:MainActivity";
	private static final String TAB_SEARCH             = "Search";
	private static final String TAB_SCENARIO           = "Scenario";
	private static final String TAB_BOOKMARK           = "Bookmark";
	private static final String TAB_INFO               = "Info";
	private static final String THREAD_NAME            = "MainActivity";
	
	public static final String	fragment_tag_search				= "tag_Search";
	public static final String	fragment_tag_scenario			= "tag_Scenario";
	public static final String	fragment_tag_bookmark			= "tag_Bookmark";
	public static final String	fragment_tag_info				= "tag_Info";
	public static final String	fragment_tag_help				= "tag_Help";
	public static final String	fragment_tag_bookmark_detail	= "tag_bookmark_detail";
	public static final String	fragment_tag_search_detail	    = "tag_search_detail";
	public static final String	fragment_tag_scenario_detail	= "tag_scenario_detail";
	
	
	// The update/copyDB Handler Thread
	private HandlerThread		mHandlerThread				= null;
	// The DB Operation Thread
	private static NonUiHandler	mNonUiHandler				= null;

	public static DBManagerment	dbManagerment				= null;
	public static Resources		mRes						= null;
	private TabHost				mTabHost					= null;
	private ProgressDialog		pg							= null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mRes = this.getResources();
        pg = new ProgressDialog(this); 
        pg.setMessage("Updating ..."); 
        pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
        pg.setCancelable(false); 
		//mContext = this.getApplication();		
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.getTabWidget().setDividerDrawable(null);
		mTabHost.addTab(mTabHost.newTabSpec(TAB_SEARCH)
				.setIndicator(mRes.getString(R.string.tab_bar_search), this.getResources().getDrawable(R.drawable.tab_search_selector))
				.setContent(R.id.tab_content_search));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_SCENARIO)
				.setIndicator(mRes.getString(R.string.tab_bar_scenario), this.getResources().getDrawable(R.drawable.tab_scenarios_selector))
				.setContent(R.id.tab_content_scenario));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_BOOKMARK)
				.setIndicator(mRes.getString(R.string.tab_bar_bookmark), this.getResources().getDrawable(R.drawable.tab_bookmark_selector))
				.setContent(R.id.tab_content_bookmark));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_INFO)
				.setIndicator(mRes.getString(R.string.tab_bar_info), this.getResources().getDrawable(R.drawable.tab_help_selector))
				.setContent(R.id.tab_content_info));
		
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++)
        {
			View view = mTabHost.getTabWidget().getChildAt(i);
			view.setBackgroundResource(R.drawable.tab_bg_selector);
			
            TextView title = (TextView)view.findViewById(android.R.id.title);
            title.setTextSize(mRes.getDimension(R.dimen.tab_bar_textview_size));
            title.setTextColor(mRes.getColorStateList(R.color.tab_text_color));
            title.invalidate();
        }
		
		mTabHost.setCurrentTabByTag(TAB_SEARCH);
		
		this.updateTab(TAB_SEARCH, R.id.tab_content_search);
		//TODO maybe we need async to handle this
		mHandlerThread = new HandlerThread(THREAD_NAME);
		mHandlerThread.setPriority(Thread.NORM_PRIORITY);
		mHandlerThread.start();
		mNonUiHandler = new NonUiHandler(mHandlerThread.getLooper());
		if (!FileUtils.hasDBFileInPhone()) {
			sendHandlerMsg(COPY_DB_TO_PHONE);
		} else {
			if (mUiHandler.hasMessages(SHOW_DOWNLOAD_DIALOG)) {
				mUiHandler.removeMessages(SHOW_DOWNLOAD_DIALOG);
			}
			mUiHandler.sendEmptyMessage(SHOW_DOWNLOAD_DIALOG);
			dbManagerment = new DBManagerment(MainActivity.this);
			sendHandlerMsg(CHECK_UPDATE_COUNT);
		}
		
		Log.d(TAG, "[onCreate] + End");
	}

	private static void sendHandlerMsg(int msgCode) {
   		if (mNonUiHandler != null) {
			if (mNonUiHandler.hasMessages(msgCode)) {
				mNonUiHandler.removeMessages(msgCode);
			}
			mNonUiHandler.sendEmptyMessage(msgCode);
		}
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
//		FragmentManager fm = this.getSupportFragmentManager();
//		int stackCount = fm.getBackStackEntryCount();
//		Log.d(TAG, "[onBackPressed] stackCount = "+stackCount);
		// TODO Auto-generated method stub
		// super.onBackPressed();
		dbManagerment.close();
		this.finish();
	}

	@Override
	public void onTabChanged(String tabTag) {
		Log.d(TAG, "[onTabChanged] + Begin,tabTag:" + tabTag);
		// TODO Auto-generated method stub
		
//		//Pop to home page
//        FragmentManager fm = getSupportFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//        	Log.d(TAG, "[onTabChanged] popBackStack ");
//            fm.popBackStack(fm.getBackStackEntryAt(0).getId(),
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
        
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

	private void updateTab(String tabTag, int viewHolderId) {
		Log.d(TAG,"[updateTab] + Begin, tabTag=" + tabTag + ",viewHolderId="	+ String.valueOf(viewHolderId));
		
		FragmentManager fm = this.getSupportFragmentManager();
		Log.i(TAG, "[updateTab] getBackStackEntryCount "+fm.getBackStackEntryCount());
		FragmentTransaction ft = fm.beginTransaction();
		Fragment newFragment = fm.findFragmentById(viewHolderId);
		if (newFragment == null) {
			Log.d(TAG,"[updateTab] find fragment == null, do add");
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
			// Add the fragment
			ft.add(viewHolderId, newFragment, fragmentTag);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
		} else {
			if (BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED && TAB_BOOKMARK.equals(tabTag)) {
				
				Fragment fragment = fm.findFragmentByTag(MainActivity.fragment_tag_bookmark_detail);
				if (fragment != null) {
					Log.i(TAG,"[updateTab] BookmarkFragment show detail view");
					mTabHost.setCurrentTabByTag(tabTag);
				} else {
					BookmarkFragment.removeIndicateWindow();
					Log.i(TAG,"[updateTab] BookmarkFragment do restartLoader");
					BookmarkFragment mBookmarkFragment = (BookmarkFragment)newFragment;
					mBookmarkFragment.getLoaderManager().restartLoader(0, null, mBookmarkFragment.mBookmarkListFragment);
					BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;
				}
			} else {
				Log.d(TAG,"[updateTab] find fragment != null, do setCurrentTabByTag");
				mTabHost.setCurrentTabByTag(tabTag);
			}
		}
		Log.d(TAG,"[updateTab] + End");
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
					//Ignore the exception since it's in another thread.
					Log.i(TAG, e.getMessage());
				}

				break;
			case CHECK_UPDATE_COUNT:
				Log.d(TAG, "[NonUihandler][handleMessage] - CHECK_UPDATE_COUNT");
				try {
//					while(!isDBReady) {
//						Thread.sleep(500);	
//					}
					checkForUpdate();
					
				} catch (Exception e) {
					//Ignore the exception since it's in another thread.
					Log.i(TAG, e.getMessage());
				}
				break;
			case UPDATE_DB:
				Log.d(TAG,
						"[NonUihandler][handleMessage] - UPDATE_DB");
				try { 
					updateDB();
				} catch (Exception e) {
					//Ignore the exception since it's in another thread.
					Log.i(TAG, e.getMessage());
				}
				break;
			default:
				Log.d(TAG,"[NonUihandler][handleMessage] Something wrong in handleMessage()");
				break;
			}
		}
		
		private void copyDB2Phone() throws InvalidKeyException, ClientProtocolException, IOException, ParseException {
			if (mUiHandler.hasMessages(SHOW_DOWNLOAD_DIALOG)) {
				mUiHandler.removeMessages(SHOW_DOWNLOAD_DIALOG);
			}
            mUiHandler.sendEmptyMessage(SHOW_DOWNLOAD_DIALOG);
            dbManagerment = new DBManagerment(MainActivity.this);
            checkForUpdate();
		}
		
		public void checkForUpdate() throws InvalidKeyException, ClientProtocolException, IOException, ParseException {
			if(!Utils.isNetworkAvailable(getApplicationContext())) {
				MyDialogFragment dialog = MyDialogFragment.newInstance(getApplicationContext(),
						MyDialogFragment.NO_INTERNET_CONNETION);
				dialog.show(MainActivity.this.getSupportFragmentManager(),
						"dialog_internet");
				return;
			}
			RestService service = new RestService();
			int updateCount = service.getNumberIfDownloads(dbManagerment.getLastUpdateTime());
			if (mUiHandler.hasMessages(HIDE_DOWNLOAD_DIALOG)) {
				mUiHandler.removeMessages(HIDE_DOWNLOAD_DIALOG);
			}
            mUiHandler.sendEmptyMessage(HIDE_DOWNLOAD_DIALOG);
 			if(updateCount > 0){
				//If user has canceled the update then remind again after 24 hours.
				pg.setMax(updateCount);
				if(canShowUpdate()) {
					if (mUiHandler.hasMessages(SHOW_NUMBER_OF_UPDATES)) {
						mUiHandler.removeMessages(SHOW_NUMBER_OF_UPDATES);
					}
					Message msg = new Message();
					msg.what = SHOW_NUMBER_OF_UPDATES;
					msg.arg1 = updateCount;
		            mUiHandler.sendMessage(msg);
				}
			}
		}
		
		public void updateDB() throws InvalidKeyException, ClientProtocolException, IOException, ParseException, JSONException {
			if (mUiHandler.hasMessages(DOWNLOAD_UPDATES)) {
				mUiHandler.removeMessages(DOWNLOAD_UPDATES);
			}
            mUiHandler.sendEmptyMessage(DOWNLOAD_UPDATES);
            RestService service = new RestService();
            List<ContentValues> dictionaryList = service.getDictionary(dbManagerment.getLastUpdateTime());
            HashMap<ContentValues, HashMap<ContentValues, List<ContentValues>>> scenarioMap = service.getScenario(dbManagerment.getLastUpdateTime());
			if (mUiHandler.hasMessages(HIDE_DOWNLOAD_UPDATES)) {
				mUiHandler.removeMessages(HIDE_DOWNLOAD_UPDATES);
			}
	        mUiHandler.sendEmptyMessage(HIDE_DOWNLOAD_UPDATES);
            
            //Update record one by one.
			if (mUiHandler.hasMessages(SHOW_DOWNLOAD_UPDATES_DB)) {
				mUiHandler.removeMessages(SHOW_DOWNLOAD_UPDATES_DB);
			}
	        mUiHandler.sendEmptyMessage(SHOW_DOWNLOAD_UPDATES_DB);
            
	       
	        int count = 0;
            for(ContentValues dictionaryContentValue : dictionaryList) {
            	dbManagerment.insertDictionary(dictionaryContentValue);
            	Message msg = new Message();
     	        msg.what = UPDATE_DOWNLOAD_UPDATES_DB;
            	msg.arg1 = count ++;
                //Update record one by one.
    			if (mUiHandler.hasMessages(UPDATE_DOWNLOAD_UPDATES_DB)) {
    				mUiHandler.removeMessages(UPDATE_DOWNLOAD_UPDATES_DB);
    			}
            	mUiHandler.sendMessage(msg);
            }
        	if(scenarioMap != null && scenarioMap.size() > 0) {
    			Iterator<ContentValues> keyIter = scenarioMap.keySet().iterator();
    			while(keyIter.hasNext()) {
    				ContentValues scenraioValues = keyIter.next();
    				HashMap<ContentValues, List<ContentValues>> dialogKeywordsMap = scenarioMap.get(scenraioValues);	
    				dbManagerment.insertScenario(scenraioValues, dialogKeywordsMap);
    			       //Update record one by one.
    				if (mUiHandler.hasMessages(UPDATE_DOWNLOAD_UPDATES_DB)) {
        				mUiHandler.removeMessages(UPDATE_DOWNLOAD_UPDATES_DB);
        			}
    				Message msg = new Message();
         	        msg.what = UPDATE_DOWNLOAD_UPDATES_DB;
                	msg.arg1 = count ++;
    				msg.arg1 = count ++;
    				mUiHandler.sendMessage(msg);
    			}
    		}
//			dbManagerment.insertScenario(service.getScenario(dbManagerment.getLastUpdateTime()));
//			dbManagerment.uodateUpdateTime();
			if (mUiHandler.hasMessages(HIDE_DOWNLOAD_UPDATES_DB)) {
				mUiHandler.removeMessages(HIDE_DOWNLOAD_UPDATES_DB);
			}
            mUiHandler.sendEmptyMessage(HIDE_DOWNLOAD_UPDATES_DB);
		}
	}

	private final Handler mUiHandler = new Handler() {
		DialogFragment downloadUpdates = null;
		DialogFragment checkUpdateDialog = null;
		DialogFragment checkNumberOFUpdatesDialog = null;
		int updateCount;
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DOWNLOAD_DIALOG: {
				Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_DIALOG");
				checkUpdateDialog = MyDialogFragment.newInstance(MainActivity.this,
						MyDialogFragment.PREPARE_DB);
				checkUpdateDialog.show(MainActivity.this.getSupportFragmentManager(),
						"check_update_dialog");
				break;
			}
			case HIDE_DOWNLOAD_DIALOG: {
				Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_DIALOG");
				if (checkUpdateDialog != null) {
					checkUpdateDialog.dismiss();
				}
				break;
			}
			case SHOW_NUMBER_OF_UPDATES: {
				Log.d(TAG, "[mUihandler handleMessage] SHOW THE NUMBERS OF UPDATE ");
				updateCount =  msg.arg1;
				checkNumberOFUpdatesDialog = MyDialogFragment.newInstance(MainActivity.this,
						MyDialogFragment.UPDATE_COUNT, updateCount);
				checkNumberOFUpdatesDialog.show(MainActivity.this.getSupportFragmentManager(),
						"check_update_count_dialog");
				break;
			}
			case HIDE_NUMBER_OF_UPDATES: {
				Log.d(TAG, "[mUihandler handleMessage] HIDE_NUMBER_OF_UPDATES");
				if (checkNumberOFUpdatesDialog != null) {
					checkNumberOFUpdatesDialog.dismiss();
				}
				break;
			}
			
			case DOWNLOAD_UPDATES: {
				Log.d(TAG, "[mUihandler handleMessage] DOWNLOAD_UPDATES");
				downloadUpdates = MyDialogFragment.newInstance(MainActivity.this,
						MyDialogFragment.PREPARE_DB);
				downloadUpdates.show(MainActivity.this.getSupportFragmentManager(),
						"download_updates");
				break;
			}
			case HIDE_DOWNLOAD_UPDATES: {
				Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_UPDATES");
				if (downloadUpdates != null) {
					downloadUpdates.dismiss();
				}
				dbManagerment.uodateUpdateTime();
				break;
			}
			case SHOW_DOWNLOAD_UPDATES_DB: {
				Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_UPDATES_DB");
	                pg.show(); 
				break;
			}
			case HIDE_DOWNLOAD_UPDATES_DB: {
				Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_UPDATES_DB");
				if (pg != null) {
					pg.dismiss();
				}
				break;
			}
			case UPDATE_DOWNLOAD_UPDATES_DB: {
				Log.d(TAG, "[mUihandler handleMessage] UPDATE_DOWNLOAD_UPDATES_DB");
				if (pg != null) {
					pg.setProgress(msg.arg1);
				}
				break;
			}
			
			default: {
				Log.e(TAG, "[mUihandler handleMessage] Something wrong!!!");
				break;
			}
			}
		}
	};
	
	public static void updateDB() {
		sendHandlerMsg(UPDATE_DB);
	}
	
	public static void updateCancelTime() {
		dbManagerment.updateCancelUpdateTime();
	}
	
	@SuppressLint("SimpleDateFormat")
	public boolean canShowUpdate() throws ParseException {
		String cancelTime = dbManagerment.getLastCancelUpdateTime();
		if(cancelTime == null || cancelTime.length() <= 0) return true;
		DateFormat  format2 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date(); 
		Date date = format2.parse(cancelTime);
		long diff = now.getTime() - date.getTime();
		long day =  diff / (1000 * 60 * 60 * 24);
		if(day >= 1) return true;
		return false;
	}
}
