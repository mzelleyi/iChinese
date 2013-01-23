package com.ihuayu.activity;



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

import com.ihuayu.R;
import com.ihuayu.activity.operation.DBManagerment;

/**
 * 
 * @author Kesen
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnTabChangeListener {

	public static DBManagerment dbManagerment = null;
	
	private static final String TAG = "iHuayu:MainActivity";
	private static final String TAB_SEARCH = "Search";
	private static final String TAB_SCENARIO = "Scenario";
	private static final String TAB_BOOKMARK = "Bookmark";
	private static final String TAB_INFO = "Info";
	private TabHost mTabHost = null;
	public static boolean TAB_NEED_UPDATE = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Resources mRes = this.getResources();

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
		
		dbManagerment = new DBManagerment(this);
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
			MainActivity.TAB_NEED_UPDATE = false;
			// Add the fragment
			ft.add(viewHolderId, newFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
		} else {
			if (TAB_NEED_UPDATE && TAB_BOOKMARK.equals(tabTag)) {
				Log.d(TAG,"[updateTab] new BookmarkFragment, do replace");
				MainActivity.TAB_NEED_UPDATE = false;
				newFragment = BookmarkFragment.newInstance();
				ft.replace(viewHolderId, newFragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.commit();
			} else {
				Log.d(TAG,"[updateTab] find fragment != null, do setCurrentTabByTag");
				mTabHost.setCurrentTabByTag(tabTag);
			}
		}
		Log.d(TAG,"[updateTab] + End");
	}
}
