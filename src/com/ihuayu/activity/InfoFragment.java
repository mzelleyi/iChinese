/**
 * 
 */
package com.ihuayu.activity;

import com.ihuayu.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Kesen
 *
 */
public class InfoFragment extends Fragment {

	private static final String TAG = "iHuayu:InfoFragment";

    /**
     * Create a new instance of SearchFragment
     */
    static InfoFragment newInstance( ) {
    	Log.d(TAG, "[newInstance] + Begin");
    	InfoFragment fragment = new InfoFragment();

        // Supply num input as an argument.
        //Bundle args = new Bundle();
        //args.putInt("num", num);
        //f.setArguments(args);
        return fragment;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//mNum = getArguments() != null ? getArguments().getInt("num") : 1;
	}
	
    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.d(TAG, "[onCreateView] + Begin");
        View v = inflater.inflate(R.layout.info_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}
}
