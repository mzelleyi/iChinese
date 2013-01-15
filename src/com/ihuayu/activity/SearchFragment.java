/**
 * 
 */
package com.ihuayu.activity;

import java.util.ArrayList;
import java.util.List;

import com.ihuayu.R;
import com.ihuayu.activity.db.entity.Dictionary;
import com.ihuayu.activity.db.entity.FuzzyResult;
import com.ihuayu.activity.db.entity.QueryType;
import com.ihuayu.view.MyDialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

	private static final String			TAG								= "iHuayu:SearchFragment";
	private static final int			DELAY_REFRESH_LIST_VIEW			= 300;
	private static final int			MSG_DO_SUGGEST_SEARCH			= 1;
	private static final int			MSG_REFRESH_SUGGEST_LISTVIEW	= 2;
	private static final int			MSG_DO_FUZZY_SEARCH				= 3;
	private static final int			MSG_REFRESH_FUZZY_RESULT		= 4;

	private static final int			VIEW_TYPE_SUGGEST				= 0;
	private static final int			VIEW_TYPE_FUZZY				= 1;

	private static FragmentActivity		parentActivity					= null;
	// private static String[] mStrings = Cheeses.sCheeseStrings;
	private static SearchListAdapter	mAdapter;
	private static ListView				mListView;
	private static LayoutInflater		mInflater;
	private static DialogFragment		searchDialog;
	private static QueryType			mSearchKeyType					= null;
	private static EditText				mEditText						= null;
	private static TextView				mFuzzyHint						= null;
	private static TextView				mDivider						= null;
	private static List<Dictionary>		mDicList						= new ArrayList<Dictionary>();
	private InputMethodManager			mInputMethodManager				= null;
	private static boolean				bFuzzyMode						= false;
	
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
		
		mInputMethodManager = (InputMethodManager)parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		final Button btnLanguage = (Button)parentActivity.findViewById(R.id.search_bar_btn);
		//Tag "EN" represent input type is English while "CN" represent Chinese.
		mSearchKeyType = QueryType.EN;
		btnLanguage.setBackgroundResource(R.drawable.btn_english);
		btnLanguage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (mSearchKeyType == QueryType.EN) {
					btnLanguage.setBackgroundResource(R.drawable.btn_chinese);
					mSearchKeyType = QueryType.CN;
				} else {
					btnLanguage.setBackgroundResource(R.drawable.btn_english);
					mSearchKeyType = QueryType.EN;
				}
			}
		});
		
		final ImageView mPromptImg = (ImageView)parentActivity.findViewById(R.id.search_fragment_prompt_image);
		final ImageView mClearImg = (ImageView)parentActivity.findViewById(R.id.search_bar_edit_clear);
		mFuzzyHint = (TextView)parentActivity.findViewById(R.id.search_fuzzy_hint_text);
		mDivider = (TextView)parentActivity.findViewById(R.id.search_fuzzy_divider_text);
	    mEditText = (EditText)parentActivity.findViewById(R.id.search_bar_edit_text);
		
	    mAdapter = new SearchListAdapter(parentActivity);
		mListView = (ListView)parentActivity.findViewById(R.id.search_result_list);
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(parentActivity.findViewById(R.id.search_fragment_emptyview));
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				Log.d(TAG, "[onItemClick] + arg 2="+arg2+",arg 3="+arg3);
				FragmentManager fm = parentActivity.getSupportFragmentManager();
				Fragment newFragment = ResultDetailFragment.newInstance(mDicList,arg2);
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(R.id.tab_content_search, newFragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.addToBackStack(null);
				ft.commit();
				mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(),0);
			}
		});
		
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
		        if (actionId == EditorInfo.IME_ACTION_SEARCH || 
		        		actionId == EditorInfo.IME_ACTION_DONE ||
		        		actionId == EditorInfo.IME_ACTION_GO) {
		        	Log.d(TAG, "[onEditorAction] -> IME_ACTION_SEARCH ");
		        	String searchKey = mEditText.getText().toString();
		            sendAllSearchMsg(searchKey,0);
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
		mUiHandler.removeMessages(MSG_DO_FUZZY_SEARCH);
		Message msg = mUiHandler.obtainMessage(MSG_DO_FUZZY_SEARCH);
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
			case SearchFragment.MSG_REFRESH_SUGGEST_LISTVIEW: {
				Log.d(TAG, "[mUiHandler][MSG_REFRESH_SUGGEST_LISTVIEW]");
				@SuppressWarnings("unchecked")
				List<Dictionary> dicList = (List<Dictionary>) msg.obj;
				
				mDicList.clear();
        		for(Dictionary object: dicList) {
        			mDicList.add(object);
        		}
				
				mFuzzyHint.setVisibility(View.GONE);
				mDivider.setVisibility(View.GONE);
				
				bFuzzyMode = false;
				mAdapter.setData(mDicList);
				mAdapter.notifyDataSetChanged();
				break;
			}
			case SearchFragment.MSG_REFRESH_FUZZY_RESULT: {
				Log.d(TAG, "[mUiHandler][MSG_REFRESH_FUZZY_RESULT]");
				searchDialog.dismiss();

				FuzzyResult fuzzyResult = (FuzzyResult) msg.obj;
				List<Dictionary> dicList =  fuzzyResult.getDictionaryList();
				
				mDicList.clear();
        		for(Dictionary object: dicList) {
        			mDicList.add(object);
        		}
        		
        		mDivider.setVisibility(View.VISIBLE);
				if (!fuzzyResult.isExactResult()) {
					mFuzzyHint.setVisibility(View.VISIBLE);
					//String hintStr = (String) mFuzzyHint.getText();
					//SpannableString spanStr = new SpannableString(hintStr);
					//spanStr.setSpan(new ForegroundColorSpan(Color.BLUE), firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					//mFuzzyHint.setText(spanStr);
				} else {
					mFuzzyHint.setVisibility(View.GONE);
				}				

				//mCurFilter = !TextUtils.isEmpty(searchResultStr) ? searchResultStr : null;
				//mAdapter.getFilter().filter(mCurFilter);
				bFuzzyMode = true;
				mAdapter.setData(mDicList);
				mAdapter.notifyDataSetChanged();
				break;
			}
			case SearchFragment.MSG_DO_SUGGEST_SEARCH: {
				Log.d(TAG, "[mUiHandler][MSG_DO_SUGGEST_SEARCH]");
				String searchStr = (String) msg.obj;
				
				List<Dictionary> dicList = MainActivity.dbManagerment.searchDictionary(mSearchKeyType, searchStr);

				Message msg1 = mUiHandler.obtainMessage(MSG_REFRESH_SUGGEST_LISTVIEW);
				msg1.obj = dicList;
				this.sendMessage(msg1);
				break;
			}
			case SearchFragment.MSG_DO_FUZZY_SEARCH: {
				Log.d(TAG, "[mUiHandler][MSG_DO_FUZZY_SEARCH]");
				mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(),0);
				
				searchDialog = MyDialogFragment.newInstance(parentActivity,
						MyDialogFragment.DO_SEARCH_DB, null);
				searchDialog.show(parentActivity.getSupportFragmentManager(),
						"dialog_search_db");

				String doSearchStr = (String) msg.obj;
				FuzzyResult fuzzyResult = MainActivity.dbManagerment.fuzzySearchDictionary(mSearchKeyType, doSearchStr);
				Message msg2 = mUiHandler.obtainMessage(MSG_REFRESH_FUZZY_RESULT);
				msg2.obj = fuzzyResult;
				this.sendMessageDelayed(msg2, 1000);
				break;
			}
			}
		}
	};
	
    public static class SearchListAdapter extends ArrayAdapter<Dictionary> {
    	
        public SearchListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
        }

        public void setData(List<Dictionary> data) {
            this.clear();
            if (data != null && data.size() > 0) {
            	//Dummy First Item For Divider
                for (Dictionary str : data) {
                    this.add(str);
                }
            }
        }
        
        

	    @Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
	    	if (bFuzzyMode) {
	    		return VIEW_TYPE_FUZZY;
	    	} else {
	    		return VIEW_TYPE_SUGGEST;
	    	}
			//return super.getItemViewType(position);
		}
	    
		@Override
		public int getViewTypeCount()
		{
			// TODO Auto-generated method stub
			return 2;
			//return super.getViewTypeCount();
		}

		/**
         * Populate new items in the list.
         */
        @Override 
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            Log.d(TAG, "[getView] pos = "+position);
            if (convertView == null) {
            	if (getItemViewType(position) == VIEW_TYPE_FUZZY) {
            		view = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
            	} else {
            		view = mInflater.inflate(R.layout.search_suggest_item, parent, false);
            	}
            } else {
                view = convertView;
            }

            Dictionary item = getItem(position);
            if (null != view) {
            	if (getItemViewType(position) == VIEW_TYPE_SUGGEST) {
            		((TextView)view.findViewById(R.id.search_result_listitem_text)).setText(item.getKeyword());
            	} else {
            		((ImageView)view.findViewById(R.id.bookmark_listitem_icon_speaker)).setImageResource(R.drawable.btn_speaker_sb);
					((TextView)view.findViewById(R.id.bookmark_listitem_text_first_line)).setText(item.getKeyword());
					((TextView)view.findViewById(R.id.bookmark_listitem_text_second_line)).setText(item.getDestiontion());
					((TextView)view.findViewById(R.id.bookmark_listitem_text_third_line)).setText(item.getChineser_tone_py());
					((TextView)view.findViewById(R.id.bookmark_listitem_text_type)).setText(item.getDic_catagory());
            	}
            }
            return view;
        }
    }
}
