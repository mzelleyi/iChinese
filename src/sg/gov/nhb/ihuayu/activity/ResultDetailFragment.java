package sg.gov.nhb.ihuayu.activity;


import java.util.ArrayList;
import java.util.List;

import sg.gov.nhb.ihuayu.activity.db.entity.Dictionary;
import sg.gov.nhb.ihuayu.activity.rest.AudioPlayer;
import sg.gov.nhb.ihuayu.view.MyDialogFragment;

import sg.gov.nhb.ihuayu.R;

import android.content.Context;
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
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ResultDetailFragment extends Fragment {

	private static final String		TAG						= "iHuayu:ResultDetailFragment";
	private static FragmentActivity	parentActivity			= null;
	// private static List<Dictionary> mResultList = new
	// ArrayList<Dictionary>();
	private static Dictionary		mCurrentDic				= null;
	// private static int mDialogType = -1;
	private static ImageView		mFavoriteImg			= null;
	private static Button			mBtnPrev				= null;
	private static Button			mBtnNext				= null;
	private static boolean			mBeFavorited			= false;
	
	private static final int		UPDATE_CURRENT		    = 1;
	private static final int		UPDATE_NEXT	        	= 2;
	private static final int		UPDATE_PREV	        	= 3;
	private static int           	mUpdateType		        = UPDATE_CURRENT;

	// Message Code
	private static final int		CHECK_FAV_STATUS		= 501;
	private static final int		UPDATE_FAV_IMAGE		= 502;
	private static final int		ADD_TO_BOOKMARK	    	= 503;
	private static final int        UPDATE_ADD_RESULT       = 504;
	private static final int		REMOVE_FROM_BOOKMARK	= 505;
	private static final int		UPDATE_REMOVE_RESULT	= 506;
	private static final int        PLAY_CHINESE_AUDIO      = 507;
	private static final int        PLAY_SENTANCE_AUDIO     = 508;
	private static final int        SHOW_DOWNLOAD_DIALOG    = 509;
	private static final int        HIDE_DOWNLOAD_DIALOG    = 10;
	
	private static DialogFragment   mCurrentDialog = null;

	
	// Define Thread Name
	private static final String		THREAD_NAME		= "ResultDetailFragmentThread";
	// The DB Handler Thread
	private HandlerThread			mHandlerThread	= null;
	// The DB Operation Thread
	private static NonUiHandler	    mNonUiHandler	= null;
	

    /**
     * Create a new instance of ResultDetailFragment
     */
    static ResultDetailFragment newInstance(Dictionary dictionary) {
    	Log.d(TAG, "[newInstance] + Begin");
    	ResultDetailFragment fragment = new ResultDetailFragment();
    	mCurrentDic = dictionary;
    	Log.d(TAG, "[newInstance] mCurrentDic.info = "+mCurrentDic.getDestiontion());
        return fragment;
    }
    
    /**
     * When creating, retrieve this parameter from its arguments.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.d(TAG, "[onCreateView] + Begin");
        View v = inflater.inflate(R.layout.result_detail_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		parentActivity = this.getActivity();
		if (mCurrentDic != null) {
			TextView TextView1 = (TextView) parentActivity.findViewById(R.id.result_detail_des_first_line);
			TextView TextView2 = (TextView) parentActivity.findViewById(R.id.result_detail_des_second_line_text);
			if (mCurrentDic.getLanguage_dir().equalsIgnoreCase("en2sc")) {
				Log.i(TAG, "[onViewCreated] is english dictionary");
				TextView1.setText(mCurrentDic.getKeyword());
				TextView2.setText(mCurrentDic.getDestiontion());
			} else {
				Log.i(TAG, "[onViewCreated] is chinese dictionary");
				TextView1.setText(mCurrentDic.getDestiontion());
				TextView2.setText(mCurrentDic.getKeyword());
			}
			TextView TextView3 = (TextView) parentActivity.findViewById(R.id.result_detail_des_third_line);
			TextView3.setText(mCurrentDic.getChineser_tone_py());
			TextView TextViewSourceLabel = (TextView) parentActivity.findViewById(R.id.result_detail_des_source_text);
			TextViewSourceLabel.setText(R.string.dic_detail_source_text);
			TextView TextView1SourceInfo = (TextView) parentActivity.findViewById(R.id.result_detail_des_source_info);
			TextView1SourceInfo.setText(mCurrentDic.getDic_catagory());
		}
		
		ImageView audioImg = (ImageView)parentActivity.findViewById(R.id.result_detail_des_second_line_icon);
		audioImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "[onClick] audioImg + Begin");
				String sentanceAudio = mCurrentDic.getChinese_audio();
				Log.d(TAG, "[onClick] sentanceAudio = "+sentanceAudio);
		   		if (mNonUiHandler != null) {
					if (mNonUiHandler.hasMessages(PLAY_CHINESE_AUDIO)) {
						mNonUiHandler.removeMessages(PLAY_CHINESE_AUDIO);
					}
					Message msg = Message.obtain(mNonUiHandler, PLAY_CHINESE_AUDIO,sentanceAudio);
					mNonUiHandler.sendMessage(msg);
				}
			}
		});
		
		mBtnPrev = (Button)parentActivity.findViewById(R.id.result_detail_footbar_btn_prev);
		mBtnPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "[onClick] btnPrev ");
				//mCurrentDic = mResultList.get(mCurrentPos);
				mUpdateType = UPDATE_PREV;
				FragmentManager fm = parentActivity.getSupportFragmentManager();
				ResultDemoFragment list = (ResultDemoFragment) fm.findFragmentById(R.id.result_detail_demo_listview);
				list.getLoaderManager().restartLoader(0, null, list);
				
			}
		});
		
		mBtnNext = (Button)parentActivity.findViewById(R.id.result_detail_footbar_btn_next);
		mBtnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "[onClick] btnNext ");
				//mCurrentDic = mResultList.get(mCurrentPos);
				mUpdateType = UPDATE_NEXT;
				FragmentManager fm = parentActivity.getSupportFragmentManager();
				ResultDemoFragment list = (ResultDemoFragment) fm.findFragmentById(R.id.result_detail_demo_listview);
				list.getLoaderManager().restartLoader(0, null, list);
			}
		});
		
		mFavoriteImg = (ImageView)parentActivity.findViewById(R.id.result_detail_des_favorite_img);
		mFavoriteImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mBeFavorited) {
					//mDialogType = MyDialogFragment.ADD_TO_BOOKMARK;
					showDialog(MyDialogFragment.ADD_TO_BOOKMARK,false);
				} else {
					showDialog(MyDialogFragment.REMOVE_FROM_BOOKMARK,false);
				}
			}
		});
		
		//Set Back Button On Click Listener
		Button backBtn = (Button)parentActivity.findViewById(R.id.result_detail_title_bar_backbtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fm = parentActivity.getSupportFragmentManager();
				fm.popBackStack();
			}
		});
		
		//Init Thread
		mHandlerThread = new HandlerThread(THREAD_NAME);
		mHandlerThread.setPriority(Thread.NORM_PRIORITY);
		mHandlerThread.start();
		mNonUiHandler = new NonUiHandler(mHandlerThread.getLooper());
		
		updateDataFragment();
	}
	
	private final Handler mUiHandler = new Handler() {
         DialogFragment downloadDialog = null;

         @Override
         public void handleMessage(Message msg) {
             switch (msg.what) {
				 case UPDATE_FAV_IMAGE: {
					 Log.d(TAG, "[mUihandler handleMessage] UPDATE_FAV_IMAGE");
					 updateFavoriteImg((Boolean) msg.obj);
					 break;
				 }
				 case UPDATE_ADD_RESULT: {
					 Log.d(TAG, "[mUihandler handleMessage] UPDATE_ADD_RESULT");
					 if (msg.arg1 > 0) {
						 // mDialogType =
						 // MyDialogFragment.ADD_RESULT;
						 BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = true;
						 mBeFavorited = true;
						 showDialog(MyDialogFragment.ADD_RESULT, true);
						 updateFavoriteImg(true);
					 } else {
						 // mDialogType = -1;
						 BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;
						 mBeFavorited = false;
						 showDialog(MyDialogFragment.ADD_RESULT, false);
						 updateFavoriteImg(false);
					 }
					 break;
				 }
				 case UPDATE_REMOVE_RESULT: {
					 Log.d(TAG, "[mUihandler handleMessage] UPDATE_REMOVE_RESULT");
					 if (msg.arg1 > 0) {
						 // mDialogType = -1;
						 mBeFavorited = false;
						 BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = true;
						 showDialog(MyDialogFragment.REMOVE_RESULT, true);
						 updateFavoriteImg(false);
					 } else {
						 // mDialogType = -1;
						 mBeFavorited = true;
						 BookmarkFragment.TAB_BOOKMARK_DATA_CHANGED = false;
						 showDialog(MyDialogFragment.REMOVE_RESULT, false);
						 updateFavoriteImg(true);
					 }
					 break;
				 }
				 case SHOW_DOWNLOAD_DIALOG: {
					 Log.d(TAG, "[mUihandler handleMessage] SHOW_DOWNLOAD_DIALOG");
					 downloadDialog = MyDialogFragment.newInstance(parentActivity,
					         MyDialogFragment.DIALOG_DOWNLOAD, false, null);
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
				 default: {
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
				case CHECK_FAV_STATUS:
					Log.d(TAG, "[NonUihandler][handleMessage] - CHECK_FAV_STATUS");
					doCheckMarked();
					break;
				case ADD_TO_BOOKMARK:
					Log.d(TAG, "[NonUihandler][handleMessage] - ADD_TO_BOOKMARK");
					doAddToBookmark();
					break;
				case REMOVE_FROM_BOOKMARK:
					Log.d(TAG, "[NonUihandler][handleMessage] - REMOVE_FROM_BOOKMARK");
					doRemoveFromBookmark();
					break;
				case PLAY_CHINESE_AUDIO:
					Log.d(TAG, "[NonUihandler][handleMessage] - PLAY_CHINESE_AUDIO");
					doPlayAudio((String) msg.obj);
					break;
				case PLAY_SENTANCE_AUDIO:
					Log.d(TAG, "[NonUihandler][handleMessage] - PLAY_SENTANCE_AUDIO");
					doPlayAudio((String) msg.obj);
					break;
				default:
					Log.d(TAG, "[NonUihandler][handleMessage] Something wrong in handleMessage()");
					break;
			}
		}
		
		private void doAddToBookmark()
		{
			Log.d(TAG, "[NonUihandler][doAddToBookmark] + Begin");
			Log.d(TAG, "[NonUihandler][doAddToBookmark] mCurrent Dictionary ID = "+mCurrentDic.getId());
			int result = (int) MainActivity.dbManagerment.addBookmark(mCurrentDic.getId());
			Log.d(TAG, "[NonUihandler][doRemoveFromBookmark] result = "+result);
			if (mUiHandler != null)
			{
				if (mUiHandler.hasMessages(UPDATE_ADD_RESULT)) {
					mUiHandler.removeMessages(UPDATE_ADD_RESULT);
    			}
				Message msg = Message.obtain(mUiHandler, UPDATE_ADD_RESULT);
				msg.arg1 = result;
				mUiHandler.sendMessageDelayed(msg, 100);
			}
			Log.d(TAG, "[NonUihandler][doAddToBookmark] + End");
		}
		
		private void doRemoveFromBookmark()
		{
			Log.d(TAG, "[NonUihandler][doRemoveFromBookmark] + Begin");
			Log.d(TAG, "[NonUihandler][doRemoveFromBookmark] mCurrent Dictionary ID = "+mCurrentDic.getId());
			int result = MainActivity.dbManagerment.removeFromFavorites(mCurrentDic.getId());
			Log.d(TAG, "[NonUihandler][doRemoveFromBookmark] result = "+result);
			if (mUiHandler != null)
			{
				if (mUiHandler.hasMessages(UPDATE_REMOVE_RESULT)) {
					mUiHandler.removeMessages(UPDATE_REMOVE_RESULT);
    			}
				Message msg = Message.obtain(mUiHandler, UPDATE_REMOVE_RESULT);
				msg.arg1 = result;
				mUiHandler.sendMessageDelayed(msg, 100);
			}
			Log.d(TAG, "[NonUihandler][doRemoveFromBookmark] + End");
		}
			
		private void doCheckMarked()
		{
			Log.d(TAG, "[NonUihandler][doCheckMarked] + Begin");
			int dictionaryId = -1;
			if (null != mCurrentDic) {
				dictionaryId = mCurrentDic.getId();
			}
			Log.d(TAG, "[NonUihandler][doCheckMarked] mCurrent Dictionary ID = "+dictionaryId);
			if (dictionaryId != -1) {
				mBeFavorited = MainActivity.dbManagerment.hasbookmarked(mCurrentDic.getId());
			}
			Log.d(TAG, "[NonUihandler][doCheckMarked] hasbookmarked = "+mBeFavorited);
			if (mUiHandler != null)
			{
				if (mUiHandler.hasMessages(UPDATE_FAV_IMAGE)) {
					mUiHandler.removeMessages(UPDATE_FAV_IMAGE);
    			}
				Message msg = Message.obtain(mUiHandler, UPDATE_FAV_IMAGE, mBeFavorited);
				mUiHandler.sendMessageDelayed(msg, 100);
			}
			Log.d(TAG, "[NonUihandler][doCheckMarked] + End");
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
						Toast.makeText(parentActivity, parentActivity.getResources().getString(R.string.toast_download_failed), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "[NonUihandler][doPlayAudio] + End");
		}
	}
	
    public static void showDialog(int dialogType, boolean bSusscuss) {
    	switch (dialogType)
		{
			case MyDialogFragment.ADD_TO_BOOKMARK:
				Log.d(TAG, "[showDialog] - ADD_TO_BOOKMARK");
				mCurrentDialog = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.ADD_TO_BOOKMARK, false, null);
				mCurrentDialog.show(parentActivity.getSupportFragmentManager(), "dialog_add");
				break;
			case MyDialogFragment.ADD_RESULT:
				Log.d(TAG, "[showDialog] - ADD_RESULT");
				mCurrentDialog = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.ADD_RESULT, bSusscuss, null);
				mCurrentDialog.show(parentActivity.getSupportFragmentManager(), "dialog_add_result");
				break;
			case MyDialogFragment.REMOVE_FROM_BOOKMARK:
				Log.d(TAG, "[showDialog] - REMOVE_FROM_BOOKMARK");
				mCurrentDialog = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.REMOVE_FROM_BOOKMARK, false, null);
				mCurrentDialog.show(parentActivity.getSupportFragmentManager(), "dialog_remove");
				break;
			case MyDialogFragment.REMOVE_RESULT:
				Log.d(TAG, "[showDialog] - REMOVE_RESULT");
				mCurrentDialog = MyDialogFragment.newInstance(parentActivity, MyDialogFragment.REMOVE_RESULT, bSusscuss, null);
				mCurrentDialog.show(parentActivity.getSupportFragmentManager(), "dialog_remove_result");
				break;
			default:
				Log.d(TAG, "[showDialog] Something wrong in handleMessage()");
				break;
		}
    }
    
    public static void doPositiveClick(int dialogType) {
    	switch (dialogType)
		{
			case MyDialogFragment.ADD_TO_BOOKMARK:
				Log.d(TAG, "[doPositiveClick] - ADD_TO_BOOKMARK");
				sendHandlerMsg(ADD_TO_BOOKMARK);
				break;
			case MyDialogFragment.REMOVE_FROM_BOOKMARK:
				Log.d(TAG, "[doPositiveClick] - REMOVE_FROM_BOOKMARK");
				sendHandlerMsg(REMOVE_FROM_BOOKMARK);
				break;
			case MyDialogFragment.REMOVE_RESULT:
			case MyDialogFragment.ADD_RESULT:
				Log.d(TAG, "[doPositiveClick] - REMOVE_RESULT or ADD_RESULT");
				mCurrentDialog.dismiss();
				break;
			default:
				Log.d(TAG, "[doPositiveClick] Something wrong in handleMessage()");
				break;
		}
    }
    
    public static void doNegativeClick() {
        // Do stuff here.
        Log.i(TAG, "Negative click!");
        //mDialogType = -1;
        //mBeFavorited = false;
    }
	
	public static void updateDataFragment() {
		//Add Detail Dialog Fragment
		FragmentManager fm = parentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ResultDemoFragment list = (ResultDemoFragment) fm.findFragmentById(R.id.result_detail_demo_listview);
        // Create the list fragment and add it as our sole content.
        if (list == null) {
        	Log.d(TAG, "[onViewCreated] new ResultDemoFragment, do add");
        	list = new ResultDemoFragment();
			ft.add(R.id.result_detail_demo_listview, list);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        } else {
        	Log.d(TAG, "[onViewCreated] used ResultDemoFragment, do replace");
        	list = new ResultDemoFragment();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.result_detail_demo_listview, list);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
	public static void updateFavoriteImg(boolean mFavorited) {
		Log.d(TAG, "[updateFavoriteImg] This item has been bookmarked = "+mFavorited);
		if (mFavorited) {
			mFavoriteImg.setImageResource(R.drawable.btn_mark_on);
		} else {
			mFavoriteImg.setImageResource(R.drawable.btn_mark_off);
		}
	}
	
	private static void sendHandlerMsg(int msgCode) {
   		if (mNonUiHandler != null) {
			if (mNonUiHandler.hasMessages(msgCode)) {
				mNonUiHandler.removeMessages(msgCode);
			}
			mNonUiHandler.sendEmptyMessage(msgCode);
		}
	}
	
	public static class ResultDemoFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<Dictionary> {
		
		// This is the Adapter being used to display the list's data.
		ResultDemoAdapter mAdapter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[ResultDemoFragment][onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);
			
			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			this.setEmptyText("");
	
			// We have a menu item to show in action bar.
			this.setHasOptionsMenu(false);
	
			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new ResultDemoAdapter(this.getActivity());
			this.setListAdapter(mAdapter);
			
			// Start out with a progress indicator.
			this.setListShown(false);
			
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			this.getLoaderManager().initLoader(0, null, this);
		}
	
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			Log.d(TAG, "[ResultDemoFragment][onCreateOptionsMenu] + Begin");
			// Since we no need action bar and menu,Choose close
			menu.close();
		}
	
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "[ResultDemoAdapter][onListItemClick] Item clicked: " + id);
			
		}
	
		public Loader<Dictionary> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[ResultDemoFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new ResultDemoLoader(this.getActivity());
		}
	
		public void onLoadFinished(Loader<Dictionary> loader, Dictionary data) {
			Log.d(TAG, "[ResultDemoFragment][onLoadFinished] + Begin");
			// Set the new data in the adapter.
			List<Dictionary> sample = new ArrayList<Dictionary>();
			if (data == null) {
				if (mUpdateType == UPDATE_NEXT) {
					Log.i(TAG, "[onLoadFinished] data is null, UpdataType is UPDATE_NEXT");
					mBtnNext.setEnabled(false);
					mBtnNext.setClickable(false);
					mBtnPrev.setEnabled(true);
					mBtnPrev.setClickable(true);
				} else if (mUpdateType == UPDATE_PREV) {
					Log.i(TAG, "[onLoadFinished] mCurrentDic is null, UpdataType is UPDATE_PREV");
					mBtnPrev.setEnabled(false);
					mBtnPrev.setClickable(false);
					mBtnNext.setEnabled(true);
					mBtnNext.setClickable(true);
				}
			} else {
				mCurrentDic = data;
				mBtnPrev.setEnabled(true);
				mBtnPrev.setClickable(true);
				mBtnNext.setEnabled(true);
				mBtnNext.setClickable(true);
			}
			
			if (mCurrentDic != null) {
				TextView TextView1 = (TextView) parentActivity.findViewById(R.id.result_detail_des_first_line);
				TextView TextView2 = (TextView) parentActivity.findViewById(R.id.result_detail_des_second_line_text);
				if (mCurrentDic.getLanguage_dir().equalsIgnoreCase("en2sc")) {
					Log.i(TAG, "[onLoadFinished] is english dictionary");
					TextView1.setText(mCurrentDic.getKeyword());
					TextView2.setText(mCurrentDic.getDestiontion());
				} else {
					Log.i(TAG, "[onLoadFinished] is chinese dictionary");
					TextView1.setText(mCurrentDic.getDestiontion());
					TextView2.setText(mCurrentDic.getKeyword());
				}
				TextView TextView3 = (TextView) parentActivity.findViewById(R.id.result_detail_des_third_line);
				TextView3.setText(mCurrentDic.getChineser_tone_py());
				TextView TextViewSourceLabel = (TextView) parentActivity.findViewById(R.id.result_detail_des_source_text);
				TextViewSourceLabel.setText(R.string.dic_detail_source_text);
				TextView TextView1SourceInfo = (TextView) parentActivity.findViewById(R.id.result_detail_des_source_info);
				TextView1SourceInfo.setText(mCurrentDic.getDic_catagory());
				
				String strEn = mCurrentDic.getSample_sentance_en();
				String strCN = mCurrentDic.getSample_sentence_ch();
				String strPY = mCurrentDic.getSample_sentance_py();
				if (strEn != null && !strEn.equalsIgnoreCase(" ")) {
					sample.add(mCurrentDic);
				}
				if (strCN != null && !strCN.equalsIgnoreCase(" ")) {
					sample.add(mCurrentDic);
				}
				if (strPY != null && !strPY.equalsIgnoreCase(" ")) {
					sample.add(mCurrentDic);
				}
			} 
			
			//Reset update type
			mUpdateType = UPDATE_CURRENT;
			
			sendHandlerMsg(CHECK_FAV_STATUS);
			
			//Cancel default divider
			ListView mListView = this.getListView();
			mListView.setDivider(null);
			mListView.setScrollingCacheEnabled(false);
			
		    mAdapter.setData(sample);
	
			// The list should now be shown.
			if (this.isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
		}
	
		public void onLoaderReset(Loader<Dictionary> loader) {
			Log.d(TAG, "[ResultDemoFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class ResultDemoLoader extends AsyncTaskLoader<Dictionary> {

		Dictionary mDictionary;
		
	    public ResultDemoLoader(Context context) {
	        super(context);
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public Dictionary loadInBackground() {
	    	Log.d(TAG, "[ResultDemoLoader][loadInBackground] + Begin");
	    	Dictionary entries = null;
	    	if (mUpdateType == UPDATE_CURRENT) {
	    		entries = mCurrentDic;
	    	} else if (mUpdateType == UPDATE_NEXT) {
	    		entries = MainActivity.dbManagerment.getNextDictionary(mCurrentDic.getId());
	    	} else if (mUpdateType == UPDATE_PREV) {
	    		entries = MainActivity.dbManagerment.getPreviousDictionary(mCurrentDic.getId());
	    	}
	        Log.d(TAG, "[ResultDemoLoader][loadInBackground] + End");
	        // Done!
	        return entries;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(Dictionary dic) {
	    	Log.d(TAG, "[ResultDemoLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (dic != null) {
	                this.onReleaseResources(dic);
	            }
	        }
	        
	        Dictionary oldDictionary = dic;
	        mDictionary = dic;

	        if (this.isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(dic);
	        }
	        
	        // At this point we can release the resources associated with
	        // 'oldScenarioList' if needed; now that the new result is delivered we
	        // know that it is no longer in use.
	        if (oldDictionary != null) {
	            this.onReleaseResources(oldDictionary);
	        }
	        Log.d(TAG, "[ResultDemoLoader][deliverResult] + End");
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	    	Log.d(TAG, "[ScenarioListLoader][onStartLoading] + Begin");
	        if (mDictionary != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            this.deliverResult(mDictionary);
	        }


	        if (takeContentChanged() || mDictionary == null) {
	            // If the data has changed since the last time it was loaded
	            // or is not currently available, start a load.
	            this.forceLoad();
	        }
	        Log.d(TAG, "[ScenarioListLoader][onStartLoading] + End");
	    }

	    /**
	     * Handles a request to stop the Loader.
	     */
	    @Override 
	    protected void onStopLoading() {
	    	Log.d(TAG, "[ScenarioListLoader][onStopLoading] + Begin");
	        // Attempt to cancel the current load task if possible.
	        this.cancelLoad();
	    }

	    /**
	     * Handles a request to cancel a load.
	     */
	    @Override 
	    public void onCanceled(Dictionary dic) {
	    	Log.d(TAG, "[ResultDemoLoader][onCanceled] + Begin");
	        super.onCanceled(dic);

	        // At this point we can release the resources associated with 'scenarioList' if needed.
	        this.onReleaseResources(dic);
	    }

	    /**
	     * Handles a request to completely reset the Loader.
	     */
	    @Override 
	    protected void onReset() {
	    	Log.d(TAG, "[ScenarioListLoader][onReset] + Begin");
	        super.onReset();

	        // Ensure the loader is stopped
	        this.onStopLoading();

	        // At this point we can release the resources associated with 'scenarioList' if needed.
	        if (mDictionary != null) {
	            this.onReleaseResources(mDictionary);
	            mDictionary = null;
	        }
	    }

	    /**
	     * Helper function to take care of releasing resources associated
	     * with an actively loaded data set.
	     */
	    protected void onReleaseResources(Dictionary data) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}
	
	public static class ResultDemoAdapter extends ArrayAdapter<Dictionary> {
	    private final LayoutInflater mInflater;

	    public ResultDemoAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	    	super(context, R.layout.result_detail_list_item);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<Dictionary> data) {
	    	Log.d(TAG, "[ResultDemoAdapter][setData] + Begin");
	        this.clear();
	        if (data != null) {
	        	Log.d(TAG, "[ResultDemoAdapter][setData] Size = "+data.size());
	            for (Dictionary dicEntry : data) {
	                this.add(dicEntry);
	            }
	        }
	        Log.d(TAG, "[ResultDemoAdapter][setData] + End");
	    }
	    
	    /**
		 * Populate new items in the list.
		 */
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "[ResultDemoAdapter][getView] + pos="+position);
	        View view;
			
	        if (convertView == null) {
        		Log.d(TAG, "convertView == null, new result_detail_list_item");
        		view = mInflater.inflate(R.layout.result_detail_list_item, parent, false);
	        } else {
	            view = convertView;
	        }
	        
	        TextView TextLine = (TextView)view.findViewById(R.id.result_detail_listitem_text);
	        ImageView speakIcon = (ImageView)view.findViewById(R.id.result_detail_listitem_icon);

	        final Dictionary item = this.getItem(position);
			if (item != null) {
				switch(position) 
				{
					case 0:
					{
						String sectenceStr = item.getSample_sentance_en();
			            String hightStr = item.getKeyword();
			            SpannableString spanStr = Utils.getSpanableText(sectenceStr, hightStr);
						if (spanStr != null) {
							TextLine.setText(spanStr);
						}
						
						speakIcon.setVisibility(View.INVISIBLE);
						break;
					}
					case 1:
					{
						speakIcon.setVisibility(View.VISIBLE);
						speakIcon.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String sentanceAudio = item.getSample_sentance_audio();
								Log.d(TAG, "[onClick] sentanceAudio = "+sentanceAudio);
						   		if (mNonUiHandler != null) {
									if (mNonUiHandler.hasMessages(PLAY_SENTANCE_AUDIO)) {
										mNonUiHandler.removeMessages(PLAY_SENTANCE_AUDIO);
									}
									Message msg = Message.obtain(mNonUiHandler, PLAY_SENTANCE_AUDIO,sentanceAudio);
									mNonUiHandler.sendMessage(msg);
								}
							}
						});
						
						String sectenceStr = item.getSample_sentence_ch();
			            String hightStr = item.getDestiontion();
			            SpannableString spanStr = Utils.getSpanableText(sectenceStr, hightStr);
						if (spanStr != null) {
							TextLine.setText(spanStr);
						}
						break;
					}
					case 2:
					{
						String sectenceStr = item.getSample_sentance_py();
			            String hightStr = item.getChineser_tone_py();
			            SpannableString spanStr = Utils.getSpanableText(sectenceStr, hightStr);
						if (spanStr != null) {
							TextLine.setText(spanStr);
						}
						
						speakIcon.setVisibility(View.INVISIBLE);
						break;
					}
				}
			}
			return view;
	    }
	}
}



