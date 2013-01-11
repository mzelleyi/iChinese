/**
 * 
 */
package com.ihuayu.activity;

import java.util.ArrayList;
import java.util.List;

import com.ihuayu.R;
import com.ihuayu.entry.Cheeses;
import com.ihuayu.view.MyDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Kesen
 *
 */
public class SearchFragment extends Fragment {

	private static final String TAG = "iHuayu:SearchFragment";
	private static final int DELAY_REFRESH_LIST_VIEW = 300;
	private static final int MSG_DO_SUGGEST_SEARCH = 1;
	private static final int MSG_REFRESH_SUGGEST_LISTVIEW = 2;
	private static final int MSG_DO_ALL_SEARCH = 3;
	private static final int MSG_REFRESH_SEARCH_RESULT = 4;
	
	private static FragmentActivity parentActivity = null;
	private static String[] mStrings = Cheeses.sCheeseStrings;
	private static SearchListAdapter mAdapter;
	private static ListView mListView;
	private static LayoutInflater mInflater;
	private static DialogFragment searchDialog;
	
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
    	mInflater = inflater;
        View v = mInflater.inflate(R.layout.search_fragment, container, false);
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
		
		mAdapter = new SearchListAdapter(parentActivity);
		int size = mStrings.length;
		List<String> data = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			data.add(mStrings[i]);
		}
		mAdapter.setData(data);
		
		mListView = (ListView)parentActivity.findViewById(R.id.search_result_list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "[onItemClick] + arg 1="+arg1+",arg 2="+arg2+",arg 3="+arg3);
			}
		});
		
		final ImageView mPromptImg = (ImageView)parentActivity.findViewById(R.id.search_fragment_prompt_image);
		final ImageView mClearImg = (ImageView)parentActivity.findViewById(R.id.search_bar_edit_clear);
	    final EditText mEditText = (EditText)parentActivity.findViewById(R.id.search_bar_edit_text);
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
					sendSuggentSearchMsg(null, DELAY_REFRESH_LIST_VIEW);
					mClearImg.setVisibility(View.GONE);
					mPromptImg.setVisibility(View.VISIBLE);
				} else {
					sendSuggentSearchMsg(text, DELAY_REFRESH_LIST_VIEW);
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
	    mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "[onEditorAction] + Begin ,actionId = "+actionId);
				boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	Log.d(TAG, "[onEditorAction] -> IME_ACTION_SEARCH ");
		        	String searchKey = mEditText.getText().toString();
		            sendAllSearchMsg(searchKey,0);
		            handled = true;
		        } else if (actionId == EditorInfo.IME_ACTION_DONE){
		        	Log.d(TAG, "[onEditorAction] -> IME_ACTION_SEARCH ");
		        	//String searchKey = mEditText.getText().toString();
		            //sendSearchMsg(searchKey,0);
		        	handled = true;
		        }
		        return handled;
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
	
	private void sendSuggentSearchMsg(String key, long delayTime) {
		mUiHandler.removeMessages(MSG_DO_SUGGEST_SEARCH);
		Message msg = mUiHandler.obtainMessage(MSG_DO_SUGGEST_SEARCH);
		msg.obj = key;
		if (delayTime > 0)
			mUiHandler.sendMessageDelayed(msg, delayTime);
		else
			mUiHandler.sendMessage(msg);
	}
	
	private void sendAllSearchMsg(String key, long delayTime) {
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
			case SearchFragment.MSG_REFRESH_SUGGEST_LISTVIEW:
				Log.d(TAG, "[mUiHandler][MSG_REFRESH_SUGGEST_LISTVIEW]");
				if (parentActivity == null) {
					Log.e(TAG, "mActivity == null.");
					break;
				}
				String resultStr = (String) msg.obj;
				
				mCurFilter = !TextUtils.isEmpty(resultStr) ? resultStr : null;
				mAdapter.getFilter().filter(mCurFilter);
				mListView.setAdapter(mAdapter);
				mListView.setEmptyView(parentActivity.findViewById(R.id.search_fragment_emptyview));
				//mAdapter.notifyDataSetChanged();
				break;
			case SearchFragment.MSG_REFRESH_SEARCH_RESULT:
				Log.d(TAG, "[mUiHandler][MSG_REFRESH_SEARCH_RESULT]");
				searchDialog.dismiss();
				
				String searchResultStr = (String) msg.obj;
				
				mCurFilter = !TextUtils.isEmpty(searchResultStr) ? searchResultStr : null;
				mAdapter.getFilter().filter(mCurFilter);
				int resultCount = mAdapter.getCount();
				Log.d(TAG, "[mUiHandler] Result Count = "+resultCount);
				//if (resultCount == 0) {
					mAdapter.notifyDataSetChanged();
				//}
				break;
			case SearchFragment.MSG_DO_SUGGEST_SEARCH:
				Log.d(TAG, "[mUiHandler][MSG_DO_SUGGEST_SEARCH]");
				String searchStr = (String) msg.obj;
				
				//TODO:Search DB then generate arraylist
				//initLocalSearch(searchStr);
				
				Message msg1 = mUiHandler.obtainMessage(MSG_REFRESH_SUGGEST_LISTVIEW);
				msg1.obj = searchStr;
				this.sendMessage(msg1);
				break;
			case SearchFragment.MSG_DO_ALL_SEARCH:
				Log.d(TAG, "[mUiHandler][MSG_DO_ALL_SEARCH]");
				searchDialog = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.DO_SEARCH_DB);
				searchDialog.show(parentActivity.getSupportFragmentManager(), "dialog_search_db");
				
				String doSearchStr = (String) msg.obj;
				Message msg3 = mUiHandler.obtainMessage(MSG_REFRESH_SEARCH_RESULT);
				msg3.obj = doSearchStr;
				this.sendMessageDelayed(msg3, 1000);
				break;
			}
		}
	};
	
    public static class SearchListAdapter extends ArrayAdapter<String> {

        public SearchListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
        }

        public void setData(List<String> data) {
            clear();
            if (data != null) {
                for (String str : data) {
                    add(str);
                }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.search_result_list_item, parent, false);
            } else {
                view = convertView;
            }

            String item = getItem(position);
            ((TextView)view.findViewById(R.id.search_result_listitem_text)).setText(item);

            return view;
        }
    }
}
