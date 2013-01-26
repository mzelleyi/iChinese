package sg.gov.nhb.ihuayu.activity;



import sg.gov.nhb.ihuayu.activity.operation.DBManagerment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import sg.gov.nhb.ihuayu.R;

/**
 * 
 * @author Kesen
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnTabChangeListener {

	public static DBManagerment	dbManagerment	= null;
	//public static Context       mContext       = null;
	public static Resources     mRes            = null;

	private static final String	TAG				= "iHuayu:MainActivity";
	private static final String	TAB_SEARCH		= "Search";
	private static final String	TAB_SCENARIO	= "Scenario";
	private static final String	TAB_BOOKMARK	= "Bookmark";
	private static final String	TAB_INFO		= "Info";
	private TabHost				mTabHost		= null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mRes = this.getResources();
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				dbManagerment = new DBManagerment(MainActivity.this);
			}
		}).start();
	
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
		
		FragmentManager fm = this.getSupportFragmentManager();
		int stackCount = fm.getBackStackEntryCount();
		Log.d(TAG, "[onBackPressed] stackCount = "+stackCount);
		
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onTabChanged(String tabTag) {
		Log.d(TAG, "[onTabChanged] + Begin,tabTag:" + tabTag);
		// TODO Auto-generated method stub
		
		//Pop to home page
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
        	Log.d(TAG, "[onTabChanged] popBackStack ");
            fm.popBackStack(fm.getBackStackEntryAt(0).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        
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
		FragmentTransaction ft = fm.beginTransaction();
		Fragment newFragment = fm.findFragmentById(viewHolderId);
		if (newFragment == null) {
			Log.d(TAG,"[updateTab] find fragment == null, do add");
			if (TAB_SEARCH.equals(tabTag)) {
				newFragment = SearchFragment.newInstance();
			} else if (TAB_SCENARIO.equals(tabTag)) {
				newFragment = ScenarioFragment.newInstance();
			} else if (TAB_BOOKMARK.equals(tabTag)) {
				newFragment = BookmarkFragment.newInstance();
			} else if (TAB_INFO.equals(tabTag)) {
				newFragment = InfoFragment.newInstance();
			} else {
				Log.e(TAG, "Error Tab Type");
			}
			BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;
			// Add the fragment
			ft.add(viewHolderId, newFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
		} else {
			if (BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED && TAB_BOOKMARK.equals(tabTag)) {
				//Log.i(TAG,"[updateTab] new BookmarkFragment, do replace");
				Log.i(TAG,"[updateTab] BookmarkFragment do removeIndicateWindow");
				BookmarkFragment.removeIndicateWindow();
				Log.i(TAG,"[updateTab] BookmarkFragment do restartLoader");
				BookmarkFragment mBookmarkFragment = (BookmarkFragment)newFragment;
				mBookmarkFragment.getLoaderManager().restartLoader(0, null, mBookmarkFragment.mBookmarkListFragment);
				BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;

				//newFragment = BookmarkFragment.newInstance();
				//ft.replace(viewHolderId, newFragment);
				//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				//ft.commit();
			} else {
				Log.d(TAG,"[updateTab] find fragment != null, do setCurrentTabByTag");
				mTabHost.setCurrentTabByTag(tabTag);
			}
		}
		Log.d(TAG,"[updateTab] + End");
	}
}
