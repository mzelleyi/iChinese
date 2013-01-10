/**
 * 
 */
package com.ihuayu.activity;

import com.ihuayu.R;
import com.ihuayu.entry.Cheeses;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * @author Kesen
 *
 */
public class SearchFragment extends Fragment {

	private static final String TAG = "iHuayu:SearchFragment";
	private static final int DELAY_REFRESH_LIST_VIEW = 300;
	private static final int MSG_REFRESH_LISTVIEW = 1;
	//private static final int MSG_UPDATE_SEARCH_LIST = 2;
	private static final int MSG_DO_ALL_SEARCH = 3;
	
	private static FragmentActivity parentActivity = null;
	private static String[] mStrings = Cheeses.sCheeseStrings;
	private static ArrayAdapter<String> mAdapter;
	private static ListView mListView;
	
 // If non-null, this is the current filter the user has provided.
    String mCurFilter;
	
    /**
     * Create a new instance of SearchFragment
     */
    static SearchFragment newInstance( ) {
    	Log.d(TAG, "[newInstance] + Begin");
    	SearchFragment fragment = new SearchFragment();

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
        View v = inflater.inflate(R.layout.search_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		parentActivity = this.getActivity();
		
		final Button btnLanguage = (Button)parentActivity.findViewById(R.id.search_bar_btn);
		//Tag "EN" represent input type is English while "CN" represent Chinese.
		btnLanguage.setTag("EN");
		btnLanguage.setBackgroundResource(R.drawable.btn_english);
		btnLanguage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (btnLanguage.getTag().equals("EN")) {
					btnLanguage.setBackgroundResource(R.drawable.btn_chinese);
					btnLanguage.setTag("CN");
				} else {
					btnLanguage.setBackgroundResource(R.drawable.btn_english);
					btnLanguage.setTag("EN");
				}
			}
		});
		
		mAdapter = new ArrayAdapter<String>(parentActivity,
		            android.R.layout.simple_list_item_1, mStrings);
		
		mListView = (ListView)parentActivity.findViewById(R.id.search_result_list);
		
		final ImageView mPromptImg = (ImageView)parentActivity.findViewById(R.id.search_fragment_prompt_image);
		final ImageView mClearImg = (ImageView)parentActivity.findViewById(R.id.search_bar_edit_clear);
		
	    final EditText mEditText = (EditText)parentActivity.findViewById(R.id.search_bar_edit_text);
//	    mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
//		{
//			@Override
//			public void onFocusChange(View v, boolean hasFocus)
//			{
//				// TODO Auto-generated method stub
//				Log.d(TAG, "[onFocusChange] hasFocus:"+hasFocus);
//				Log.d(TAG, "[onFocusChange] View id:"+v.getId());
//				if (v.getId() == R.id.search_bar_edit_text) {
//					if (hasFocus) {
//						mPromptImg.setVisibility(View.GONE);
//					} else {
//						mPromptImg.setVisibility(View.VISIBLE);
//					}
//				}
//			}
//		});
	    mEditText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "[onTextChanged] + Begin");
				String text = s.toString().trim();
				Log.d(TAG, "[onTextChanged] text string is :"+text);
				if (text == null || text.equals("")) {
					sendSearchMsg(null, DELAY_REFRESH_LIST_VIEW);
					mClearImg.setVisibility(View.GONE);
					mPromptImg.setVisibility(View.VISIBLE);
				} else {
					sendSearchMsg(text, DELAY_REFRESH_LIST_VIEW);
					mClearImg.setVisibility(View.VISIBLE);
					mPromptImg.setVisibility(View.GONE);
				}
				Log.d(TAG, "[onTextChanged] + End");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "[beforeTextChanged] + Begin");
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "[afterTextChanged] + Begin");
			}
		});
	    
		mClearImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "[onClick] mClearImg + Begin");
				mEditText.getText().clear();
			}
		});
	}
	
	private void sendSearchMsg(String key, long delayTime) {
		mUiHandler.removeMessages(MSG_DO_ALL_SEARCH);
		Message msg = mUiHandler.obtainMessage(MSG_DO_ALL_SEARCH);
		msg.obj = key;
		if (delayTime > 0)
			mUiHandler.sendMessageDelayed(msg, delayTime);
		else
			mUiHandler.sendMessage(msg);
	}
	
	private Handler mUiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_LISTVIEW:
				Log.d(TAG, "[mUiHandler][MSG_REFRESH_LISTVIEW]");
				if (parentActivity == null) {
					Log.e(TAG, "mActivity == null.");
					break;
				}
				String resultStr = (String) msg.obj;
				
				mCurFilter = !TextUtils.isEmpty(resultStr) ? resultStr : null;
				mAdapter.getFilter().filter(mCurFilter);
				mListView.setAdapter(mAdapter);
				mListView.invalidate();
				//mAdapter.notifyDataSetChanged();
				break;
			case MSG_DO_ALL_SEARCH:
				Log.d(TAG, "[mUiHandler][MSG_DO_ALL_SEARCH]");
				String searchStr = (String) msg.obj;
				
				//TODO:Search DB then generate arraylist
				//initLocalSearch(searchStr);
				
				Message msg1 = mUiHandler.obtainMessage(MSG_REFRESH_LISTVIEW);
				msg1.obj = searchStr;
				this.sendMessage(msg1);
				break;
			}
		}
	};
	
}
