/**
 * 
 */
package com.ihuayu.activity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ihuayu.R;
import com.ihuayu.activity.db.entity.Dictionary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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

/**
 * @author Kesen
 *
 */
public class BookmarkFragment extends Fragment {

	private static final String			TAG						= "iHuayu:BookmarkFragment";
	private static final int			VIEW_TYPE_NORMAL		= 0;
	private static final int			VIEW_TYPE_DIVIDER		= 1;
	private static FragmentActivity		mParentActivity			= null;
	private static LayoutInflater	    mInflater				= null;
	private static Resources			mRes					= null;
	private BookmarkListFragment		mBookmarkListFragment	= null;

    /**
     * Create a new instance of SearchFragment
     */
    static BookmarkFragment newInstance( ) {
    	Log.d(TAG, "[newInstance] + Begin");
    	BookmarkFragment fragment = new BookmarkFragment();
        return fragment;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "[onCreate] + Begin");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		mParentActivity = this.getActivity();
		mRes = mParentActivity.getResources();
		
		FragmentManager fm = mParentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mBookmarkListFragment = (BookmarkListFragment) fm.findFragmentById(R.id.fragment_bookmark_listview);
		
		//Set Edit Button On Click Listener
		final Button editBtn = (Button)mParentActivity.findViewById(R.id.bookmark_titlebar_btn_edit);
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
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.fragment_bookmark_listview, mBookmarkListFragment);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
	public static class BookmarkListFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<Dictionary>> {
		
		// This is the Adapter being used to display the list's data.
		BookmarkListAdapter mAdapter;
		
		//Handle indicate case.
		ListView mListView = null;
	    private RemoveWindow mRemoveWindow = new RemoveWindow();
	    private Handler mHandler = new Handler();
	    private WindowManager mWindowManager;
	    private TextView mDialogText;
	    private boolean mShowing;
	    private boolean mReady;
	    private char mPrevLetter = Character.MIN_VALUE;
	    //List<Character> sDividerCharList = new ArrayList<Character>();
		
		//The origin data that get from DB.
		List<Dictionary> mOriginBookmarkList = null;
		//The data that fill with divider elements.
		List<Dictionary> mWithDividerList = new ArrayList<Dictionary>();
		//The list used to keep need remove list normal item.
		List<Dictionary> mRemoveBookmarkList = new ArrayList<Dictionary>();
		//The list used to keep need remove list divider item.
		List<Dictionary> mRemoveDividerList = new ArrayList<Dictionary>();
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[BookmarkListFragment][onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);
			
			mInflater = (LayoutInflater)mParentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        mWindowManager = (WindowManager)mParentActivity.getSystemService(Context.WINDOW_SERVICE);
	        
			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			this.setEmptyText("No Bookmarks");
	
			// We have a menu item to show in action bar.
			this.setHasOptionsMenu(false);
	
			// Create an empty adapter we will use to display the loaded data.
			mAdapter = new BookmarkListAdapter(this.getActivity());
			this.setListAdapter(mAdapter);
	
			// Start out with a progress indicator.
			this.setListShown(true);
	
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			this.getLoaderManager().initLoader(0, null, this);
			Log.d(TAG, "[BookmarkListFragment][onActivityCreated] + End");
		}
		
	    @Override
		public void onResume() {
	        super.onResume();
	        mReady = true;
	    }

	    
	    @Override
	    public void onPause() {
	        super.onPause();
	        removeWindow();
	        mReady = false;
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        mWindowManager.removeView(mDialogText);
	        mReady = false;
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
			Log.i(TAG, "[BookmarkListFragment][onListItemClick] Item clicked: " + id);
			
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
			mOriginBookmarkList = data;
			
//			mOriginBookmarkList = new ArrayList<Dictionary>(data.size());
//	        for (int i=0; i<data.size(); i++) {
//	            mOriginBookmarkList.add(data.get(i));
//	        }
//			mWithDividerList = new ArrayList<Dictionary>(data.size());
//			for (int i = 0; i < data.size(); i++) {
//				mWithDividerList.add(data.get(i));
//			}
	        
			if (data.size() > 0) {
				Dictionary firstEntry = data.get(0);
	        	String temp = String.valueOf(firstEntry.getKeyword().charAt(0));
	        	
	        	//New First Entry As Divider
	        	Dictionary firstAddEntry = new Dictionary();
	        	firstAddEntry.setKeyword(data.get(0).getKeyword());
	        	firstAddEntry.setId(data.get(0).getId());
	        	firstAddEntry.setIsDivider(true);
	        	mWithDividerList.add(firstAddEntry);
	        	
	        	//Add to dividers list
	        	//sDividerCharList.add(firstAddEntry.getLabel().charAt(0));
	        	Log.d(TAG, "add isDivider = "+firstAddEntry.getIsDivider()+"; entry = "+firstAddEntry.getKeyword());
	            for (int i = 0; i < data.size() ; i++) {
	            	Log.d(TAG, " i = "+i);
	            	//Add char flag for divider
	            	Dictionary entry = data.get(i);
	            	Character thisChar = entry.getKeyword().charAt(0);
	            	if(String.valueOf(thisChar).equalsIgnoreCase(temp)) {
	    	        	Log.d(TAG, "add isDivider = "+entry.getIsDivider()+	"; entry = "+entry.getKeyword());
	    	        	mWithDividerList.add(entry);
	    	        	//sDividerChars[i] = thisChar;
	            	} else {
	            		temp = String.valueOf(thisChar);
	            		
	            		//New duplicate fist char entry as list item
	            		//Dictionary addEntry = new Dictionary(data.get(i).getApplicationInfo());
	                   	Dictionary addEntry = new Dictionary();
	                   	addEntry.setKeyword(data.get(i).getKeyword());
	                   	addEntry.setId(data.get(i).getId());
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
	        mHandler.post(new Runnable() {
	            public void run() {
	                mReady = true;
	                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
	                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                        WindowManager.LayoutParams.TYPE_APPLICATION,
	                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
	                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
	                        PixelFormat.TRANSLUCENT);
	                mWindowManager.addView(mDialogText, lp);
	            }});
			
			mListView = this.getListView();
			if (null != mListView) {
				mListView.setOnScrollListener(new AbsListView.OnScrollListener()
				{
					
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState)
					{
						// TODO Auto-generated method stub
						Log.d(TAG, "[onScrollStateChanged] + scrollState = "+scrollState);
						
					}
					
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
					{
						// TODO Auto-generated method stub
						Log.d(TAG, "[onScroll] + firstVisibleItem = "+firstVisibleItem
								+",visibleItemCount = "+visibleItemCount+",totalItemCount = "+totalItemCount);
				        if (mReady) {
				        	if (mWithDividerList.size() > 0) {
				        		char firstLetter = mWithDividerList.get(firstVisibleItem).getKeyword().charAt(0);
					            
					            if (!mShowing && firstLetter != mPrevLetter) {
					                mShowing = true;
					                mDialogText.setVisibility(View.VISIBLE);
					            }
					            mDialogText.setText(((Character)firstLetter).toString());
					            mHandler.removeCallbacks(mRemoveWindow);
					            mHandler.postDelayed(mRemoveWindow, 2000);
					            mPrevLetter = firstLetter;
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
		
	    private void removeWindow() {
	        if (mShowing) {
	            mShowing = false;
	            mDialogText.setVisibility(View.INVISIBLE);
	        }
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
//			for (int i = 0 ; i < mAdapter.getCount(); i++) {
//				Log.d(TAG, "[BookmarkListFragment] i = "+i);
//				Dictionary entry = mAdapter.getItem(i);
//				if (entry.mNeedDelete) {
//					Log.d(TAG, "[BookmarkListFragment] remove item i ="+i);
//					mAdapter.remove(entry);
//					Log.d(TAG, "[BookmarkListFragment] now count ="+mAdapter.getCount());
//				}
//			}
//			Log.d(TAG, "[BookmarkListFragment] === Remove Unused Divider ===");
//			Log.d(TAG, "[BookmarkListFragment] Adapter.getCount = "+mAdapter.getCount());
//			for (int i = 0 ; i < mAdapter.getCount(); i++) {
//				Log.d(TAG, "[BookmarkListFragment] i = "+i);
//				Dictionary entry = mAdapter.getItem(i);
//				if (entry.misDivider) {
//		        	Dictionary nextEntry = mAdapter.getItem(i+1);
//		        	if (nextEntry.misDivider) {
//		        		Log.d(TAG, "[BookmarkListAdapter] remove divider i = "+i);
//			        	mAdapter.remove(entry);
//			        	Log.d(TAG, "[BookmarkListFragment] now count ="+mAdapter.getCount());
//		        	}
//				}
//			}
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
			MainActivity.dbManagerment.removeFromFavorites(removeId);
			
			Log.d(TAG, "[BookmarkListFragment][leaveEditMode] + End");
		}
	
		//The class used to show indicate for list view 
	    private final class RemoveWindow implements Runnable {
	        public void run() {
	            removeWindow();
	        }
	    }
	}
	
//	/**
//	 * This class holds the per-item data in our Loader.
//	 */
//	public static class Dictionary {
//	    public Dictionary(BookmarkListLoader loader, ApplicationInfo info) {
//	        mLoader = loader;
//	        mInfo = info;
//	        mApkFile = new File(info.sourceDir);
//	    }
//	    
//	    public Dictionary(ApplicationInfo info) {
//	    	mLoader = null;
//	        mInfo = info;
//	        mApkFile = new File(info.sourceDir);
//	    }
//
//	    public ApplicationInfo getApplicationInfo() {
//	        return mInfo;
//	    }
//
//	    public String getLabel() {
//	        return mLabel;
//	    }
//	    
//	    public Drawable getIcon() {
//	        if (mIcon == null) {
//	            if (mApkFile.exists()) {
//	                mIcon = mInfo.loadIcon(mLoader.mPm);
//	                return mIcon;
//	            } else {
//	                mMounted = false;
//	            }
//	        } else if (!mMounted) {
//	            // If the Bookmark wasn't mounted but is now mounted, reload
//	            // its icon.
//	            if (mApkFile.exists()) {
//	                mMounted = true;
//	                mIcon = mInfo.loadIcon(mLoader.mPm);
//	                return mIcon;
//	            }
//	        } else {
//	            return mIcon;
//	        }
//
//	        return mLoader.getContext().getResources().getDrawable(
//	                android.R.drawable.sym_def_app_icon);
//	    }
//
//	    @Override 
//	    public String toString() {
//	        return mLabel;
//	    }
//
//	    void loadLabel(Context context) {
//	        if (mLabel == null || !mMounted) {
//	            if (!mApkFile.exists()) {
//	                mMounted = false;
//	                mLabel = mInfo.packageName;
//	            } else {
//	                mMounted = true;
//	                CharSequence label = mInfo.loadLabel(context.getPackageManager());
//	                mLabel = label != null ? label.toString() : mInfo.packageName;
//	            }
//	        }
//	    }
//
//	    private final BookmarkListLoader mLoader;
//	    private final ApplicationInfo mInfo;
//	    private final File mApkFile;
//	    private String mLabel;
//	    private boolean misDivider = false;
//	    private boolean mNeedDelete = false;
//	    private Drawable mIcon;
//	    private boolean mMounted;
//	}
	
	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class BookmarkListLoader extends AsyncTaskLoader<List<Dictionary>> {
	    //final PackageManager mPm;

	    List<Dictionary> mBookmarkList;

	    public BookmarkListLoader(Context context) {
	        super(context);

	        // Retrieve the package manager for later use; note we don't
	        // use 'context' directly but instead the save global application
	        // context returned by getContext().
	        //mPm = getContext().getPackageManager();
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<Dictionary> loadInBackground() {
	    	Log.d(TAG, "[BookmarkListLoader][loadInBackground] + Begin");
	        // Retrieve all known applications.
//	        List<ApplicationInfo> apps = mPm.getInstalledApplications(
//	                PackageManager.GET_UNINSTALLED_PACKAGES |
//	                PackageManager.GET_DISABLED_COMPONENTS);
//	        if (apps == null) {
//	            apps = new ArrayList<ApplicationInfo>();
//	        }
//	        final Context context = getContext();
	    	
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
	    }
	}

	public static class BookmarkListAdapter extends ArrayAdapter<Dictionary> {
	    
	    private boolean bEditMode = false;
	    
	    public BookmarkListAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	        //super(context, R.layout.Bookmark_list_item);
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
	        Log.d(TAG, "[BookmarkListAdapter][setData] + End");
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
			ViewHolder holder = null;
			TextView 	separatorText = null;
//			ImageView	listImageIcon	= null;
//			TextView	listTextView1	= null;
//			TextView	listTextView2	= null;
//			TextView	listTextView3	= null;
//			TextView    listTextType    = null;
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
//						if (bEditMode) {
//							
////							ViewStub stub = (ViewStub) convertView.findViewById(R.id.bookmark_listitem_left_viewStub);
////							View inflated = stub.inflate();
//						    holder.listImageDelete  = (ImageView) convertView.findViewById(R.id.bookmark_listview_viewstub_img);
//						} else {
////							ViewStub stub = (ViewStub) convertView.findViewById(R.id.bookmark_listitem_left_viewStub);
////							if (stub != null) {
////								stub.setVisibility(View.GONE);
////								holder.listImageDelete = null;
////							}
//							holder.listImageDelete  = (ImageView) convertView.findViewById(R.id.bookmark_listview_viewstub_img);
//						}
						
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
						//convertView = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
//						listImageIcon = (ImageView) convertView.findViewById(R.id.bookmark_listitem_icon_speaker);
//						listTextView1 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_first_line);
//						listTextView2 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_second_line);
//						listTextView3 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_third_line);
//						listTextType = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_type);
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
//							ViewStub stub = (ViewStub) convertView.findViewById(R.id.bookmark_listitem_left_viewStub);
//							if (holder.listImageDelete == null) {
//								View inflated = stub.inflate();
//								holder.listImageDelete = (ImageView) inflated.findViewById(R.id.bookmark_listview_viewstub_img);
//							}
							
							deleteImg.setVisibility(View.VISIBLE);
							if (item.getNeedDelete()) {
								deleteImg.setImageResource(R.drawable.btn_clear_on);
							} else {
								deleteImg.setImageResource(R.drawable.btn_clear_rest);
							}
							
							deleteImg.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View arg0)
								{
									// TODO Auto-generated method stub
									if (item.getNeedDelete()) {
										deleteImg.setImageResource(R.drawable.btn_clear_rest);
										item.setNeedDelete(false);
									} else {
										deleteImg.setImageResource(R.drawable.btn_clear_on);
										item.setNeedDelete(true);
									}
								}
							});
						} else {
//							ViewStub stub = (ViewStub) convertView.findViewById(R.id.bookmark_listitem_left_viewStub);
//							if (stub != null) {
//								stub.setVisibility(View.GONE);
								holder.listImageDelete.setVisibility(View.GONE);
								item.setNeedDelete(false);
//							}
						}
	
						if (holder.listImageSpearker != null) {
							if (bEditMode) {
								holder.listImageSpearker.setImageResource(R.drawable.btn_speaker_sb);
								holder.listImageSpearker.setClickable(false);
							} else {
								holder.listImageSpearker.setImageResource(R.drawable.btn_speaker);
								holder.listImageSpearker.setOnClickListener(new View.OnClickListener()	{
									@Override
									public void onClick(View v)
									{
										// TODO Auto-generated method stub
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
	
	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
	public static final Comparator<Dictionary> ALPHA_COMPARATOR = new Comparator<Dictionary>() {
	    private final Collator sCollator = Collator.getInstance();
	    public int compare(Dictionary object1, Dictionary object2) {
	        return sCollator.compare(object1.getKeyword(), object2.getKeyword());
	    }
	};
}
