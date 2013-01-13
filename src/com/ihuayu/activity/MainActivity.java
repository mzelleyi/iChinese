package com.ihuayu.activity;

import com.ihuayu.R;
import com.ihuayu.activity.db.DBDemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.support.v4.app.NavUtils;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

/**
 * 
 * @author Kesen
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnTabChangeListener {

	private static final String TAG = "iHuayu:MainActivity";
	private static final String TAB_SEARCH = "Search";
	private static final String TAB_SCENARIO = "Scenario";
	private static final String TAB_BOOKMARK = "Bookmark";
	private static final String TAB_INFO = "Info";
	private TabHost mTabHost = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec(TAB_SEARCH)
				.setIndicator("Search", this.getResources().getDrawable(R.drawable.tabbar_search))
				.setContent(R.id.tab_content_search));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_SCENARIO)
				.setIndicator("Scenario", this.getResources().getDrawable(R.drawable.tabbar_scenario))
				.setContent(R.id.tab_content_scenario));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_BOOKMARK)
				.setIndicator("Bookmark", this.getResources().getDrawable(R.drawable.tabbar_bookmarks))
				.setContent(R.id.tab_content_bookmark));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_INFO)
				.setIndicator("Info", this.getResources().getDrawable(R.drawable.tabbar_info))
				.setContent(R.id.tab_content_info));
		
		mTabHost.setCurrentTabByTag(TAB_SEARCH);
		this.updateTab(TAB_SEARCH, R.id.tab_content_search);
		
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

	public void onTabChanged(String tabTag) {
		Log.d(TAG, "[onTabChanged] + Begin,tabTag:" + tabTag);
		DBDemo demo = new DBDemo(this);
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
			// Add the fragment
			ft.add(viewHolderId, newFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
		} else {
			Log.d(TAG,"[updateTab] find fragment != null, do setCurrentTabByTag");
			// Replace the fragment
			//ft.replace(viewHolderId, newFragment);
			//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.commit();
			mTabHost.setCurrentTabByTag(tabTag);
		}
		Log.d(TAG,"[updateTab] + End");
	}
}
