package com.ihuayu;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.support.v4.app.NavUtils;



public class MainActivity extends Activity {

	private static final String TAG = "iHuayu:MainActivity";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate] + Begin");
        setContentView(R.layout.activity_main);
        Log.d(TAG, "[onCreate] + End");
    }
	

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "[onStart] + Begin");
		Log.d(TAG, "[onStart] + End");
	}
	
	@Override
	protected void onRestart()
	{
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(TAG, "[onRestart] + Begin");
		Log.d(TAG, "[onRestart] + End");
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "[onResume] + Begin");
		Log.d(TAG, "[onResume] + End");
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "[onPause] + Begin");
		Log.d(TAG, "[onPause] + End");
	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "[onStop] + Begin");
		Log.d(TAG, "[onStop] + End");
	} 

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "[onDestory] + Begin");
		Log.d(TAG, "[onDestory] + End");
	}   
}
