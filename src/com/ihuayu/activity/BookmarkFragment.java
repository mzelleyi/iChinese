/**
 * 
 */
package com.ihuayu.activity;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ihuayu.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Kesen
 *
 */
public class BookmarkFragment extends Fragment {

	private static final String TAG = "iHuayu:BookmarkFragment";
	private static FragmentActivity parentActivity = null;

    /**
     * Create a new instance of SearchFragment
     */
    static BookmarkFragment newInstance( ) {
    	Log.d(TAG, "[newInstance] + Begin");
    	BookmarkFragment fragment = new BookmarkFragment();

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
        View v = inflater.inflate(R.layout.bookmark_fragment, container, false);
        return v;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "[onViewCreated] + Begin");
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		parentActivity = this.getActivity();
		
		FragmentManager fm = parentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		BookmarkListFragment list = (BookmarkListFragment) fm.findFragmentById(R.id.fragment_bookmark_listview);
        // Create the list fragment and add it as our sole content.
        if (list == null) {
        	Log.d(TAG, "[onViewCreated] new BookmarkListFragment, do add");
        	list = new BookmarkListFragment();
			ft.add(R.id.fragment_bookmark_listview, list);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
        } else {
        	Log.d(TAG, "[onViewCreated] used BookmarkListFragment, do replace");
        	list = new BookmarkListFragment();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.fragment_bookmark_listview, list);
			//ft.addToBackStack(null);
			ft.commit();
        }
	}
	
	public static class BookmarkListFragment extends ListFragment implements 
		LoaderManager.LoaderCallbacks<List<BookmarkEntry>> {
		
		// This is the Adapter being used to display the list's data.
		BookmarkListAdapter mAdapter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d(TAG, "[BookmarkListFragment][onActivityCreated] + Begin");
			super.onActivityCreated(savedInstanceState);
	
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
	
		public Loader<List<BookmarkEntry>> onCreateLoader(int id, Bundle args) {
			Log.d(TAG, "[BookmarkListFragment][onCreateLoader] + Begin");
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new BookmarkListLoader(this.getActivity());
		}
	
		public void onLoadFinished(Loader<List<BookmarkEntry>> loader, List<BookmarkEntry> data) {
			Log.d(TAG, "[BookmarkListFragment][onLoadFinished] + Begin");
			// Set the new data in the adapter.
			mAdapter.setData(data);
	
			// The list should now be shown.
			if (this.isResumed()) {
				this.setListShown(true);
			} else {
				this.setListShownNoAnimation(true);
			}
		}
	
		public void onLoaderReset(Loader<List<BookmarkEntry>> loader) {
			Log.d(TAG, "[BookmarkListFragment][onLoaderReset] + Begin");
			// Clear the data in the adapter.
			mAdapter.setData(null);
		}
	}
	
	/**
	 * This class holds the per-item data in our Loader.
	 */
	public static class BookmarkEntry {
	    public BookmarkEntry(BookmarkListLoader loader, ApplicationInfo info) {
	        mLoader = loader;
	        mInfo = info;
	        mApkFile = new File(info.sourceDir);
	    }
	    
	    public BookmarkEntry(ApplicationInfo info) {
	    	mLoader = null;
	        mInfo = info;
	        mApkFile = new File(info.sourceDir);
	    }

	    public ApplicationInfo getApplicationInfo() {
	        return mInfo;
	    }

	    public String getLabel() {
	        return mLabel;
	    }
	    
	    public Drawable getIcon() {
	        if (mIcon == null) {
	            if (mApkFile.exists()) {
	                mIcon = mInfo.loadIcon(mLoader.mPm);
	                return mIcon;
	            } else {
	                mMounted = false;
	            }
	        } else if (!mMounted) {
	            // If the Bookmark wasn't mounted but is now mounted, reload
	            // its icon.
	            if (mApkFile.exists()) {
	                mMounted = true;
	                mIcon = mInfo.loadIcon(mLoader.mPm);
	                return mIcon;
	            }
	        } else {
	            return mIcon;
	        }

	        return mLoader.getContext().getResources().getDrawable(
	                android.R.drawable.sym_def_app_icon);
	    }

	    @Override 
	    public String toString() {
	        return mLabel;
	    }

	    void loadLabel(Context context) {
	        if (mLabel == null || !mMounted) {
	            if (!mApkFile.exists()) {
	                mMounted = false;
	                mLabel = mInfo.packageName;
	            } else {
	                mMounted = true;
	                CharSequence label = mInfo.loadLabel(context.getPackageManager());
	                mLabel = label != null ? label.toString() : mInfo.packageName;
	            }
	        }
	    }

	    private final BookmarkListLoader mLoader;
	    private final ApplicationInfo mInfo;
	    private final File mApkFile;
	    private String mLabel;
	    private boolean misDivider = false;
	    private Drawable mIcon;
	    private boolean mMounted;
	}
	
	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class BookmarkListLoader extends AsyncTaskLoader<List<BookmarkEntry>> {
	    final PackageManager mPm;

	    List<BookmarkEntry> mBookmarkList;

	    public BookmarkListLoader(Context context) {
	        super(context);

	        // Retrieve the package manager for later use; note we don't
	        // use 'context' directly but instead the save global application
	        // context returned by getContext().
	        mPm = getContext().getPackageManager();
	    }

	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<BookmarkEntry> loadInBackground() {
	    	Log.d(TAG, "[BookmarkListLoader][loadInBackground] + Begin");
	        // Retrieve all known applications.
	        List<ApplicationInfo> apps = mPm.getInstalledApplications(
	                PackageManager.GET_UNINSTALLED_PACKAGES |
	                PackageManager.GET_DISABLED_COMPONENTS);
	        if (apps == null) {
	            apps = new ArrayList<ApplicationInfo>();
	        }

	        final Context context = getContext();

	        // Create corresponding array of entries and load their labels.
	        List<BookmarkEntry> entries = new ArrayList<BookmarkEntry>(apps.size());
	        for (int i=0; i<apps.size(); i++) {
	        	BookmarkEntry entry = new BookmarkEntry(this, apps.get(i));
	            entry.loadLabel(context);
	            entries.add(entry);
	        }

	        // Sort the list.
	        Collections.sort(entries, ALPHA_COMPARATOR);
	        
	        Log.d(TAG, "[BookmarkListLoader][loadInBackground] List Size ="+entries.size());
	        Log.d(TAG, "[BookmarkListLoader][loadInBackground] + End");
	        // Done!
	        return entries;
	    }

	    /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(List<BookmarkEntry> BookmarkList) {
	    	Log.d(TAG, "[BookmarkListLoader][deliverResult] + Begin");
	        if (this.isReset()) {
	            // An async query came in while the loader is stopped.  We
	            // don't need the result.
	            if (BookmarkList != null) {
	                this.onReleaseResources(BookmarkList);
	            }
	        }
	        List<BookmarkEntry> oldBookmarkList = BookmarkList;
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
	    public void onCanceled(List<BookmarkEntry> BookmarkList) {
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
	    protected void onReleaseResources(List<BookmarkEntry> BookmarkList) {
	        // For a simple List<> there is nothing to do.  For something
	        // like a Cursor, we would close it here.
	    }
	}

	public static class BookmarkListAdapter extends ArrayAdapter<BookmarkEntry> {
	    private final LayoutInflater mInflater;
	    
	    public BookmarkListAdapter(Context context) {
	        //super(context, android.R.layout.simple_list_item_2);
	        //super(context, R.layout.Bookmark_list_item);
	    	super(context, R.layout.bookmark_list_item);
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setData(List<BookmarkEntry> data) {
	    	Log.d(TAG, "[BookmarkListAdapter][setData] + Begin");
	        this.clear();
	        if (data != null) {
	        	Log.d(TAG, "[BookmarkListAdapter][setData] Size = "+data.size());
	        	BookmarkEntry firstEntry = data.get(0);
	        	String temp = String.valueOf(firstEntry.getLabel().charAt(0));
	        	
	        	//New First Entry As Divider
	        	BookmarkEntry firstAddEntry = new BookmarkEntry(data.get(0).getApplicationInfo());
	        	firstAddEntry.loadLabel(this.getContext());
	        	firstAddEntry.misDivider = true;
	        	Log.d(TAG, "[BookmarkListAdapter] add isDivider = "+firstAddEntry.misDivider+
	        			"; entry = "+firstAddEntry.getLabel());
            	this.add(firstAddEntry);
            	
	            for (int i = 0; i < data.size() ; i++) {
	            	Log.d(TAG, "[BookmarkListAdapter] i = "+i);
	            	//Add char flag for divider
	            	BookmarkEntry entry = data.get(i);
	            	Character thisChar = entry.getLabel().charAt(0);
	            	if(String.valueOf(thisChar).equalsIgnoreCase(temp)) {
	    	        	Log.d(TAG, "[BookmarkListAdapter] add isDivider = "+entry.misDivider+
	    	        			"; entry = "+entry.getLabel());
	            		this.add(entry);
	            	} else {
	            		temp = String.valueOf(thisChar);
	            		
	            		//New duplicate fist char entry as list item
	            		BookmarkEntry addEntry = new BookmarkEntry(data.get(i).getApplicationInfo());
	    	        	addEntry.loadLabel(this.getContext());
	    	        	addEntry.misDivider = true;
	    	        	Log.d(TAG, "[BookmarkListAdapter] add isDivider = "+addEntry.misDivider+
	    	        			"; entry = "+addEntry.getLabel());
	                	this.add(addEntry);
		            	
	                	//Add 
	    	        	Log.d(TAG, "[BookmarkListAdapter] add isDivider = "+entry.misDivider+
	    	        			"; entry = "+entry.getLabel());
		            	this.add(entry);
	            	}
	            }
	        }
	        Log.d(TAG, "[BookmarkListAdapter][setData] + End");
	    }
	    
		class ViewHolder
		{
			public ImageView	listImageIcon	= null;
			public TextView		listTextView1	= null;
			public TextView		listTextView2	= null;
			public TextView		listTextView3	= null;
			public TextView     listTextType    = null;
		}

	    /**
		 * Populate new items in the list.
		 */
	    @Override 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "[BookmarkListAdapter][getView] + pos = "+position);
	        //View view = null;
			//ViewHolder holder = null;
			TextView 	separatorText = null;
			ImageView	listImageIcon	= null;
			TextView	listTextView1	= null;
			TextView	listTextView2	= null;
			TextView	listTextView3	= null;
			TextView    listTextType    = null;
			final BookmarkEntry item = this.getItem(position);
			Log.d(TAG, "[BookmarkListAdapter][getView] isDivider = "+item.misDivider+"; label = "+item.getLabel());
			if (item != null) {				
				if (convertView == null) {
					Log.d(TAG, "convertView == null");
					if (item.misDivider) {
						Log.d(TAG, "inflate then find bookmark_list_separate");
						convertView = mInflater.inflate(R.layout.bookmark_list_separate, parent, false);
						separatorText = (TextView) convertView.findViewById(R.id.bookmark_listitem_separate_text);
					} else {
						Log.d(TAG, "inflate then find bookmark_list_item");
						convertView = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
						listImageIcon = (ImageView) convertView.findViewById(R.id.bookmark_listitem_icon_speaker);
						listTextView1 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_first_line);
						listTextView2 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_second_line);
						listTextView3 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_third_line);
						listTextType = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_type);
					}
				} else {
					Log.d(TAG, "convertView £¡= null");
					if (item.misDivider) {
						Log.d(TAG, "inflate and find separatorText");
						convertView = mInflater.inflate(R.layout.bookmark_list_separate, parent, false);
						separatorText = (TextView) convertView.findViewById(R.id.bookmark_listitem_separate_text);
					} else {
						Log.d(TAG, "inflate and find bookmark_list_item");
						convertView = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
						listImageIcon = (ImageView) convertView.findViewById(R.id.bookmark_listitem_icon_speaker);
						listTextView1 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_first_line);
						listTextView2 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_second_line);
						listTextView3 = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_third_line);
						listTextType = (TextView) convertView.findViewById(R.id.bookmark_listitem_text_type);
					}
				}
				
				if (null != convertView) {
					if (item.misDivider) {
						if (separatorText != null) {
							Log.d(TAG, "set Separator Text");
							separatorText.setText(String.valueOf(item.getLabel().charAt(0)));
						}
					} else {
						Log.d(TAG, "set ListItem Data");
						if (listImageIcon != null) {
							listImageIcon.setImageDrawable(item.getIcon());
							listImageIcon.setOnClickListener(new View.OnClickListener()	{
								@Override
								public void onClick(View v)
								{
									// TODO Auto-generated method stub
								}
							});
						}
						if (listTextView1 != null) {
							listTextView1.setText(item.getLabel());
						}
						if (listTextView2 != null) {
							listTextView2.setText(item.getLabel());
						}
						if (listTextView3 != null) {
							listTextView3.setText(item.getLabel());
						}
						if (listTextType != null) {
							listTextType.setText("Test");
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
	public static final Comparator<BookmarkEntry> ALPHA_COMPARATOR = new Comparator<BookmarkEntry>() {
	    private final Collator sCollator = Collator.getInstance();
	    public int compare(BookmarkEntry object1, BookmarkEntry object2) {
	        return sCollator.compare(object1.getLabel(), object2.getLabel());
	    }
	};
}
