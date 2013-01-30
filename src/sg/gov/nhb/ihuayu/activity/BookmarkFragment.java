/**
 * 
 */
package sg.gov.nhb.ihuayu.activity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.gov.nhb.ihuayu.R;
import sg.gov.nhb.ihuayu.activity.db.entity.Dictionary;
import sg.gov.nhb.ihuayu.activity.rest.AudioPlayer;
import sg.gov.nhb.ihuayu.view.MyDialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
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
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Kesen
 *
 */
public class BookmarkFragment extends Fragment {
	
	private static final String		TAG							= "iHuayu:BookmarkFragment";
	private static FragmentActivity	parentActivity				= null;
	private static LayoutInflater	mInflater					= null;
	private static Resources		mRes						= null;
	public BookmarkListFragment	    mBookmarkListFragment		= null;

	private static WindowManager	mWindowManager				= null;
	private static TextView			mDialogText					= null;
	private static boolean			mShowing;
	private static boolean			mReady;
	private static char             mFirstLetter                = Character.MAX_VALUE;
	private static char				mPrevLetter					= Character.MIN_VALUE;

	private static final int		VIEW_TYPE_NORMAL			= 0;
	private static final int		VIEW_TYPE_DIVIDER			= 1;
	// Message Code
	private static final int		REMOVE_FROM_BOOKMARK		= 501;
	private static final int		PLAY_AUDIO					= 502;
	private static final int		REMOVE_SUCCESS				= 503;
	private static final int		REMOVE_FAILED				= 504;
	private static final int		INDICATE_ADD				= 505;
	private static final int		INDICATE_HIDE				= 506;
	private static final int        SHOW_DOWNLOAD_DIALOG        = 507;
	private static final int        HIDE_DOWNLOAD_DIALOG        = 508;

	// Define Thread Name
	private static final String		THREAD_NAME					= "BookmarkFragmentThread";
	// The DB Handler Thread
	private HandlerThread			mHandlerThread				= null;
	// The DB Operation Thread
	private static NonUiHandler		mNonUiHandler				= null;

	public static boolean			TAB_BOOKMARK_DATA_CHANGED	= false;
	

    /**
     * Create a new instance of SearchFragment
     */
    static BookmarkFragment newInstance( ) {
    	Log.d(TAG, "[newInstance] + Begin");
    	BookmarkFragment fragment = new BookmarkFragment();
        return fragment;
    }
    
    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.d(TAG, "[onCreateView] + Begin");
    	mInflater = inflater;
        View v = mInflater.inflate(R.layout.bookmark_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		parentActivity = this.getActivity();
		mRes = parentActivity.getResources();
		
		mWindowManager = (WindowManager)parentActivity.getSystemService(Context.WINDOW_SERVICE);
		
        
		//Init Thread
		mHandlerThread = new HandlerThread(THREAD_NAME);
		mHandlerThread.setPriority(Thread.NORM_PRIORITY);
		mHandlerThread.start();
		mNonUiHandler = new NonUiHandler(mHandlerThread.getLooper());
		
		FragmentManager fm = parentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mBookmarkListFragment = (BookmarkListFragment) fm.findFragmentById(R.id.fragment_bookmark_listview);
		
		//Set Edit Button On Click Listener
		final Button editBtn = (Button)parentActivity.findViewById(R.id.bookmark_titlebar_btn_edit);
		editBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editBtn.getText().toString().equalsIgnoreCase(
						mRes.getString(R.string.title_bar_btn_edit))) {
					mBookmarkListFragment.enterEditMode();
					editBtn.setText(R.string.title_bar_btn_done);
				} else {
					mBookmarkListFragment.leaveEditMode();
					editBtn.setText(R.string.title_bar_btn_edit);
				}
			}
		});
		
        // Create the list fragment and add it as our sole content.
        if (mBookmarkListFragment == null) {
        	Log.d(TAG, "[onViewCreated] new BookmarkListFragment, do add");
        	mBookmarkListFragment = new BookmarkListFragment();
			ft.add(R.id.fragment_bookmark_listview, mBookmarkListFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        } else {
        	Log.d(TAG, "[onViewCreated] used BookmarkListFragment, do replace");
        	mBookmarkListFragment = new BookmarkListFragment();
			ft.replace(R.id.fragment_bookmark_listview, mBookmarkListFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
//    @Override
//	public void onResume() {
//	    Log.d(TAG, "[onResume] + Begin");
//        super.onResume();
//        mBookmarkListFragment.getLoaderManager().restartLoader(arg0, arg1, arg2)
//        mReady = true;
//    }
//
//	@Override
//    public void onPause() {
//    Log.d(TAG, "[onPause] + Begin");
//        super.onPause();
//        hideIndicateWindow();
//        mReady = false;
//    }
	
	private final static Handler mUiHandler = new Handler()
	{
		DialogFragment downloadDialog = null;
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case REMOVE_FAILED:	{
					Log.d(TAG, "[mUihandler handleMessage] REMOVE_FAILED");
					Toast toast = Toast.makeText(parentActivity, "Remove Failed", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				}
				case REMOVE_SUCCESS: {
					Log.d(TAG, "[mUihandler handleMessage] REMOVE_SUCCESS");
					Toast toast = Toast.makeText(parentActivity, "Remove Successed", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
				}
				case INDICATE_HIDE: {
					Log.d(TAG, "[mUihandler handleMessage] INDICATE_HIDE");
					hideIndicateWindow();
					break;
				}
				case INDICATE_ADD: {
					Log.d(TAG, "[mUihandler handleMessage] INDICATE_ADD");
					addIndicateWindow();
					break;
				}
				case SHOW_DOWNLOAD_DIALOG: {
					Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_DIALOG");
					downloadDialog = MyDialogFragment.newInstance(parentActivity,
							MyDialogFragment.DIALOG_DOWNLOAD);
					downloadDialog.show(parentActivity.getSupportFragmentManager(),
							"dialog_download");
					break;
				}
				case HIDE_DOWNLOAD_DIALOG: {
					Log.d(TAG, "[mUihandler handleMessage] HIDE_DOWNLOAD_DIALOG");
					if (downloadDialog != null) {
					    downloadDialog.dismiss();
					}
					break;
				}
				default:{
					Log.e(TAG, "[mUihandler handleMessage] Something wrong!!!");
					break;
				}
			}
		}
	};
	
	/**
	 * The NonUiHandler to do DB action
	 * @author kesen
	 *
	 */
	private final class NonUiHandler extends Handler
	{
		public NonUiHandler(Looper looper)
		{
			super(looper);
			Log.d(TAG, "[NonUihandler] Constructor");
		}

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case REMOVE_FROM_BOOKMARK:
					Log.d(TAG, "[NonUihandler][handleMessage] - REMOVE_FROM_BOOKMARK");
					doRemoveAction((int[]) msg.obj);
					break;
				case PLAY_AUDIO:
					Log.d(TAG, "[NonUihandler][handleMessage] - PLAY_AUDIO");
					doPlayAudio((String) msg.obj);
					break;
				default:
					Log.d(TAG, "Something wrong in handleMessage()");
					break;
			}
		}
			
		private void doRemoveAction(int[] removeId)
		{
			Log.d(TAG, "[NonUihandler][doRemoveAction] + Begin");
			for (int i = 0; i < removeId.length; i++ ) {
				Log.d(TAG, "[NonUihandler] remove id = "+removeId[i]);
			}
			
//			try
//			{
				MainActivity.dbManagerment.removeFromFavorites(removeId);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				if (mUiHandler != null)
//				{
//					if (mUiHandler.hasMessages(REMOVE_FAILED)) {
//	    				NonUiHandler.removeMessages(REMOVE_FAILED);
//	    			}
//					Log.d(TAG, "[NonUiHandler] Send REMOVE_FAILED Msg");
//					mUiHandler.sendEmptyMessageDelayed(REMOVE_FAILED, 100);
//				}
//			}
			if (mUiHandler != null)
			{
				if (mUiHandler.hasMessages(REMOVE_SUCCESS)) {
					mUiHandler.removeMessages(REMOVE_SUCCESS);
    			}
				Log.d(TAG, "[NonUihandler] Send REMOVE_SUCCESS msg");
				mUiHandler.sendEmptyMessageDelayed(REMOVE_SUCCESS, 100);
			}
			Log.d(TAG, "[NonUihandler][doRemoveAction] + End");
		}
		
		private void doPlayAudio(String audio) {
			Log.d(TAG, "[NonUihandler][doPlayAudio] + Begin");
			String audioStr = null;
			if (audio != null) {
				audioStr = audio;
			}
			Log.d(TAG, "[NonUihandler][doPlayAudio] audioStr = "+audioStr);
			
			AudioPlayer mAudioPlayer = AudioPlayer.newInstance();
			try {
				boolean bDownloaded = mAudioPlayer.doCheckDownloaded(audioStr);
				if (bDownloaded) {
					mAudioPlayer.doPlay(audioStr);
				} else {
					if(!Utils.hasNetwork(parentActivity)) return;
					if (mUiHandler.hasMessages(SHOW_DOWNLOAD_DIALOG)) {
	    				mUiHandler.removeMessages(SHOW_DOWNLOAD_DIALOG);
	    			}
		            mUiHandler.sendEmptyMessage(SHOW_DOWNLOAD_DIALOG);
					boolean result = mAudioPlayer.doDownload(audioStr);
					if (result) {
						if (mUiHandler.hasMessages(HIDE_DOWNLOAD_DIALOG)) {
		    				mUiHandler.removeMessages(HIDE_DOWNLOAD_DIALOG);
		    			}
			            mUiHandler.sendEmptyMessage(HIDE_DOWNLOAD_DIALOG);
			            mAudioPlayer.doPlay(audioStr);
					} else {
						Toast.makeText(parentActivity, mRes.getString(R.string.toast_download_failed), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "[NonUihandler][doPlayAudio] + End");
		}
	}
	
    private static void hideIndicateWindow() {
    	Log.d(TAG, "[hideIndicateWindow] + Begin");
        if (mShowing) {
            mShowing = false;
            if (null != mDialogText) {
            	Log.i(TAG, "[hideIndicateWindow] hide mDialogText");
            	mDialogText.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    private static void addIndicateWindow() {
    	Log.d(TAG, "[addIndicateWindow] + Begin");
    	mReady = true;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        if (null != mDialogText) {
        	Log.i(TAG, "[addIndicateWindow] add mDialogText");
        	mWindowManager.addView(mDialogText, lp);
        }
    }
    
    public static void removeIndicateWindow() {
    	Log.d(TAG, "[removeIndicateWindow] + Begin");
        if (null != mDialogText) {
        	Log.i(TAG, "[removeIndicateWindow] remove mDialogText");
        	mWindowManager.removeView(mDialogText);
        }
        mReady = false;
    }
	
	
	public static class BookmarkListFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<Dictionary>> {
		
		// This is the Adapter being used to display the list's data.
		BookmarkListAdapter			mAdapter = null;

		// Handle indicate case.
		private ListView			mListView			= null;
		//private RemoveWindow		mRemoveWindow		= new RemoveWindow();
		//private Handler				mHandler			= new Handler();
		// List<Character> sDividerCharList = new ArrayList<Character>();

		//The origin data that get from DB.
		List<Dictionary> mOriginBookmarkList = new ArrayList<Dictionary>();
		//The data that fill with divider elements.
		List<Dictionary> mWithDividerList = new ArrayList<Dictionary>();
		//The list used to keep need remove list normal item.
		List<Dictionary> mRemoveBookmarkList = new ArrayList<Dictionary>();
		//The list used to keep need remove list divider item.
		List<Dictionary> mRemoveDividerList = new ArrayList<Dictionary>();
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);
			
			mInflater = (LayoutInflater)parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        
	        // Give some text to display if there is no data. In a real
			// application this would come from a resource.
			this.setEmptyText("No Bookmarks");
	
			// We have a menu item to show in action bar.
			this.setHasOptionsMenu(false);
	
			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new BookmarkListAdapter(this.getActivity());
			this.setListAdapter(mAdapter);
	
			// Start out with a progress indicator.
			this.setListShown(false);
	
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			this.getLoaderManager().initLoader(0, null, this);
			Log.d(TAG, "[onActivityCreated] + End");
		}
		
		
//	    @Override
//		public void onResume() {
//		    Log.d(TAG, "[onResume] + Begin");
//	        if (TAB_BOOKMARK_DATA_CHANGED) {
//				Log.i(TAG,"[BookmarkListFragment][onResume] --- restartLoader");
//				TAB_BOOKMARK_DATA_CHANGED = false;
//				this.getLoaderManager().restartLoader(0, null, this);
//	        }
//	        Log.d(TAG, "[onResume] + End");
//	        //mReady = true;
//	        super.onResume();
//	        mReady = true;
//	    }


		@Override
		public void onDestroyView()
		{
			Log.d(TAG, "[onDestroyView] + Begin");
			// TODO Auto-generated method stub
			removeIndicateWindow();
			super.onDestroyView();
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			Log.d(TAG, "[BookmarkListFragment][onCreateOptionsMenu] + Begin");
			// Since we no need action bar and menu,Choose close
			menu.close();
		}
	
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.d(TAG, "[BookmarkListFragment][onItemClick] + position="+position+",id="+id);
			if (mAdapter.bEditMode) {
				Log.w(TAG, "[BookmarkListFragment][onItemClick] Edit Mode,do return");
				return;
			} else {
				if (mAdapter.getItemViewType(position) == VIEW_TYPE_NORMAL) {
					Dictionary dictionary = mAdapter.getItem(position);
					//int index = mOriginBookmarkList.indexOf(dictionary);
					Log.d(TAG, "[BookmarkListFragment][onItemClick] dictionary info = "+dictionary.getDestiontion());
					
					FragmentManager fm = parentActivity.getSupportFragmentManager();
					Fragment newFragment = BookmarkDetailFragment.newInstance(dictionary);
					FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.tab_content_bookmark, newFragment, MainActivity.fragment_tag_bookmark_detail);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					//ft.addToBackStack(null);
					ft.commit();
				} else {
					Log.w(TAG, "[BookmarkListFragment][onItemClick] Item Type is Divider,do return");
				}
			}
		}
	
		@Override
		public Loader<List<Dictionary>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[BookmarkListFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new BookmarkListLoader(this.getActivity());
		}
	
		@Override
		public void onLoadFinished(Loader<List<Dictionary>> loader, List<Dictionary> data) {
			Log.d(TAG, "[BookmarkListFragment][onLoadFinished] + Begin");
			// Set the new data in the adapter.
			Log.d(TAG, "[BookmarkListFragment] mOriginBookmarkList Size = "+ data.size());
			
			//Save origin get data list
			//mOriginBookmarkList = data;
			
			int originSize = data.size();
			mOriginBookmarkList = new ArrayList<Dictionary>(originSize);
			for (int i = 0; i < originSize; i++) {
				mOriginBookmarkList.add(data.get(i));
			}
//			mWithDividerList = new ArrayList<Dictionary>(data.size());
//			for (int i = 0; i < data.size(); i++) {
//				mWithDividerList.add(data.get(i));
//			}
			
			mWithDividerList.clear();
			if (originSize > 0) {
				Dictionary firstEntry = mOriginBookmarkList.get(0);
				String temp = null;
				char tempchar = firstEntry.getKeyword().charAt(0);
	        	if (isNotCnChar(tempchar)) {
	        		temp = String.valueOf(tempchar);
	        	} else {
	        		temp = String.valueOf(firstEntry.getDestiontion().charAt(0));
	        	}
	        	
	        	//TODO:[Kesen] This solution is very tricky. Need fine tuning.
	        	//New First Entry As Divider
	        	Dictionary firstAddEntry = new Dictionary();
	        	char firstChar = firstEntry.getKeyword().charAt(0);
	        	if (isNotCnChar(firstChar)) {
	        		firstAddEntry.setKeyword(firstEntry.getKeyword());
	        	} else {
	        		firstAddEntry.setKeyword(firstEntry.getDestiontion());
	        	}
	        	firstAddEntry.setId(firstEntry.getId());
	        	firstAddEntry.setIsDivider(true);
	        	mWithDividerList.add(firstAddEntry);
	        	
	        	//Add to dividers list
	        	Log.d(TAG, "add isDivider = "+firstAddEntry.getIsDivider()+"; entry = "+firstAddEntry.getKeyword());
	            for (int i = 0; i < originSize ; i++) {
	            	Log.d(TAG, " i = "+i);
	            	//Add char flag for divider
	            	Dictionary entry = mOriginBookmarkList.get(i);
	            	Character thisChar = null;
	            	char valuechar = entry.getKeyword().charAt(0);
		        	if (isNotCnChar(valuechar)) {
		        		thisChar = valuechar;
		        	} else {
		        		thisChar = entry.getDestiontion().charAt(0);
		        	}
		        	
	            	if(String.valueOf(thisChar).equalsIgnoreCase(temp)) {
	    	        	Log.d(TAG, "add isDivider = "+entry.getIsDivider()+	"; entry = "+entry.getKeyword());
	    	        	mWithDividerList.add(entry);
	            	} else {
	            		temp = String.valueOf(thisChar);
	            		
	            		//New duplicate fist char entry as list item
	                   	Dictionary addEntry = new Dictionary();
	                   	if (isNotCnChar(entry.getKeyword().charAt(0))) {
	                   		addEntry.setKeyword(entry.getKeyword());
	    	        	} else {
	    	        		addEntry.setKeyword(entry.getDestiontion());
	    	        	}
	                   	addEntry.setId(entry.getId());
	                   	addEntry.setIsDivider(true);
	    	        	Log.d(TAG, "add isDivider = "+addEntry.getIsDivider()+"; entry = "+addEntry.getKeyword());
	    	        	//sDividerCharList.add(addEntry.getLabel().charAt(0));
	    	        	mWithDividerList.add(addEntry);
		            	
	                	//Add Normal Item
	    	        	Log.d(TAG, "add isDivider = "+entry.getIsDivider()+"; entry = "+entry.getKeyword());
	    	        	mWithDividerList.add(entry);
	            	}
	            }
			}

            Log.d(TAG, "[BookmarkListFragment] mWithDividerList Size = "+ mWithDividerList.size());
			mAdapter.setData(mWithDividerList);
			//Log.d(TAG, "[BookmarkListFragment] sDividerChars length = "+ sDividerCharList.size());
			Log.d(TAG, "[BookmarkListFragment] Behind Set Data Size = "+ mAdapter.getCount());
	
			// The list should now be shown.
			if (this.isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
			
			// Set Indicate For ListView
			mDialogText = (TextView) mInflater.inflate(R.layout.list_position_indicate, null);
	        mDialogText.setVisibility(View.INVISIBLE);
	        addIndicateWindow();
//			if (mUiHandler.hasMessages(INDICATE_ADD)) {
//				mUiHandler.removeMessages(INDICATE_ADD);
//			}
//			Log.d(TAG, "Send INDICATE_ADD Msg delay 0");
//	        mUiHandler.sendEmptyMessage(INDICATE_ADD);
			
			mListView = this.getListView();
			if (null != mListView) {
				//mListView.setBackgroundColor(R.color.listview_bg_color);
				mListView.setScrollingCacheEnabled(false);
				mListView.setOnScrollListener(new AbsListView.OnScrollListener()
				{
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState)
					{
						// TODO Auto-generated method stub
						Log.d(TAG, "[onScrollStateChanged] + scrollState = "+scrollState);
//						if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//							hideIndicateWindow();
////							if (mUiHandler.hasMessages(INDICATE_HIDE)) {
////			    				mUiHandler.removeMessages(INDICATE_HIDE);
////			    			}
////			    			Log.d(TAG, "[onScroll] Send INDICATE_HIDE Msg");
////				            mUiHandler.sendEmptyMessage(INDICATE_HIDE);
//						} else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//
//						}
					}
					
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
					{
						// TODO Auto-generated method stub
						Log.d(TAG, "[onScroll] + firstVisibleItem = "+firstVisibleItem
								+",visibleItemCount = "+visibleItemCount+",totalItemCount = "+totalItemCount);
				        if (mReady) {
				        	if (mWithDividerList.size() > 0) {
				        		//char firstLetter = mWithDividerList.get(firstVisibleItem).getKeyword().charAt(0);
				        		mFirstLetter = mWithDividerList.get(firstVisibleItem).getKeyword().charAt(0);
				        		if (!mShowing && mFirstLetter != mPrevLetter) {
					                mShowing = true;
					                Log.i(TAG, "show mDialogText");
					                mDialogText.setVisibility(View.VISIBLE);
					            }
				        		mDialogText.setText(((Character)mFirstLetter).toString());
				        		if (mUiHandler.hasMessages(INDICATE_HIDE)) {
				    				mUiHandler.removeMessages(INDICATE_HIDE);
				    			}
				    			Log.d(TAG, "[onScroll] Send INDICATE_HIDE Msg Delay 500");
					            mUiHandler.sendEmptyMessageDelayed(INDICATE_HIDE,500);
					            mPrevLetter = mFirstLetter;
				        	}
				        }
					}
				});
			}
		}
	
		@Override
		public void onLoaderReset(Loader<List<Dictionary>> loader) {
			Log.d(TAG, "[BookmarkListFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	    
	    private void enterEditMode() {
			Log.d(TAG, "[BookmarkListFragment][enterEditMode] + Begin");
			Log.d(TAG, "[BookmarkListFragment] adpater size = "+mAdapter.getCount());
			mAdapter.bEditMode = true;
			mAdapter.notifyDataSetChanged();
			Log.d(TAG, "[BookmarkListFragment][enterEditMode] + End");
		}
		
		private void leaveEditMode() {
			Log.d(TAG, "[BookmarkListFragment][leaveEditMode] + Begin");
			Log.d(TAG, "[BookmarkListFragment] Adapter.getCount = "+mAdapter.getCount());
			mAdapter.bEditMode = false;
			
			Log.d(TAG, "[BookmarkListFragment] === Remove Delete Item ===");
			Log.d(TAG, "[BookmarkListFragment] mWithDividerList size = "+mWithDividerList.size());
			mRemoveBookmarkList.clear();
			for (int i = 0 ; i < mWithDividerList.size(); i++) {
				Log.d(TAG, " i = "+i);
				Dictionary entry = mWithDividerList.get(i);
				if (entry.getNeedDelete()) {
					Log.d(TAG, "remove item i ="+i);
					mRemoveBookmarkList.add(entry);
					//mWithDividerList.remove(entry);
					Log.d(TAG, "delete item count ="+mRemoveBookmarkList.size());
					//Log.d(TAG, "now count ="+mWithDividerList.size());
				}
			}
            for (Dictionary entry : mRemoveBookmarkList) {
            	mWithDividerList.remove(entry);
            }
            Log.d(TAG, "[BookmarkListFragment] After Remove Delete Item, The Size ="+mWithDividerList.size());
            
			
			Log.d(TAG, "[BookmarkListFragment] === Remove Unused Divider ===");
			Log.d(TAG, "[BookmarkListFragment] mWithDividerList size = "+mWithDividerList.size());
			mRemoveDividerList.clear();
			for (int i = 0 ; i < mWithDividerList.size(); i++) {
				Log.d(TAG, " i = "+i);
				Dictionary entry = mWithDividerList.get(i);
				if (entry.getIsDivider()) {
					int nextPos = i + 1;
					if (nextPos < mWithDividerList.size()) {
						Dictionary nextEntry = mWithDividerList.get(nextPos);
						if (nextEntry.getIsDivider()) {
			        		Log.d(TAG, " remove divider i = "+i);
			        		mRemoveDividerList.add(entry);
			        		//mWithDividerList.remove(entry);
			        		Log.d(TAG, " delete divider count ="+mRemoveDividerList.size());
				        	//Log.d(TAG, " now count ="+mWithDividerList.size());
			        	}
					} else {
						Log.d(TAG, " remove the last divider i = "+i);
						mRemoveDividerList.add(entry);
						Log.d(TAG, " delete divider count ="+mRemoveDividerList.size());
					}
				}
			}
            for (Dictionary entry : mRemoveDividerList) {
            	mWithDividerList.remove(entry);
            	//sDividerCharList.remove(entry.getLabel().charAt(0));
            }
            //Log.d(TAG, "[BookmarkListFragment] After Remove Unused Divider, The sDividerCharList Size ="+sDividerCharList.size());
            Log.d(TAG, "[BookmarkListFragment] After Remove Unused Divider, The mWithDividerList Size ="+mWithDividerList.size());
			
			mAdapter.setData(mWithDividerList);
			mAdapter.notifyDataSetChanged();
			
			//Remove From DB
			int size = mRemoveBookmarkList.size();
			int[] removeId = new int[size];
            for (int i = 0; i < size; i++) {
            	removeId[i] = mRemoveBookmarkList.get(i).getId();
            }
            
    		if (mNonUiHandler != null)
    		{
    			if (mNonUiHandler.hasMessages(REMOVE_FROM_BOOKMARK)) {
    				mNonUiHandler.removeMessages(REMOVE_FROM_BOOKMARK);
    			}
    			Log.d(TAG, "[BookmarkListFragment] Send REMOVE_FROM_BOOKMARK Msg");
    			Message msg = Message.obtain(mNonUiHandler, REMOVE_FROM_BOOKMARK, removeId);
    			mNonUiHandler.sendMessageDelayed(msg, 100);
    		}
           	//MainActivity.dbManagerment.removeFromFavorites(removeId);
			Log.d(TAG, "[BookmarkListFragment][leaveEditMode] + End");
		}
	
//		//The class used to show indicate for list view 
//	    private final class RemoveWindow implements Runnable {
//	        public void run() {
//	            removeWindow();
//	        }
//	    }
		
	}
	
	
	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class BookmarkListLoader extends AsyncTaskLoader<List<Dictionary>> {
	    List<Dictionary> mBookmarkList = null;

	    public BookmarkListLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<Dictionary> loadInBackground() {
	    	Log.d(TAG, "[BookmarkListLoader][loadInBackground] + Begin");
	    	
	    	List<Dictionary> mList = MainActivity.dbManagerment.getAllBookMarks();

//	        // Create corresponding array of entries and load their labels.
//	        List<Dictionary> entries = new ArrayList<Dictionary>(apps.size());
//	        for (int i=0; i<apps.size(); i++) {
//	        	Dictionary entry = new Dictionary(this, apps.get(i));
//	            entry.loadLabel(context);
//	            entries.add(entry);
//	        }

	        // Sort the list.
	        Collections.sort(mList, ALPHA_COMPARATOR);
	        
	        Log.d(TAG, "[BookmarkListLoader][loadInBackground] List Size ="+mList.size());
	        Log.d(TAG, "[BookmarkListLoader][loadInBackground] + End");
	        // Done!
	        return mList;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(List<Dictionary> BookmarkList) {
	    	Log.d(TAG, "[BookmarkListLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (BookmarkList != null) {
	                this.onReleaseResources(BookmarkList);
	            }
	        }
	        List<Dictionary> oldBookmarkList = BookmarkList;
	        mBookmarkList = BookmarkList;

	        if (this.isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(BookmarkList);
	        }

	        // At this point we can release the resources associated with
	        // 'oldBookmarkList' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (oldBookmarkList != null) {
	            this.onReleaseResources(oldBookmarkList);
	        }
	        Log.d(TAG, "[BookmarkListLoader][deliverResult] + End");
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	    	Log.d(TAG, "[BookmarkListLoader][onStartLoading] + Begin");
	        if (mBookmarkList != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            this.deliverResult(mBookmarkList);
	        }

	        if (takeContentChanged() || mBookmarkList == null) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            this.forceLoad();
	        }
	        Log.d(TAG, "[BookmarkListLoader][onStartLoading] + End");
	    }

	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override 
	    protected void onStopLoading() {
	    	Log.d(TAG, "[BookmarkListLoader][onStopLoading] + Begin");
	        // Attempt to cancel the current load task if possible.
	        this.cancelLoad();
	    }

	    /**
	     * Handles a request to cancel a load.
	     */
	    @Override 
	    public void onCanceled(List<Dictionary> BookmarkList) {
	    	Log.d(TAG, "[BookmarkListLoader][onCanceled] + Begin");
	        super.onCanceled(BookmarkList);

	        // At this point we can release the resources associated with 'BookmarkList' if needed.
	        this.onReleaseResources(BookmarkList);
	    }

	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override 
	    protected void onReset() {
	    	Log.d(TAG, "[BookmarkListLoader][onReset] + Begin");
	        super.onReset();

	        // Ensure the loader is stopped
	        this.onStopLoading();

	        // At this point we can release the resources associated with 'BookmarkList' if needed.
	        if (mBookmarkList != null) {
	            this.onReleaseResources(mBookmarkList);
	            mBookmarkList = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(List<Dictionary> BookmarkList) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    	BookmarkList.clear();
	    }
	}

	public static class BookmarkListAdapter extends ArrayAdapter<Dictionary> {
	    
	    private boolean bEditMode = false;
	    
	    public BookmarkListAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	    	super(context, R.layout.bookmark_list_item);
	        //mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<Dictionary> data) {
	    	Log.d(TAG, "[BookmarkListAdapter][setData] + Begin");
	        this.clear();
	        if (data != null) {
	        	Log.d(TAG, "[BookmarkListAdapter][setData] Size = "+data.size());
	            for (Dictionary entry : data) {
	                this.add(entry);
	            }
	        }
	    }
	    
	    @Override
		public int getItemViewType(int position)
		{
			// TODO Auto-generated method stub
	    	Dictionary item = this.getItem(position);
	    	if (item.getIsDivider()) {
	    		return VIEW_TYPE_DIVIDER;
	    	} else {
	    		return VIEW_TYPE_NORMAL;
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
		
		class ViewHolder
		{
			public ImageView    listImageDelete     = null;
			public ImageView	listImageSpearker   = null;
			public TextView		listTextView1		= null;
			public TextView		listTextView2		= null;
			public TextView		listTextView3		= null;
			public TextView		listTextCategory	= null;
		}

		/**
		 * Populate new items in the list.
		 */
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "[BookmarkListAdapter][getView] + pos = "+position+",EditMode = "+bEditMode);
	        //View view = null;
	    	final int pos = position;
			ViewHolder holder = null;
			TextView 	separatorText = null;
			
			final Dictionary item = this.getItem(position);
			Log.d(TAG, "isDivider = "+item.getIsDivider()+"; label = "+item.getKeyword());
			if (item != null) {				
				if (convertView == null) {
					if (this.getItemViewType(position) == VIEW_TYPE_DIVIDER) {
						Log.d(TAG, "convertView == null, inflate -> find bookmark_list_separate");
						convertView = mInflater.inflate(R.layout.bookmark_list_separate, parent, false);
						separatorText = (TextView) convertView.findViewById(R.id.bookmark_listitem_separate_text);
					} else {
						Log.d(TAG, "convertView == null, inflate -> find bookmark_list_item");
						holder = new ViewHolder();
						convertView = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
						
						holder.listImageDelete  = (ImageView) convertView.findViewById(R.id.bookmark_listview_viewstub_img);
						holder.listImageSpearker = (ImageView) convertView.findViewById(R.id.bookmark_listitem_icon_speaker);
						holder.listTextView1 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_first_line);
						holder.listTextView2 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_second_line);
						holder.listTextView3 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_third_line);
						holder.listTextCategory = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_type);
						convertView.setTag(holder);
					}
				} else {
					if (this.getItemViewType(position) == VIEW_TYPE_DIVIDER) {
						Log.d(TAG, "convertView != null, find separatorText");
						//convertView = mInflater.inflate(R.layout.bookmark_list_separate, parent, false);
						separatorText = (TextView) convertView.findViewById(R.id.bookmark_listitem_separate_text);
					} else {
						Log.d(TAG, "convertView != null, find bookmark_list_item");
						holder = (ViewHolder) convertView.getTag();
					}
				}
				
				if (null != convertView) {
					if (this.getItemViewType(position) == 1) {
						if (separatorText != null) {
							Log.d(TAG, "set Separator Text");
							separatorText.setText(String.valueOf(item.getKeyword().charAt(0)));
						}
					} else {
						Log.d(TAG, "set ListItem Data");
						
						final ImageView deleteImg = holder.listImageDelete;
						if (bEditMode) {
							deleteImg.setVisibility(View.VISIBLE);
							if (item.getNeedDelete()) {
								deleteImg.setImageResource(R.drawable.list_check);
							} else {
								deleteImg.setImageResource(R.drawable.list_uncheck);
							}
							
							deleteImg.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View arg0)
								{
									// TODO Auto-generated method stub
									if (item.getNeedDelete()) {
										deleteImg.setImageResource(R.drawable.list_uncheck);
										item.setNeedDelete(false);
									} else {
										deleteImg.setImageResource(R.drawable.list_check);
										item.setNeedDelete(true);
									}
								}
							});
						} else {
							holder.listImageDelete.setVisibility(View.GONE);
							item.setNeedDelete(false);
						}
	
						if (holder.listImageSpearker != null) {
							if (bEditMode) {
								holder.listImageSpearker.setImageResource(R.drawable.btn_speaker);
								holder.listImageSpearker.setClickable(false);
							} else {
								holder.listImageSpearker.setImageResource(R.drawable.btn_speaker);
								holder.listImageSpearker.setOnClickListener(new View.OnClickListener()	{
									@Override
									public void onClick(View v)
									{
										// TODO Auto-generated method stub
										Dictionary item = getItem(pos);
										String strAudio = item.getChinese_audio();
										Log.d(TAG, "[onClick] Chinese_audio = "+strAudio);
								   		if (mNonUiHandler != null) {
											if (mNonUiHandler.hasMessages(PLAY_AUDIO)) {
												mNonUiHandler.removeMessages(PLAY_AUDIO);
											}
											Message msg = Message.obtain(mNonUiHandler, PLAY_AUDIO,strAudio);
											mNonUiHandler.sendMessage(msg);
										}
									}
								});
							}
						}
						if (holder.listTextView1 != null) {
							holder.listTextView1.setText(item.getKeyword());
						}
						if (holder.listTextView2 != null) {
							holder.listTextView2.setText(item.getDestiontion());
						}
						if (holder.listTextView3 != null) {
							holder.listTextView3.setText(item.getChineser_tone_py());
						}
						if (holder.listTextCategory != null) {
							holder.listTextCategory.setText(item.getDic_catagory());
						}
					}
				}
			}
	        return convertView;
	    }
	}
	
	private static boolean isNotCnChar (char thisChar) {
		if(((thisChar>='a'&& thisChar<='z') || (thisChar>='A'&&thisChar<='Z')) || Character.isDigit(thisChar)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
	public static final Comparator<Dictionary> ALPHA_COMPARATOR = new Comparator<Dictionary>() {
	    private final Collator sCollator = Collator.getInstance();
	    public int compare(Dictionary object1, Dictionary object2) {
	    	char Char1 = object1.getSrc().charAt(0);
	    	char Char2 = object2.getSrc().charAt(0);
	    	boolean object_1_not_cn = isNotCnChar(Char1);
	    	boolean object_2_not_cn = isNotCnChar(Char2);
	    	Log.d(TAG, "object_1_not_cn ="+object_1_not_cn);
	    	Log.d(TAG, "object_2_not_cn ="+object_2_not_cn);
	    	if (object_1_not_cn) {
	    		if (object_2_not_cn) {
	    			return sCollator.compare(object1.getSrc(), object2.getSrc());
	    		} else {
	    			return sCollator.compare(object1.getSrc(), object2.getDestiontion());
	    		}
	    	} else {
	    		if (object_2_not_cn) {
	    			return sCollator.compare(object1.getDestiontion(), object2.getSrc());
	    		} else {
	    			return sCollator.compare(object1.getDestiontion(), object2.getDestiontion());
	    		}
	    	}
	    }
	};
}
